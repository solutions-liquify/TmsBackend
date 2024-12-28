package app.tmsbackend.service

import app.tmsbackend.config.LocationMappingConfig
import org.springframework.stereotype.Service

@Service
class LocationMappingService(private val locationMappingConfig: LocationMappingConfig) {

    fun listStates(): List<String> =
        locationMappingConfig.states.map { it.name }

    fun listDistricts(states: List<String> = emptyList()): List<String> =
        locationMappingConfig.states
            .let { if (states.isEmpty()) it else it.filter { state -> state.name in states } }
            .flatMap { it.districts }
            .map { it.name }

    fun listTalukas(states: List<String> = emptyList(), districts: List<String> = emptyList()): List<String> =
        locationMappingConfig.states
            .let { if (states.isEmpty()) it else it.filter { state -> state.name in states } }
            .flatMap { it.districts }
            .let { if (districts.isEmpty()) it else it.filter { district -> district.name in districts } }
            .flatMap { it.talukas }
            .map { it.name }

    fun listCities(states: List<String> = emptyList(), districts: List<String> = emptyList(), talukas: List<String> = emptyList()): List<String> =
        locationMappingConfig.states
            .let { if (states.isEmpty()) it else it.filter { state -> state.name in states } }
            .flatMap { it.districts }
            .let { if (districts.isEmpty()) it else it.filter { district -> district.name in districts } }
            .flatMap { it.talukas }
            .let { if (talukas.isEmpty()) it else it.filter { taluka -> taluka.name in talukas } }
            .flatMap { it.cities }
            
}
