package app.tmsbackend.model

data class DeliveryOrderItem(
    val id: String?,
    val deliveryOrderId: String?,
    val district: String,
    val taluka: String,
    val location: String,
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