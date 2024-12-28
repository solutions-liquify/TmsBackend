package app.tmsbackend.repository

import app.tmsbackend.model.MaterialDTO
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class MaterialRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(MaterialRepository::class.java)

    /**
     * Row mapper for MaterialDTO
     * @param rs: java.sql.ResultSet
     * @return MaterialDTO
     */
    private fun rowMapper(rs: java.sql.ResultSet): MaterialDTO {
        return MaterialDTO(
            rs.getString("id"),
            rs.getString("name"),
            rs.getLong("created_at")
        )
    }

    /**
     * Create material
     * @param materialDTO: MaterialDTO
     * @return MaterialDTO
     */
    fun createMaterial(materialDTO: MaterialDTO): MaterialDTO {
        try {
            logger.debug("[APP] Creating material with ID: ${materialDTO.id}")
            val sql = "INSERT INTO materials (id, name, created_at) VALUES (?, ?, ?)"
            jdbcTemplate.update(
                sql,
                materialDTO.id,
                materialDTO.name,
                materialDTO.createdAt
            )
            logger.debug("[APP] Material created with ID: ${materialDTO.id}")
            return materialDTO
        } catch (e: Exception) {
            logger.error("[APP] Error creating material with ID: ${materialDTO.id}", e)
            throw e
        }
    }

    /**
     * Update material
     * @param materialDTO: MaterialDTO
     * @return MaterialDTO
     */
    fun updateMaterial(materialDTO: MaterialDTO): MaterialDTO {
        try {
            logger.debug("[APP] Updating material with ID: ${materialDTO.id}")
            val sql = "UPDATE materials SET name = ?, created_at = ? WHERE id = ?"
            jdbcTemplate.update(
                sql,
                materialDTO.name,
                materialDTO.createdAt,
                materialDTO.id
            )
            logger.debug("[APP] Material updated with ID: ${materialDTO.id}")
            return materialDTO
        } catch (e: Exception) {
            logger.error("[APP] Error updating material with ID: ${materialDTO.id}", e)
            throw e
        }
    }

    /**
     * Get material by id
     * @param id: String
     * @return MaterialDTO?
     */
    fun getMaterial(id: String): MaterialDTO? {
        try {
            logger.debug("[APP] Fetching material with ID: $id")
            val sql = "SELECT * FROM materials WHERE id = ?"
            val material = jdbcTemplate.queryForObject(sql, { rs, _ -> rowMapper(rs) }, id)
            if (material != null) {
                logger.info("[APP] Material found with ID: $id")
            } else {
                logger.warn("[APP] No material found with ID: $id")
            }
            return material
        } catch (e: Exception) {
            logger.error("[APP] Error fetching material with ID: $id", e)
            throw e
        }
    }

    /**
     * List all materials
     * @return List<MaterialDTO>
     */
    fun listAllMaterials(): List<MaterialDTO> {
        try {
            logger.debug("[APP] Listing all materials")
            val sql = "SELECT * FROM materials ORDER BY created_at DESC"
            val materials = jdbcTemplate.query(sql) { rs, _ -> rowMapper(rs) }
            logger.info("[APP] Listed ${materials.size} materials")
            return materials
        } catch (e: Exception) {
            logger.error("[APP] Error listing all materials", e)
            throw e
        }
    }
}
