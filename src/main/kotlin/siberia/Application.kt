package siberia

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import siberia.conf.AppConf
import siberia.modules.auth.controller.AuthController
import siberia.modules.rbac.data.models.role.RoleModel
import siberia.modules.rbac.data.models.rule.RuleCategoryModel
import siberia.modules.rbac.data.models.rule.RuleModel
import siberia.modules.auth.service.AuthService
import siberia.modules.brand.controller.BrandController
import siberia.modules.brand.data.models.BrandModel
import siberia.modules.brand.service.BrandService
import siberia.modules.category.controller.CategoryController
import siberia.modules.category.data.models.CategoryModel
import siberia.modules.category.data.models.CategoryToCategoryModel
import siberia.modules.category.service.CategoryService
import siberia.modules.collection.controller.CollectionController
import siberia.modules.collection.data.models.CollectionModel
import siberia.modules.collection.service.CollectionService
import siberia.modules.logger.controller.SystemEventController
import siberia.modules.logger.service.SystemEventService
import siberia.modules.product.data.models.ProductCategoryModel
import siberia.modules.product.data.models.ProductModel
import siberia.modules.stock.data.models.StockModel
import siberia.modules.stock.data.models.StockProductsModel
import siberia.modules.rbac.controller.RbacController
import siberia.modules.user.controller.UserController
import siberia.modules.user.data.models.UserModel
import siberia.modules.rbac.service.RbacService
import siberia.modules.user.service.UserAccessControlService
import siberia.plugins.*
import siberia.utils.database.DatabaseConnector
import siberia.utils.kodein.bindSingleton
import siberia.utils.kodein.kodeinApplication

fun main() {
    embeddedServer(Netty, port = AppConf.server.port, host = AppConf.server.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureCORS()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureExceptionFilter()

    kodeinApplication {
        bindSingleton { AuthService(it) }
        bindSingleton { UserAccessControlService(it) }
        bindSingleton { RbacService(it) }
        bindSingleton { SystemEventService(it) }
        bindSingleton { BrandService(it) }
        bindSingleton { CollectionService(it) }
        bindSingleton { CategoryService(it) }

        bindSingleton { AuthController(it) }
        bindSingleton { UserController(it) }
        bindSingleton { RbacController(it) }
        bindSingleton { CollectionController(it) }
        bindSingleton { BrandController(it) }
        bindSingleton { CategoryController(it) }
        bindSingleton { SystemEventController(it) }
    }

    DatabaseConnector(
        UserModel,
        RoleModel, RuleModel, RuleCategoryModel,
        StockModel, StockProductsModel,
        BrandModel, CollectionModel,
        CategoryModel, CategoryToCategoryModel,
        ProductModel, ProductCategoryModel
    ) {

    }
}
