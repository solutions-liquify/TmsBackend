package app.tmsbackend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RequestMapping("/api/v1/units")
@RestController
class UnitController {

    @GetMapping("/list")
    fun listUnits(): ResponseEntity<List<String>> {
        val units = listOf("MT", "Kg", "Units", "Ltr")
        return ResponseEntity.ok(units)
    }
}
