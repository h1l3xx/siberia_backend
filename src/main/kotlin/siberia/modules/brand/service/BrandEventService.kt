package siberia.modules.brand.service

import org.jetbrains.exposed.sql.select
import org.kodein.di.DI
import org.kodein.di.instance
import siberia.modules.auth.data.dto.AuthorizedUser
import siberia.modules.brand.data.dto.BrandInputDto
import siberia.modules.brand.data.dto.BrandUpdateDto
import siberia.modules.brand.data.models.BrandModel
import siberia.modules.logger.data.dto.SystemEventOutputDto
import siberia.utils.kodein.KodeinEventService

class BrandEventService(di: DI) : KodeinEventService(di) {
    private val brandService: BrandService by instance()
    override fun rollbackUpdate(authorizedUser: AuthorizedUser, event: SystemEventOutputDto) {
        val updateEventData = event.getRollbackData<BrandUpdateDto>()
        with(
            BrandModel.select {
                BrandModel.id eq updateEventData.objectId
            }.map {
                it[BrandModel.id]
            }
        ){
            if (this.isNotEmpty())
                brandService.update(authorizedUser, updateEventData.objectId, updateEventData.objectDto)
        }
    }

    override fun rollbackRemove(authorizedUser: AuthorizedUser, event: SystemEventOutputDto) {
        val createEventData = event.getRollbackData<BrandInputDto>()
        brandService.create(authorizedUser, createEventData.objectDto)
    }

    override fun rollbackCreate(authorizedUser: AuthorizedUser, event: SystemEventOutputDto) {
        TODO("Not yet implemented")
    }
}