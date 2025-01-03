package app.tmsbackend.model


data class DeliverOrderItemMetadata(
    val id: String,
    val district: String,
    val taluka: String,
    val locationName: String,
    val materialName: String,
    val quantity: Double,
    val status: String,
    val rate: Double?,
    val dueDate: Long?,
    val deliveredQuantity: Double = 0.0,
    val inProgressQuantity: Double = 0.0
)


data class DeliveryOrderItem(
    val id: String?,
    val deliveryOrderId: String?,
    val district: String,
    val taluka: String,
    val locationId: String,
    val materialId: String?,
    val quantity: Double = 0.0,
    val deliveredQuantity: Double = 0.0,
    val inProgressQuantity: Double = 0.0,
    val rate: Double = 0.0,
    val dueDate: Long?,
    val status: String,
)

data class DeliveryOrderSection(
    val district: String,
    val totalQuantity: Double = 0.0,
    val totalDeliveredQuantity: Double = 0.0,
    val totalInProgressQuantity: Double = 0.0,
    val status: String,
    val deliveryOrderItems: List<DeliveryOrderItem> = emptyList()
)


data class DeliveryOrder(
    val id: String?,
    val contractId: String,
    val partyId: String,
    val dateOfContract: Long?,
    val status: String,
    val grandTotalQuantity: Double = 0.0,
    val grandTotalDeliveredQuantity: Double = 0.0,
    val grandTotalInProgressQuantity: Double = 0.0,
    val createdAt: Long?,
    val updatedAt: Long?,
    val deliveryOrderSections: List<DeliveryOrderSection> = emptyList()
)

data class ListDeliveryOrderInput(
    val search: String? = null,
    val page: Int = 1,
    val pageSize: Int = 10,
    val statuses: List<String> = emptyList(),
    val partyIds: List<String> = emptyList()
)

data class ListDeliveryOrderItem(
    val id: String,
    val contractId: String?,
    val partyName: String?,
    val status: String?,
)
