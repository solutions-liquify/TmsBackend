package app.tmsbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TmsBackendApplication

fun main(args: Array<String>) {
    runApplication<TmsBackendApplication>(*args)
}
