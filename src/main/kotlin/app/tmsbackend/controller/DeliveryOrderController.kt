package app.tmsbackend.controller

import app.tmsbackend.model.DeliveryOrder
import app.tmsbackend.service.DeliveryOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RequestMapping("/api/v1/delivery-orders")
@RestController
class DeliveryOrderController(
    private val deliveryOrderService: DeliveryOrderService
) {

    @PostMapping("/create")
    fun createDeliveryOrder(@RequestBody deliveryOrder: DeliveryOrder): ResponseEntity<DeliveryOrder> {
        val createdDeliveryOrder = deliveryOrderService.createDeliveryOrder(deliveryOrder)
        return ResponseEntity.ok(createdDeliveryOrder)
    }

    @PostMapping("/update")
    fun updateDeliveryOrder(@RequestBody deliveryOrder: DeliveryOrder): ResponseEntity<DeliveryOrder> {
        val updatedDeliveryOrder = deliveryOrderService.updateDeliveryOrder(deliveryOrder)
        return ResponseEntity.ok(updatedDeliveryOrder)
    }

    @GetMapping("/get/{id}")
    fun getDeliveryOrder(@PathVariable id: String): ResponseEntity<DeliveryOrder?> {
        val deliveryOrder = deliveryOrderService.getDeliveryOrder(id)
        return ResponseEntity.ok(deliveryOrder)
    }

    @GetMapping("/list")
    fun listDeliveryOrders(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<DeliveryOrder>> {
        val deliveryOrders = deliveryOrderService.listDeliveryOrders(page, size)
        return ResponseEntity.ok(deliveryOrders)
    }
}
