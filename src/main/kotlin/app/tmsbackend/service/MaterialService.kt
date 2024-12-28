package app.tmsbackend.service

import app.tmsbackend.model.Material
import app.tmsbackend.model.MaterialDTO
import app.tmsbackend.repository.MaterialRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class MaterialService(private val materialRepository: MaterialRepository) {

    /**
     * Create material
     * sets id and createdAt
     * @param material: Material
     * @return Material
     */
    fun createMaterial(material: Material): Material {
        val materialToCreate: Material = material.copy(
            id = UUID.randomUUID().toString(),
            createdAt = Instant.now().epochSecond
        )

        val materialDTO = MaterialDTO(
            materialToCreate.id ?: "",
            materialToCreate.name,
            materialToCreate.createdAt ?: 0L
        )
        materialRepository.createMaterial(materialDTO)
        return materialToCreate
    }

    /**
     * Update material
     * @param material: Material
     * @return Material
     */
    fun updateMaterial(material: Material): Material {
        val materialDTO = MaterialDTO(
            material.id ?: throw IllegalArgumentException("Material ID must be present"),
            material.name,
            material.createdAt ?: 0L
        )
        materialRepository.updateMaterial(materialDTO)
        return material
    }

    /**
     * Get material by id
     * @param id: String
     * @return Material
     */
    fun getMaterial(id: String): Material {
        val materialDTO = materialRepository.getMaterial(id) ?: throw IllegalArgumentException("Material not found")
        return Material(
            materialDTO.id,
            materialDTO.name,
            materialDTO.createdAt
        )
    }

    /**
     * List all materials
     * @return List<Material>
     */
    fun listAllMaterials(): List<Material> {
        val materialDTOs = materialRepository.listAllMaterials()
        return materialDTOs.map { it.toMaterial() }
    }
}

