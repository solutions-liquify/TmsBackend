package app.tmsbackend.controller

import app.tmsbackend.service.LocationMappingService
import app.tmsbackend.model.ListDistrictsInput
import app.tmsbackend.model.ListTalukasInput
import app.tmsbackend.model.ListCitiesInput
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
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
    fun listDistricts(
        @RequestBody listDistrictsInput: ListDistrictsInput
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listDistricts(
            states = listDistrictsInput.states
        ))
    }

    @PostMapping("/talukas/list")
    fun listTalukas(
        @RequestBody listTalukasInput: ListTalukasInput
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listTalukas(
            states = listTalukasInput.states,
            districts = listTalukasInput.districts
        ))
    }

    @PostMapping("/cities/list")
    fun listCities(
        @RequestBody listCitiesInput: ListCitiesInput
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(locationMappingService.listCities(
            states = listCitiesInput.states,
            districts = listCitiesInput.districts,
            talukas = listCitiesInput.talukas
        ))
    }
}