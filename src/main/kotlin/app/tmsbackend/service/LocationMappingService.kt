package app.tmsbackend.service

import app.tmsbackend.config.LocationMappingConfig
import org.springframework.stereotype.Service

@Service
class LocationMappingService(private val locationMappingConfig: LocationMappingConfig) {

    fun listStates(): List<String> = 
        locationMappingConfig.states.map { it.name }

    fun listDistricts(): List<String> = 
        locationMappingConfig.states.flatMap { it.districts }.map { it.name }

    fun listTalukas(): List<String> = 
        locationMappingConfig.states
            .flatMap { it.districts }
            .flatMap { it.talukas }
            .map { it.name }

    fun listCities(): List<String> = 
        locationMappingConfig.states
            .flatMap { it.districts }
            .flatMap { it.talukas }
            .flatMap { it.cities }
            .map { it }
}