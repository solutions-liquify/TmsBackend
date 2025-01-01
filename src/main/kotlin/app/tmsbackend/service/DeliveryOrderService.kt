package app.tmsbackend.service

import app.tmsbackend.model.DeliveryOrder
import app.tmsbackend.model.DeliveryOrderSection
import app.tmsbackend.model.ListDeliveryOrderItem
import app.tmsbackend.repository.DeliveryOrderRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class DeliveryOrderService(
    private val deliveryOrderRepository: DeliveryOrderRepository
) {

    /**
     * Create delivery order
     * Sets id, createdAt and updatedAt timestamps
     * @param deliveryOrder: DeliveryOrder
     * @return DeliveryOrder
     */
    fun createDeliveryOrder(deliveryOrder: DeliveryOrder): DeliveryOrder {
        val deliveryOrderId = UUID.randomUUID().toString()
        val deliveryOrderSections:List<DeliveryOrderSection> = deliveryOrder.deliveryOrderSections.map { section ->
            section.copy(
                deliveryOrderItems = section.deliveryOrderItems.map {
                    it.copy(id = UUID.randomUUID().toString(), deliveryOrderId = deliveryOrderId)
                }
            )
        }

        val deliveryOrderToCreate = deliveryOrder.copy(
            id = deliveryOrderId,
            deliveryOrderSections = deliveryOrderSections,
            createdAt = Instant.now().epochSecond,
            updatedAt = Instant.now().epochSecond
        )

        return deliveryOrderRepository.createDeliveryOrder(deliveryOrderToCreate)
    }

    /**
     * Update delivery order
     * Updates the updatedAt timestamp
     * @param deliveryOrder: DeliveryOrder
     * @return DeliveryOrder
     */
    fun updateDeliveryOrder(deliveryOrder: DeliveryOrder): DeliveryOrder {
        // Validate that delivery order exists
        if (deliveryOrder.id == null) {
            throw IllegalArgumentException("Delivery order ID cannot be null")
        }
        
        val existingOrder = deliveryOrderRepository.getDeliveryOrder(deliveryOrder.id)
        if (existingOrder == null) {
            throw IllegalArgumentException("Delivery order not found")
        }

        // Process new items - assign UUIDs to new items (where id is null)
        val updatedSections = deliveryOrder.deliveryOrderSections.map { section ->
            section.copy(
                deliveryOrderItems = section.deliveryOrderItems.map {
                    it.takeUnless { it.id == null } ?: it.copy(
                        id = UUID.randomUUID().toString(),
                        deliveryOrderId = deliveryOrder.id
                    )
                }
            )
        }

        val deliveryOrderToUpdate = deliveryOrder.copy(
            updatedAt = Instant.now().epochSecond,
            deliveryOrderSections = updatedSections
        )

        return deliveryOrderRepository.updateDeliveryOrder(deliveryOrderToUpdate)
    }

    /**
     * Get delivery order by id
     * @param id: String
     * @return DeliveryOrder?
     */
    fun getDeliveryOrder(id: String): DeliveryOrder? {
        return deliveryOrderRepository.getDeliveryOrder(id)
    }

    /**
     * List delivery orders with pagination
     * @param page: Int
     * @param size: Int
     * @return List<DeliveryOrder>
     */
    fun listDeliveryOrders(
        search: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        statuses: List<String>? = null,
        partyIds: List<String>? = null
    ): List<ListDeliveryOrderItem> {
        return deliveryOrderRepository.listDeliveryOrders(
            search = search,
            page = page,
            pageSize = pageSize,
            statuses = statuses,
            partyIds = partyIds
        )
    }
}
