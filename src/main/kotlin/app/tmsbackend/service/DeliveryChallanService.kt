package app.tmsbackend.service

import app.tmsbackend.model.DeliveryChallan
import app.tmsbackend.model.DeliveryChallanItem
import app.tmsbackend.model.ListDeliveryChallanOutputRecord
import app.tmsbackend.repository.DeliveryChallanRepository
import app.tmsbackend.repository.DeliveryOrderRepository
import app.tmsbackend.repository.PartyRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class DeliveryChallanService(
    private val deliveryChallanRepository: DeliveryChallanRepository,
    private val deliveryOrderRepository: DeliveryOrderRepository,
    private val partyRepository: PartyRepository
) {

    /**
     * Create delivery challan from delivery order
     * Sets id, createdAt and updatedAt timestamps
     * @param deliveryOrderId: String
     * @return DeliveryChallan
     */
    fun createDeliveryChallanFromDeliveryOrder(deliveryOrderId: String): DeliveryChallan {
        val deliveryOrder = deliveryOrderRepository.getDeliveryOrder(deliveryOrderId)
        if (deliveryOrder == null) {
            throw IllegalArgumentException("Delivery order not found")
        }

        val deliveryChallanToCreate = DeliveryChallan(
            id = UUID.randomUUID().toString(),
            deliveryOrderId = deliveryOrderId,
            status = "in-progress",
            createdAt = Instant.now().epochSecond,
            updatedAt = Instant.now().epochSecond,
            partyName = null,
            dateOfChallan = Instant.now().epochSecond,
            deliveryChallanItems = emptyList()
        )

        return deliveryChallanRepository.createDeliveryChallan(deliveryChallanToCreate)
    }

    /**
     * Update delivery challan
     * Updates the updatedAt timestamp
     * @param deliveryChallan: DeliveryChallan
     * @return DeliveryChallan
     */
    fun updateDeliveryChallan(deliveryChallan: DeliveryChallan): DeliveryChallan {
        // Validate that delivery challan exists
        if (deliveryChallan.id == null) {
            throw IllegalArgumentException("Delivery challan ID cannot be null")
        }
        
        val existingChallan = deliveryChallanRepository.getDeliveryChallan(deliveryChallan.id)
        if (existingChallan == null) {
            throw IllegalArgumentException("Delivery challan not found")
        }

        val deliveryChallanToUpdate = deliveryChallan.copy(
            deliveryChallanItems = deliveryChallan.deliveryChallanItems.map {
                it.takeUnless { it.id == null } ?: it.copy(id = UUID.randomUUID().toString())
            },
            updatedAt = Instant.now().epochSecond
        )
        return deliveryChallanRepository.updateDeliveryChallan(deliveryChallanToUpdate)
    }

    /**
     * Get delivery challan by id
     * @param id: String
     * @return DeliveryChallan?
     */
    fun getDeliveryChallan(id: String): DeliveryChallan? {
        return deliveryChallanRepository.getDeliveryChallan(id)
    }

    /**
     * List delivery challans with pagination
     * @param search: String
     * @param page: Int
     * @param size: Int
     * @return List<DeliveryChallan>
     */
    fun listDeliveryChallans(
        search: String? = null,
        page: Int = 1,
        size: Int = 10
    ): List<ListDeliveryChallanOutputRecord> {
        return deliveryChallanRepository.listDeliveryChallans(
            search = search,
            page = page,
            size = size
        )
    }
}
