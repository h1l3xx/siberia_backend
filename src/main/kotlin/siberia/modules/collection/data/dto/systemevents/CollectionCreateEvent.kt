package siberia.modules.collection.data.dto.systemevents

import siberia.conf.AppConf

data class CollectionCreateEvent(
    override val author: String,
    val createdCollectionName: String,
    override val eventObjectId: Int
) : CollectionEvent() {
    override val rollbackInstance: String
        get() = ""
    override val eventType: Int
        get() = AppConf.eventTypes.createEvent
    override val eventDescription: String
        get() = "Collection $createdCollectionName was created."
    override val eventObjectName: String
        get() = createdCollectionName
}