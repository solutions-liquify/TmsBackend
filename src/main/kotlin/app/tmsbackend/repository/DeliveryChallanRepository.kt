package app.tmsbackend.repository

import app.tmsbackend.model.DeliveryChallan
import app.tmsbackend.model.DeliveryChallanItem
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Repository
class DeliveryChallanRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(DeliveryChallanRepository::class.java)

    private fun deliveryChallanRowMapper(rs: ResultSet): DeliveryChallan {
        return DeliveryChallan(
            id = rs.getString("id"),
            deliveryOrderId = rs.getString("delivery_order_id"),
            dateOfChallan = rs.getLong("date_of_challan"),
            status = rs.getString("status"),
            partyName = rs.getString("party_name"),
            totalDeliveringQuantity = rs.getDouble("total_delivering_quantity"),
            createdAt = rs.getLong("created_at"),
            updatedAt = rs.getLong("updated_at"),
            deliveryChallanItems = emptyList()
        )
    }

    private fun deliveryChallanItemRowMapper(rs: ResultSet): DeliveryChallanItem {
        return DeliveryChallanItem(
            id = rs.getString("id"),
            deliveryChallanId = rs.getString("delivery_challan_id"),
            deliveryOrderItemId = rs.getString("delivery_order_item_id"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationName = rs.getString("location_name"),
            materialName = rs.getString("material_name"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("due_date"),
            deliveringQuantity = rs.getDouble("delivering_quantity")
        )
    }

    fun createDeliveryChallan(deliveryChallan: DeliveryChallan): DeliveryChallan {
        try {
            logger.debug("[APP] Creating delivery challan with ID: ${deliveryChallan.id}")

            val sql = """
                INSERT INTO delivery_challan (
                    id,
                    delivery_order_id,
                    date_of_challan,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
            """.trimIndent()

            jdbcTemplate.update(
                sql,
                deliveryChallan.id,
                deliveryChallan.deliveryOrderId,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.createdAt,
                deliveryChallan.updatedAt
            )

            deliveryChallan.deliveryChallanItems.forEach { item ->
                createDeliveryChallanItem(item)
            }

            logger.debug("[APP] Delivery challan created with ID: ${deliveryChallan.id}")
            return deliveryChallan
        } catch (e: Exception) {
            logger.error("[APP] Error creating delivery challan with ID: ${deliveryChallan.id}", e)
            throw e
        }
    }

    private fun createDeliveryChallanItem(item: DeliveryChallanItem) {
        val sql = """
            INSERT INTO delivery_challan_item (
                id,
                delivery_challan_id,
                delivery_order_item_id,
                delivering_quantity
            )
            VALUES (
                ?,
                ?,
                ?,
                ?
            )
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            item.id,
            item.deliveryChallanId,
            item.deliveryOrderItemId,
            item.deliveringQuantity
        )
    }

    fun getDeliveryChallan(id: String): DeliveryChallan? {
        try {
            logger.debug("[APP] Fetching delivery challan with ID: $id")

            val challanSql = """
                SELECT *
                FROM delivery_challan
                WHERE id = ?
            """.trimIndent()
            val deliveryChallan = jdbcTemplate.queryForObject(challanSql, { rs, _ -> deliveryChallanRowMapper(rs) }, id) ?: return null

            val items = getDeliveryChallanItems(id)

            return deliveryChallan.copy(
                deliveryChallanItems = items
            ).also {
                logger.info("[APP] Delivery challan found with ID: $id")
            }

        } catch (e: Exception) {
            logger.error("[APP] Error fetching delivery challan with ID: $id", e)
            throw e
        }
    }

    private fun getDeliveryChallanItems(deliveryChallanId: String): List<DeliveryChallanItem> {
        val sql = """
            SELECT *
            FROM delivery_challan_item
            WHERE delivery_challan_id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ -> deliveryChallanItemRowMapper(rs) }, deliveryChallanId)
    }

    @Transactional
    fun updateDeliveryChallan(deliveryChallan: DeliveryChallan): DeliveryChallan {
        try {
            logger.debug("[APP] Updating delivery challan with ID: ${deliveryChallan.id}")

            val sql = """
                UPDATE delivery_challan 
                SET
                    delivery_order_id = ?,
                    date_of_challan = ?,
                    status = ?,
                    updated_at = ?
                WHERE id = ?
            """.trimIndent()

            jdbcTemplate.update(
                sql,
                deliveryChallan.deliveryOrderId,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.updatedAt,
                deliveryChallan.id
            )

            // Handle delivery challan items
            val existingItems = getDeliveryChallanItems(deliveryChallan.id!!)
            val newItems = deliveryChallan.deliveryChallanItems

            val existingItemIds = existingItems.mapNotNull { it.id }.toSet()
            val newItemIds = newItems.mapNotNull { it.id }.toSet()

            val itemsToCreate = newItems.filter { it.id == null || !existingItemIds.contains(it.id) }
            val itemsToUpdate = newItems.filter { it.id != null && existingItemIds.contains(it.id) }
            val itemsToDelete = existingItems.filter { it.id != null && !newItemIds.contains(it.id) }

            // Delete removed items
            if (itemsToDelete.isNotEmpty()) {
                val deleteSql = """
                    DELETE FROM delivery_challan_item
                    WHERE id = ?
                """.trimIndent()
                itemsToDelete.forEach { item ->
                    jdbcTemplate.update(deleteSql, item.id)
                }
            }

            // Update existing items
            val updateItemSql = """
                UPDATE delivery_challan_item 
                SET
                    delivering_quantity = ?
                WHERE id = ?
            """.trimIndent()

            itemsToUpdate.forEach { item ->
                jdbcTemplate.update(
                    updateItemSql,
                    item.deliveringQuantity,
                    item.id
                )
            }

            // Create new items
            itemsToCreate.forEach { item ->
                createDeliveryChallanItem(item)
            }

            logger.info("[APP] Successfully updated delivery challan ${deliveryChallan.id} with " +
                    "${itemsToCreate.size} new items, ${itemsToUpdate.size} updated items, " +
                    "and ${itemsToDelete.size} deleted items")

            return deliveryChallan

        } catch (e: Exception) {
            logger.error("[APP] Error updating delivery challan with ID: ${deliveryChallan.id}", e)
            throw e
        }
    }

    fun listDeliveryChallans(
        search: String? = null,
        page: Int = 1,
        size: Int = 10
    ): List<DeliveryChallan> {
        val offset = (page - 1) * size

        val sql = """
            SELECT *
            FROM delivery_challan
            WHERE (? IS NULL OR status ILIKE ?)
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
        """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ -> deliveryChallanRowMapper(rs) }, search, search, size, offset).also {
            logger.info("[APP] Retrieved ${it.size} delivery challans for page $page with size $size and search $search")
        }
    }
}
