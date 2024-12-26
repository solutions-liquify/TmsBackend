package app.tmsbackend.config

import app.tmsbackend.model.State
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "locationmapping")
class LocationMappingConfig {
    var states: List<State> = listOf()
}