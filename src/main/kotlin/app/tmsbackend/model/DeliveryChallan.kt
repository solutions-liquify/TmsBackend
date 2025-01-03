package app.tmsbackend.model

import kotlin.String
import kotlin.collections.List

data class DeliveryChallan(
    val id: String?,
    val deliveryOrderId: String?,
    val dateOfChallan: Long?,
    val status: String?,
    val partyName: String?,
    val totalDeliveringQuantity: Double = 0.0,
    val createdAt: Long?,
    val updatedAt: Long?,
    val deliveryChallanItems: List<DeliveryChallanItem> = emptyList()
)

data class DeliveryChallanItem(
    val id: String?,
    val deliveryChallanId: String?,
    val deliveryOrderItemId: String?,
    val district: String,
    val taluka: String,
    val locationName: String?,
    val materialName: String?,
    val quantity: Double = 0.0,
    val deliveredQuantity: Double = 0.0,
    val inProgressQuantity: Double = 0.0,
    val rate: Double = 0.0,
    val dueDate: Long?,
    val deliveringQuantity: Double = 0.0,
)

data class ListDeliveryChallansInput(
    val search: String? = null,
    val page: Int = 1,
    val size: Int = 10,
    val deliveryOrderIds: List<String> = emptyList()
)

data class ListDeliveryChallanOutputRecord(
    val id: String,
    val deliveryOrderId: String,
    val dateOfChallan: Long?,
    val status: String?,
    val partyName: String?,
    val totalDeliveringQuantity: Double = 0.0,
)
