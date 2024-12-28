package app.tmsbackend.controller

import app.tmsbackend.model.Material
import app.tmsbackend.service.MaterialService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RequestMapping("/api/v1/materials")
@RestController
class MaterialController(
    private val materialService: MaterialService
) {

    @PostMapping("/create")
    fun createMaterial(@RequestBody material: Material): ResponseEntity<Material> {
        val createdMaterial = materialService.createMaterial(material)
        return ResponseEntity.ok(createdMaterial)
    }

    @PostMapping("/update")
    fun updateMaterial(@RequestBody material: Material): ResponseEntity<Material> {
        val updatedMaterial = materialService.updateMaterial(material)
        return ResponseEntity.ok(updatedMaterial)
    }

    @GetMapping("/get/{id}")
    fun getMaterial(@PathVariable id: String): ResponseEntity<Material> {
        val material = materialService.getMaterial(id)
        return ResponseEntity.ok(material)
    }

    @GetMapping("/list")
    fun listAllMaterials(): ResponseEntity<List<Material>> {
        val materials = materialService.listAllMaterials()
        return ResponseEntity.ok(materials)
    }
}
