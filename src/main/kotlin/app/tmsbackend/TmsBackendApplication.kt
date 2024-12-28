package app.tmsbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
    
@SpringBootApplication
class TmsBackendApplication

fun main(args: Array<String>) {
    runApplication<TmsBackendApplication>(*args)
}

@CrossOrigin
@RestController
class TmsBackendController {
    @GetMapping("/")
    fun index(): String {
        return "Welcome to backend service for TMS"
    }
}
