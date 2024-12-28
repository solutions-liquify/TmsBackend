package app.tmsbackend.service

import app.tmsbackend.model.Location
import app.tmsbackend.model.LocationDTO
import app.tmsbackend.repository.LocationRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class LocationService(private val locationRepository: LocationRepository) {

    fun createLocation(location: Location): Location {
        val locationDTO = LocationDTO(
            id = location.id ?: UUID.randomUUID().toString(),
            name = location.name,
            pointOfContact = location.pointOfContact,
            contactNumber = location.contactNumber,
            email = location.email,
            addressLine1 = location.addressLine1,
            addressLine2 = location.addressLine2,
            state = location.state,
            district = location.district,
            taluka = location.taluka,
            city = location.city,
            pinCode = location.pinCode,
            createdAt = location.createdAt ?: Instant.now().epochSecond
        )
        val createdLocationDTO = locationRepository.createLocation(locationDTO)
        return createdLocationDTO.toLocation()
    }

    fun updateLocation(location: Location): Location {
        val locationDTO = LocationDTO(
            id = location.id ?: throw IllegalArgumentException("Location ID cannot be null"),
            name = location.name,
            pointOfContact = location.pointOfContact,
            contactNumber = location.contactNumber,
            email = location.email,
            addressLine1 = location.addressLine1,
            addressLine2 = location.addressLine2,
            state = location.state,
            district = location.district,
            taluka = location.taluka,
            city = location.city,
            pinCode = location.pinCode,
            createdAt = location.createdAt ?: Instant.now().epochSecond
        )
        val updatedLocationDTO = locationRepository.updateLocation(locationDTO)
        return updatedLocationDTO.toLocation()
    }

    fun getLocation(id: String): Location {
        val locationDTO = locationRepository.getLocation(id) ?: throw IllegalArgumentException("Location not found with ID: $id")
        return locationDTO.toLocation()
    }

    fun listLocations(
        search: String,
        page: Int,
        size: Int,
        states: List<String>,
        districts: List<String>,
        talukas: List<String>,
        cities: List<String>,
        returnAll: Boolean
    ): List<Location> {
        val locationDTOs = locationRepository.listLocations(search, page, size, states, districts, talukas, cities, returnAll)
        return locationDTOs.map { it.toLocation() }
    }
}
