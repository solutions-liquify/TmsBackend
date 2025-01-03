package app.tmsbackend.repository

import app.tmsbackend.model.DeliveryChallan
import app.tmsbackend.model.DeliveryChallanItem
import app.tmsbackend.model.ListDeliveryChallanOutputRecord
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
            createdAt = rs.getLong("created_at"),
            updatedAt = rs.getLong("updated_at"),
            partyName = rs.getString("party_name")
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
            deliveringQuantity = rs.getDouble("delivering_quantity"),
            inProgressQuantity = rs.getDouble("in_progress_quantity"),
            deliveredQuantity = rs.getDouble("delivered_quantity"),
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
            INSERT INTO delivery_challan_items (
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
            SELECT 
                dc.*, 
                p.name AS party_name 
            FROM 
                delivery_challan AS dc
            JOIN 
                delivery_orders AS d_orders ON dc.delivery_order_id = d_orders.id
            JOIN 
                parties AS p ON d_orders.party_id = p.id
            WHERE 
                dc.id = ?;
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
    SELECT 
        dci.*,
        doi.district,
        doi.taluka,
        loc.name AS location_name,
        mat.name AS material_name,
        doi.quantity,
        doi.rate, 
        doi.due_date, 
        doi.status,
        COALESCE(SUM(CASE 
            WHEN dc.status = 'delivered' THEN dci_sub.delivering_quantity 
            ELSE 0 
        END), 0) AS delivered_quantity,
        COALESCE(SUM(CASE 
            WHEN dc.status = 'in-progress' THEN dci_sub.delivering_quantity 
            ELSE 0 
        END), 0) AS in_progress_quantity
    FROM 
        delivery_challan_items dci
    JOIN 
        delivery_order_items doi ON dci.delivery_order_item_id = doi.id
    JOIN 
        locations loc ON doi.location_id = loc.id
    JOIN 
        materials mat ON doi.material_id = mat.id
    LEFT JOIN 
        delivery_challan_items dci_sub ON dci_sub.delivery_order_item_id = doi.id
    LEFT JOIN 
        delivery_challan dc ON dci_sub.delivery_challan_id = dc.id
    WHERE 
        dci.delivery_challan_id = ?
    GROUP BY 
        dci.id, doi.id, doi.district, doi.taluka, loc.name, mat.name, 
        doi.quantity, doi.rate, doi.due_date, doi.status
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
                    DELETE FROM delivery_challan_items
                    WHERE id = ?
                """.trimIndent()
                itemsToDelete.forEach { item ->
                    jdbcTemplate.update(deleteSql, item.id)
                }
            }

            // Update existing items
            val updateItemSql = """
                UPDATE delivery_challan_items
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

            logger.info(
                "[APP] Successfully updated delivery challan ${deliveryChallan.id} with " +
                        "${itemsToCreate.size} new items, ${itemsToUpdate.size} updated items, " +
                        "and ${itemsToDelete.size} deleted items"
            )

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
    ): List<ListDeliveryChallanOutputRecord> {
        val offset = (page - 1) * size

        val sql = """
        SELECT 
            dc.id,
            dc.delivery_order_id,
            dc.date_of_challan,
            dc.status,
            p.name AS party_name
        FROM 
            delivery_challan dc
        LEFT JOIN 
            delivery_orders d_order ON dc.delivery_order_id = d_order.id
        LEFT JOIN 
            parties p ON d_order.party_id = p.id
        ORDER BY 
            dc.created_at DESC
        LIMIT ? OFFSET ?
    """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ ->
            ListDeliveryChallanOutputRecord(
                id = rs.getString("id"),
                deliveryOrderId = rs.getString("delivery_order_id"),
                dateOfChallan = rs.getLong("date_of_challan"),
                status = rs.getString("status"),
                partyName = rs.getString("party_name"),
                totalDeliveringQuantity = 0.0
            )
        }, size, offset).also {
            logger.info("[APP] Retrieved ${it.size} delivery challans for page $page with size $size")
        }
    }
}

