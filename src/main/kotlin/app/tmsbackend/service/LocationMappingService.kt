package app.tmsbackend.service

import app.tmsbackend.config.LocationMappingConfig
import org.springframework.stereotype.Service

@Service
class LocationMappingService(
    private val locationMappingConfig: LocationMappingConfig
) {
    fun listStates(): List<String> {
        return locationMappingConfig.states.map { it.name }
    }
}