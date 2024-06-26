package siberia.modules.user.data.dto.systemevents.user

import siberia.conf.AppConf

data class UserUpdateEvent(
    override val author: String,
    val updatedUserLogin: String,
    override val eventObjectId: Int,
    override val rollbackInstance: String
) : UserEvent() {
    override val eventType: Int
        get() = AppConf.eventTypes.updateEvent
    override val eventDescription: String
        get() = "User $updatedUserLogin was updated."
    override val eventObjectName: String
        get() = updatedUserLogin
}