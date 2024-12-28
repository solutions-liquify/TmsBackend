package app.tmsbackend.controller

import app.tmsbackend.model.Location
import app.tmsbackend.model.ListLocationsInput
import app.tmsbackend.service.LocationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/api/v1/locations")
@RestController
class LocationController(
    private val locationService: LocationService
) {

    @PostMapping("/create")
    fun createLocation(@RequestBody location: Location): ResponseEntity<Location> {
        val createdLocation = locationService.createLocation(location)
        return ResponseEntity.ok(createdLocation)
    }

    @PostMapping("/update")
    fun updateLocation(@RequestBody location: Location): ResponseEntity<Location> {
        val updatedLocation = locationService.updateLocation(location)
        return ResponseEntity.ok(updatedLocation)
    }

    @GetMapping("/{id}")
    fun getLocation(@PathVariable id: String): ResponseEntity<Location> {
        val location = locationService.getLocation(id)
        return ResponseEntity.ok(location)
    }

    @PostMapping("/list")
    fun listLocations(
        @RequestBody listLocationsInput: ListLocationsInput
    ): ResponseEntity<List<Location>> {
        val locations = locationService.listLocations(
            search = listLocationsInput.search,
            page = listLocationsInput.page,
            size = listLocationsInput.size,
            states = listLocationsInput.states,
            districts = listLocationsInput.districts,
            talukas = listLocationsInput.talukas,
            cities = listLocationsInput.cities,
            returnAll = listLocationsInput.returnAll
        )
        return ResponseEntity.ok(locations)
    }
}

