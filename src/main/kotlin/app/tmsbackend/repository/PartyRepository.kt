package app.tmsbackend.repository

import app.tmsbackend.model.PartyDTO
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class PartyRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(PartyRepository::class.java)

    /**
     * Row mapper for PartyDTO
     * @param rs: java.sql.ResultSet
     * @return PartyDTO
     */
    private fun rowMapper(rs: java.sql.ResultSet): PartyDTO {
        return PartyDTO(
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
     * Create party
     * @param partyDTO: PartyDTO
     * @return PartyDTO
     */
    fun createParty(partyDTO: PartyDTO): PartyDTO {
        try {
            logger.debug("[APP] Creating party with ID: ${partyDTO.id}")
            val sql = "INSERT INTO parties (id, name, point_of_contact, contact_number, email, address_line1, address_line2, state, district, taluka, city, pin_code, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            jdbcTemplate.update(
                sql,
                partyDTO.id,
                partyDTO.name,
                partyDTO.pointOfContact,
                partyDTO.contactNumber,
                partyDTO.email,
                partyDTO.addressLine1,
                partyDTO.addressLine2,
                partyDTO.state,
                partyDTO.district,
                partyDTO.taluka,
                partyDTO.city,
                partyDTO.pinCode,
                partyDTO.createdAt
            )
            logger.debug("[APP] Party created with ID: ${partyDTO.id}")
            return partyDTO
        } catch (e: Exception) {
            logger.error("[APP] Error creating party with ID: ${partyDTO.id}", e)
            throw e
        }
    }

    /**
     * Update party
     * @param partyDTO: PartyDTO
     * @return PartyDTO
     */
    fun updateParty(partyDTO: PartyDTO): PartyDTO {
        try {
            logger.debug("[APP] Updating party with ID: ${partyDTO.id}")
            val sql = "UPDATE parties SET name = ?, point_of_contact = ?, contact_number = ?, email = ?, address_line1 = ?, address_line2 = ?, state = ?, district = ?, taluka = ?, city = ?, pin_code = ?, created_at = ? WHERE id = ?"
            jdbcTemplate.update(
                sql,
                partyDTO.name,
                partyDTO.pointOfContact,
                partyDTO.contactNumber,
                partyDTO.email,
                partyDTO.addressLine1,
                partyDTO.addressLine2,
                partyDTO.state,
                partyDTO.district,
                partyDTO.taluka,
                partyDTO.city,
                partyDTO.pinCode,
                partyDTO.createdAt,
                partyDTO.id
            )
            logger.debug("[APP] Party updated with ID: ${partyDTO.id}")
            return partyDTO
        } catch (e: Exception) {
            logger.error("[APP] Error updating party with ID: ${partyDTO.id}", e)
            throw e
        }
    }

    /**
     * Get party by id
     * @param id: String
     * @return PartyDTO?
     */
    fun getParty(id: String): PartyDTO? {
        try {
            logger.debug("[APP] Fetching party with ID: $id")
            val sql = "SELECT * FROM parties WHERE id = ?"
            val party = jdbcTemplate.queryForObject(sql, { rs, _ -> rowMapper(rs) }, id)
            if (party != null) {
                logger.info("[APP] Party found with ID: $id")
            } else {
                logger.warn("[APP] No party found with ID: $id")
            }
            return party
        } catch (e: Exception) {
            logger.error("[APP] Error fetching party with ID: $id", e)
            throw e
        }
    }

    /**
     * List parties with pagination and search
     * @param search: String
     * @param page: Int
     * @param size: Int
     * @return List<PartyDTO>
     */

    fun listParties(search: String, page: Int, size: Int): List<PartyDTO> {
        try {
            logger.debug("[APP] Listing parties with search: '$search', page: $page, size: $size")
            val sqlBuilder = StringBuilder("SELECT * FROM parties WHERE 1=1")

            if (search.isNotBlank()) {
                sqlBuilder.append(" AND name LIKE ?")
            }

            sqlBuilder.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?")

            val sql = sqlBuilder.toString()
            val offset = (page - 1) * size
            val parties = if (search.isNotBlank()) {
                jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, "%$search%", size, offset)
            } else {
                jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, size, offset)
            }

            logger.info("[APP] Listed ${parties.size} parties")
            return parties
        } catch (e: Exception) {
            logger.error("[APP] Error listing parties with search: '$search', page: $page, size: $size", e)
            throw e
        }
    }

    /**
     * List all parties without pagination and search
     * @return List<PartyDTO>
     */
    fun listAllParties(): List<PartyDTO> {
        try {
            logger.debug("[APP] Listing all parties")
            val sql = "SELECT * FROM parties ORDER BY created_at DESC"
            val parties = jdbcTemplate.query(sql) { rs, _ -> rowMapper(rs) }
            logger.info("[APP] Listed ${parties.size} parties")
            return parties
        } catch (e: Exception) {
            logger.error("[APP] Error listing all parties", e)
            throw e
        }
    }

}
