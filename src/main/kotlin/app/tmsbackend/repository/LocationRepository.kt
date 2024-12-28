package app.tmsbackend.repository

import app.tmsbackend.model.LocationDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.slf4j.LoggerFactory

@Repository
class LocationRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(LocationRepository::class.java)

    /**
     * Row mapper for LocationDTO
     * @param rs: java.sql.ResultSet
     * @return LocationDTO
     */
    private fun rowMapper(rs: java.sql.ResultSet): LocationDTO {
        return LocationDTO(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("point_of_contact"),
            rs.getString("contact_number"),
            rs.getString("email"),
            rs.getString("address_line1"),
            rs.getString("address_line2"),
            rs.getString("state"),
            rs.getString("district"),
            rs.getString("taluka"),
            rs.getString("city"),
            rs.getString("pin_code"),
            rs.getLong("created_at")
        )
    }
    
    /**
     * Create location
     * @param locationDTO: LocationDTO
     * @return LocationDTO
     */
    fun createLocation(locationDTO: LocationDTO): LocationDTO {
        try {
            logger.debug("[APP] Creating location with ID: ${locationDTO.id}")
            val sql = "INSERT INTO locations (id, name, point_of_contact, contact_number, email, address_line1, address_line2, state, district, taluka, city, pin_code, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            jdbcTemplate.update(
                sql,
                locationDTO.id,
                locationDTO.name,
                locationDTO.pointOfContact,
                locationDTO.contactNumber,
                locationDTO.email,
                locationDTO.addressLine1,
                locationDTO.addressLine2,
                locationDTO.state,
                locationDTO.district,
                locationDTO.taluka,
                locationDTO.city,
                locationDTO.pinCode,
                locationDTO.createdAt
            )
            logger.debug("[APP] Location created with ID: ${locationDTO.id}")
            return locationDTO
        } catch (e: Exception) {
            logger.error("[APP] Error creating location with ID: ${locationDTO.id}", e)
            throw e
        }
    }

    /**
     * Update location
     * @param locationDTO: LocationDTO
     * @return LocationDTO
     */
    fun updateLocation(locationDTO: LocationDTO): LocationDTO {
        try {
            logger.debug("[APP] Updating location with ID: ${locationDTO.id}")
            val sql = "UPDATE locations SET name = ?, point_of_contact = ?, contact_number = ?, email = ?, address_line1 = ?, address_line2 = ?, state = ?, district = ?, taluka = ?, city = ?, pin_code = ?, created_at = ? WHERE id = ?"
            jdbcTemplate.update(
                sql,
                locationDTO.name,
                locationDTO.pointOfContact,
                locationDTO.contactNumber,
                locationDTO.email,
                locationDTO.addressLine1,
                locationDTO.addressLine2,
                locationDTO.state,
                locationDTO.district,
                locationDTO.taluka,
                locationDTO.city,
                locationDTO.pinCode,
                locationDTO.createdAt,
                locationDTO.id
            )
            logger.debug("[APP] Location updated with ID: ${locationDTO.id}")
            return locationDTO
        } catch (e: Exception) {
            logger.error("[APP] Error updating location with ID: ${locationDTO.id}", e)
            throw e
        }
    }

    /**
     * Get location by id
     * @param id: String
     * @return LocationDTO?
     */ 
    fun getLocation(id: String): LocationDTO? {
        try {
            logger.debug("[APP] Fetching location with ID: $id")
            val sql = "SELECT * FROM locations WHERE id = ?"
            val location = jdbcTemplate.queryForObject(sql, { rs, _ -> rowMapper(rs) }, id)
            if (location != null) {
                logger.info("[APP] Location found with ID: $id")
            } else {
                logger.warn("[APP] No location found with ID: $id")
            }
            return location
        } catch (e: Exception) {
            logger.error("[APP] Error fetching location with ID: $id", e)
            throw e
        }
    } 
    
    /**
     * List locations with pagination and search
     * @param search: String
     * @param page: Int
     * @param size: Int
     * @param states: List<String>
     * @param districts: List<String>
     * @param talukas: List<String>
     * @param cities: List<String>
     * @param returnAll: Boolean
     * @return List<LocationDTO>
     */
    
    fun listLocations(
        search: String = "",
        page: Int = 1,
        size: Int = 10,
        states: List<String> = emptyList(),
        districts: List<String> = emptyList(),
        talukas: List<String> = emptyList(),
        cities: List<String> = emptyList(),
        returnAll: Boolean = false
    ): List<LocationDTO> {
        try {
            logger.debug("[APP] Listing locations with search: '$search', page: $page, size: $size, returnAll: $returnAll")
            val sqlBuilder = StringBuilder("SELECT * FROM locations WHERE 1=1")
            
            if (search.isNotBlank()) {
                sqlBuilder.append(" AND name LIKE ?")
            }
            
            if (states.isNotEmpty()) {
                sqlBuilder.append(" AND state IN (${states.joinToString(",") { "?" }})")
            }
            
            if (districts.isNotEmpty()) {
                sqlBuilder.append(" AND district IN (${districts.joinToString(",") { "?" }})")
            }
            
            if (talukas.isNotEmpty()) {
                sqlBuilder.append(" AND taluka IN (${talukas.joinToString(",") { "?" }})")
            }
            
            if (cities.isNotEmpty()) {
                sqlBuilder.append(" AND city IN (${cities.joinToString(",") { "?" }})")
            }
            
            sqlBuilder.append(" ORDER BY created_at DESC")
            
            if (!returnAll) {
                sqlBuilder.append(" LIMIT ? OFFSET ?")
            }
            
            val sql = sqlBuilder.toString()
            val offset = (page - 1) * size
            val locations = if (search.isNotBlank()) {
                if (returnAll) {
                    jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, "%$search%")
                } else {
                    jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, "%$search%", size, offset)
                }
            } else {
                if (returnAll) {
                    jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) })
                } else {
                    jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, size, offset)
                }
            }
            
            logger.info("[APP] Listed ${locations.size} locations")
            return locations
        } catch (e: Exception) {
            logger.error("[APP] Error listing locations with search: '$search', page: $page, size: $size, returnAll: $returnAll", e)
            throw e
        }
    }

}
