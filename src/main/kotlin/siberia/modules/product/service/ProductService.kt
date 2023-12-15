package siberia.modules.product.service

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import siberia.modules.auth.data.dto.AuthorizedUser
import siberia.modules.brand.data.dao.BrandDao
import siberia.modules.brand.data.dao.BrandDao.Companion.createRangeCond
import siberia.modules.category.data.dao.CategoryDao
import siberia.modules.collection.data.dao.CollectionDao
import siberia.modules.logger.data.models.SystemEventModel
import siberia.modules.product.data.dao.ProductDao
import siberia.modules.product.data.dto.*
import siberia.modules.product.data.dto.systemevents.ProductCreateEvent
import siberia.modules.product.data.dto.systemevents.ProductRemoveEvent
import siberia.modules.product.data.dto.systemevents.ProductUpdateEvent
import siberia.modules.product.data.models.ProductModel
import siberia.modules.stock.data.dao.StockDao.Companion.createLikeCond
import siberia.modules.stock.data.dao.StockDao.Companion.createListCond
import siberia.modules.user.data.dao.UserDao
import siberia.utils.kodein.KodeinService

class ProductService(di: DI) : KodeinService(di) {
    fun create(authorizedUser: AuthorizedUser, productCreateDto: ProductCreateDto): ProductFullOutputDto = transaction {
        val userDao = UserDao[authorizedUser.id]
        val event = ProductCreateEvent(userDao.login, productCreateDto.name, productCreateDto.vendorCode)

        val productDao = ProductDao.new {
            photo = productCreateDto.photo!!
            vendorCode = productCreateDto.vendorCode
            barcode = productCreateDto.barcode
            brand = if (productCreateDto.brand != null) BrandDao[productCreateDto.brand] else null
            name = productCreateDto.name
            description = productCreateDto.description
            purchasePrice = productCreateDto.purchasePrice
            distributorPrice = productCreateDto.distributorPrice
            professionalPrice = productCreateDto.professionalPrice
            commonPrice = productCreateDto.commonPrice
            category = if (productCreateDto.category != null) CategoryDao[productCreateDto.category] else null
            collection = if (productCreateDto.collection != null) CollectionDao[productCreateDto.collection] else null
            color = productCreateDto.color
            amountInBox = productCreateDto.amountInBox
            expirationDate = productCreateDto.expirationDate
            link = productCreateDto.link
//            Future iterations
//            size = productCreateDto.size
//            volume = productCreateDto.volume
        }

        SystemEventModel.logEvent(event)

        productDao.fullOutput()
    }

    fun update(authorizedUser: AuthorizedUser, productId: Int, productUpdateDto: ProductUpdateDto): ProductFullOutputDto = transaction {
        val userDao = UserDao[authorizedUser.id]
        val productDao = ProductDao[productId]

        productDao.loadUpdateDto(productUpdateDto)
        productDao.flush()
        val event = ProductUpdateEvent(userDao.login, productDao.name, productDao.vendorCode)
        SystemEventModel.logEvent(event)

        productDao.fullOutput()
    }

    fun remove(authorizedUser: AuthorizedUser, productId: Int): ProductRemoveResultDto = transaction {
        val userDao = UserDao[authorizedUser.id]
        val productDao = ProductDao[productId]

        productDao.delete()
        val event = ProductRemoveEvent(userDao.login, productDao.name, productDao.vendorCode)
        SystemEventModel.logEvent(event)

        ProductRemoveResultDto(
            success = true,
            message = "Product $productId (${productDao.vendorCode}) successfully removed"
        )
    }

    fun getByFilter(productSearchDto: ProductSearchDto): List<ProductOutputDto> = transaction {
        val searchFilterDto = productSearchDto.filters
        val paginationOutputDto = productSearchDto.pagination
        ProductDao.find {
            createRangeCond(searchFilterDto.amountInBox, (ProductModel.id neq 0), ProductModel.amountInBox, -1, Int.MAX_VALUE) and
            createRangeCond(searchFilterDto.commonPrice, (ProductModel.id neq 0), ProductModel.commonPrice, -1.0, Double.MAX_VALUE) and
            createRangeCond(searchFilterDto.distributorPrice, (ProductModel.id neq 0), ProductModel.distributorPrice, -1.0, Double.MAX_VALUE) and
            createRangeCond(searchFilterDto.professionalPrice, (ProductModel.id neq 0), ProductModel.professionalPrice, -1.0, Double.MAX_VALUE) and
            createListCond(searchFilterDto.brand, (ProductModel.id neq 0), ProductModel.brand)and
            createListCond(searchFilterDto.category, (ProductModel.id neq 0), ProductModel.category) and
            createListCond(searchFilterDto.collection, (ProductModel.id neq 0), ProductModel.collection)
            createLikeCond(searchFilterDto.name, (ProductModel.id neq 0), ProductModel.name) and
            createLikeCond(searchFilterDto.color, (ProductModel.id neq 0), ProductModel.color) and
            createLikeCond(searchFilterDto.vendorCode, (ProductModel.id neq 0), ProductModel.vendorCode) and
            createLikeCond(searchFilterDto.description, (ProductModel.id neq 0), ProductModel.description)

//            Future iterations
//            createRangeCond(searchFilterDto.size, (ProductModel.id neq 0), ProductModel.size, -1.0, Double.MAX_VALUE) and
//            createRangeCond(searchFilterDto.volume, (ProductModel.id neq 0), ProductModel.volume, -1.0, Double.MAX_VALUE) and
        }.limit(paginationOutputDto.n, paginationOutputDto.offset).map { it.toOutputDto() }
    }

    fun getOne(productId: Int): ProductFullOutputDto =
        ProductDao[productId].fullOutput()
}