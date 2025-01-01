package app.tmsbackend.service

import app.tmsbackend.model.DeliveryOrder
import app.tmsbackend.model.DeliveryOrderSection
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
        deliveryOrder.id?.let { deliveryOrderRepository.getDeliveryOrder(it) ?: throw IllegalArgumentException("Delivery order not found") } ?: throw IllegalArgumentException("Delivery order ID cannot be null")

        // Process new items - assign UUIDs to new items (where id is null)
        val processedSections = deliveryOrder.deliveryOrderSections.map { section ->
            section.copy(
                deliveryOrderItems = section.deliveryOrderItems.map { item ->
                    if (item.id == null) {
                        item.copy(
                            id = UUID.randomUUID().toString(),
                            deliveryOrderId = deliveryOrder.id
                        )
                    } else {
                        item
                    }
                }
            )
        }

        val deliveryOrderToUpdate = deliveryOrder.copy(
            updatedAt = Instant.now().epochSecond,
            deliveryOrderSections = processedSections
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
    fun listDeliveryOrders(page: Int, size: Int): List<DeliveryOrder> {
        return deliveryOrderRepository.listDeliveryOrders(page, size)
    }
}
