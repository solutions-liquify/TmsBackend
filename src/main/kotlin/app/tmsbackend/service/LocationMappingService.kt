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

    fun listDistricts(): List<String> {
        return locationMappingConfig.states.flatMap { it.districts }.map { it.name }
//        var districtNames: List<String> = emptyList()
//        for (state in locationMappingConfig.states) {
//            for (district in state.districts) {
//                districtNames = districtNames + district.name
//            }
//        }
//        return districtNames
    }

    fun listTalukas(): List<String> {
        return locationMappingConfig
            .states
            .flatMap { it.districts }
            .flatMap { it.talukas }
            .map { it.name }
    }

    fun listCities(): List<String> {
        return locationMappingConfig
            .states
            .flatMap { it.districts }
            .flatMap { it.talukas }
            .flatMap { it.cities }
            .map { it }
    }
}