package siberia.modules.product.data.dto.systemevents

import siberia.conf.AppConf

data class ProductRemoveEvent(
    override val author: String, val removedProductName: String, val removedProductVendorCode: String
) : ProductEvent() {
    override val eventType: Int
        get() = AppConf.eventTypes.removeEvent
    override val eventDescription: String
        get() = "Product $removedProductName ($removedProductVendorCode) was created."
    override val eventObjectName: String
        get() = removedProductName
}