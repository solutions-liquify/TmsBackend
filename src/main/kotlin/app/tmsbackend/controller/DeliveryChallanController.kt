package app.tmsbackend.controller

import app.tmsbackend.model.DeliveryChallan
import app.tmsbackend.model.ListDeliveryChallanOutputRecord
import app.tmsbackend.model.ListDeliveryChallansInput
import app.tmsbackend.service.DeliveryChallanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RequestMapping("/api/v1/delivery-challans")
@RestController
class DeliveryChallanController(
    private val deliveryChallanService: DeliveryChallanService
) {

    @GetMapping("/create/from-delivery-order/{deliveryOrderId}")
    fun createDeliveryChallanFromDeliveryOrder(
        @PathVariable deliveryOrderId: String,
    ): ResponseEntity<DeliveryChallan> {
        val createdDeliveryChallan = deliveryChallanService.createDeliveryChallanFromDeliveryOrder(deliveryOrderId)
        return ResponseEntity.ok(createdDeliveryChallan)
    }

    @PostMapping("/update")
    fun updateDeliveryChallan(@RequestBody deliveryChallan: DeliveryChallan): ResponseEntity<DeliveryChallan> {
        val updatedDeliveryChallan = deliveryChallanService.updateDeliveryChallan(deliveryChallan)
        return ResponseEntity.ok(updatedDeliveryChallan)
    }

    @GetMapping("/get/{id}")
    fun getDeliveryChallan(@PathVariable id: String): ResponseEntity<DeliveryChallan> {
        val deliveryChallan = deliveryChallanService.getDeliveryChallan(id)
        return ResponseEntity.ok(deliveryChallan)
    }

    @PostMapping("/list")
    fun listDeliveryChallans(
        @RequestBody listDeliveryChallansInput: ListDeliveryChallansInput
    ): ResponseEntity<List<ListDeliveryChallanOutputRecord>> {
        val deliveryChallans = deliveryChallanService.listDeliveryChallans(
            search = listDeliveryChallansInput.search,
            page = listDeliveryChallansInput.page,
            size = listDeliveryChallansInput.size
        )
        return ResponseEntity.ok(deliveryChallans)
    }
}
