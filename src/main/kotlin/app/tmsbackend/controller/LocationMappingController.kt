package app.tmsbackend.controller

import app.tmsbackend.service.LocationMappingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RequestMapping("/api/v1")
@RestController
class LocationMappingController(
    private val locationMappingService: LocationMappingService
) {
    @GetMapping("/states/list")
    fun listStates(): ResponseEntity<List<String>> {
        val states = locationMappingService.listStates()
        return ResponseEntity.ok(states)
    }

    @PostMapping("/districts/list")
    fun listDistricts(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listDistricts())
    }

    @PostMapping("/talukas/list")
    fun listTalukas(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listTalukas())
    }

    @PostMapping("/cities/list")
    fun listCities(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listCities())
    }
}