package app.tmsbackend.repository

import app.tmsbackend.model.DeliveryOrder
import app.tmsbackend.model.DeliveryOrderItem
import app.tmsbackend.model.DeliveryOrderSection
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Repository
class DeliveryOrderRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(DeliveryOrderRepository::class.java)

    private fun deliveryOrderRowMapper(rs: ResultSet): DeliveryOrder {
        return DeliveryOrder(
            id = rs.getString("id"),
            contractId = rs.getString("contract_id"),
            partyId = rs.getString("party_id"),
            dateOfContract = rs.getLong("date_of_contract"),
            status = rs.getString("status"),
            grandTotalQuantity = 0.0,
            grandTotalDeliveredQuantity = 0.0,
            grandTotalInProgressQuantity = 0.0,
            createdAt = rs.getLong("created_at"),
            updatedAt = rs.getLong("updated_at"),
            deliveryOrderSections = emptyList()
        )
    }

    private fun deliveryOrderItemRowMapper(rs: ResultSet): DeliveryOrderItem {
        return DeliveryOrderItem(
            id = rs.getString("id"),
            deliveryOrderId = rs.getString("delivery_order_id"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("location_id"),
            materialId = rs.getString("material_id"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("due_date"),
            status = rs.getString("status"),
            deliveredQuantity = 0.0,
            inProgressQuantity = 0.0
        )
    }

    fun createDeliveryOrder(deliveryOrder: DeliveryOrder): DeliveryOrder {
        try {
            logger.debug("[APP] Creating delivery order with ID: ${deliveryOrder.id}")

            val sql = """
                INSERT INTO delivery_orders (
                    id, contract_id, party_id, date_of_contract, status, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

            jdbcTemplate.update(
                sql,
                deliveryOrder.id,
                deliveryOrder.contractId,
                deliveryOrder.partyId,
                deliveryOrder.dateOfContract,
                deliveryOrder.status,
                deliveryOrder.createdAt,
                deliveryOrder.updatedAt
            )

            deliveryOrder.deliveryOrderSections.forEach { section ->
                section.deliveryOrderItems.forEach { item ->
                    createDeliveryOrderItem(item)
                }
            }

            logger.debug("[APP] Delivery order created with ID: ${deliveryOrder.id}")
            return deliveryOrder
        } catch (e: Exception) {
            logger.error("[APP] Error creating delivery order with ID: ${deliveryOrder.id}", e)
            throw e
        }
    }

    private fun createDeliveryOrderItem(item: DeliveryOrderItem) {
        val sql = """
            INSERT INTO delivery_order_items (
                id, delivery_order_id, district, taluka, location_id, 
                material_id, quantity, rate, due_date, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            item.id,
            item.deliveryOrderId,
            item.district,
            item.taluka,
            item.locationId,
            item.materialId,
            item.quantity,
            item.rate,
            item.dueDate,
            item.status
        )
    }

    fun getDeliveryOrder(id: String): DeliveryOrder? {
        try {
            logger.debug("[APP] Fetching delivery order with ID: $id")

            val orderSql = "SELECT * FROM delivery_orders WHERE id = ?"
            val deliveryOrder = jdbcTemplate.queryForObject(orderSql, { rs, _ -> deliveryOrderRowMapper(rs) }, id) ?: return null

            val items = getDeliveryOrderItems(id)

            val sections = items.groupBy { it.district }.map { (district, districtItems) ->
                DeliveryOrderSection(
                    district = district,
                    totalQuantity = districtItems.sumOf { it.quantity },
                    totalDeliveredQuantity = districtItems.sumOf { it.deliveredQuantity },
                    totalInProgressQuantity = districtItems.sumOf { it.inProgressQuantity },
                    status = districtItems.firstOrNull()?.status ?: "", // TODO: make this computed
                    deliveryOrderItems = districtItems
                )
            }

            return deliveryOrder.copy(
                deliveryOrderSections = sections,
                grandTotalQuantity = sections.sumOf { it.totalQuantity },
                grandTotalDeliveredQuantity = sections.sumOf { it.totalDeliveredQuantity },
                grandTotalInProgressQuantity = sections.sumOf { it.totalInProgressQuantity }
            ).also {
                logger.info("[APP] Delivery order found with ID: $id")
            }

        } catch (e: Exception) {
            logger.error("[APP] Error fetching delivery order with ID: $id", e)
            throw e
        }
    }

    private fun getDeliveryOrderItems(deliveryOrderId: String): List<DeliveryOrderItem> {
        val sql = "SELECT * FROM delivery_order_items WHERE delivery_order_id = ?"
        return jdbcTemplate.query(sql, { rs, _ -> deliveryOrderItemRowMapper(rs) }, deliveryOrderId)
    }

    @Transactional
    fun updateDeliveryOrder(deliveryOrder: DeliveryOrder): DeliveryOrder {
        try {
            logger.debug("[APP] Updating delivery order with ID: ${deliveryOrder.id}")

            // 1. Update the main delivery order record
            val orderSql = """
                UPDATE delivery_orders 
                SET contract_id = ?, 
                    party_id = ?, 
                    date_of_contract = ?, 
                    status = ?, 
                    updated_at = ?
                WHERE id = ?
            """.trimIndent()

            jdbcTemplate.update(
                orderSql,
                deliveryOrder.contractId,
                deliveryOrder.partyId,
                deliveryOrder.dateOfContract,
                deliveryOrder.status,
                deliveryOrder.updatedAt,
                deliveryOrder.id
            )

            // 2. Get existing items from database
            val existingItems = getDeliveryOrderItems(deliveryOrder.id!!)

            // 3. Get new items from the updated delivery order
            val newItems = deliveryOrder.deliveryOrderSections.flatMap { it.deliveryOrderItems }

            // 4. Categorize items
            val existingItemIds = existingItems.mapNotNull { it.id }.toSet()
            val newItemIds = newItems.mapNotNull { it.id }.toSet()

            val itemsToCreate = newItems.filter { it.id == null || !existingItemIds.contains(it.id) }
            val itemsToUpdate = newItems.filter { it.id != null && existingItemIds.contains(it.id) }
            val itemsToDelete = existingItems.filter { it.id != null && !newItemIds.contains(it.id) }

            // 5. Delete removed items
            if (itemsToDelete.isNotEmpty()) {
                val deleteSql = "DELETE FROM delivery_order_items WHERE id = ?"
                itemsToDelete.forEach { item ->
                    jdbcTemplate.update(deleteSql, item.id)
                }
            }

            // 6. Update existing items
            val updateItemSql = """
                UPDATE delivery_order_items 
                SET district = ?,
                    taluka = ?,
                    location_id = ?,
                    material_id = ?,
                    quantity = ?,
                    rate = ?,
                    due_date = ?,
                    status = ?
                WHERE id = ?
            """.trimIndent()

            itemsToUpdate.forEach { item ->
                jdbcTemplate.update(
                    updateItemSql,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate,
                    item.dueDate,
                    item.status,
                    item.id
                )
            }

            // 7. Create new items
            val createItemSql = """
                INSERT INTO delivery_order_items (
                    id, delivery_order_id, district, taluka, location_id,
                    material_id, quantity, rate, due_date, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

            itemsToCreate.forEach { item ->
                jdbcTemplate.update(
                    createItemSql,
                    item.id,
                    deliveryOrder.id,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate,
                    item.dueDate,
                    item.status
                )
            }

            logger.info("[APP] Successfully updated delivery order ${deliveryOrder.id} with " +
                    "${itemsToCreate.size} new items, ${itemsToUpdate.size} updated items, " +
                    "and ${itemsToDelete.size} deleted items")

            return deliveryOrder

        } catch (e: Exception) {
            logger.error("[APP] Error updating delivery order with ID: ${deliveryOrder.id}", e)
            throw e
        }
    }
    fun listDeliveryOrders(page: Int, size: Int): List<DeliveryOrder> {
        try {
            logger.debug("[APP] Listing delivery orders with page: $page, size: $size")

            val sql = "SELECT * FROM delivery_orders ORDER BY created_at DESC LIMIT ? OFFSET ?"
            val offset = (page - 1) * size

            return jdbcTemplate.query(sql, { rs, _ ->
                val order = deliveryOrderRowMapper(rs)
                val items = getDeliveryOrderItems(order.id.toString())

                val sections = items.groupBy { it.district }.map { (district, districtItems) ->
                    DeliveryOrderSection(
                        district = district,
                        totalQuantity = districtItems.sumOf { it.quantity },
                        totalDeliveredQuantity = districtItems.sumOf { it.deliveredQuantity },
                        totalInProgressQuantity = districtItems.sumOf { it.inProgressQuantity },
                        status = districtItems.firstOrNull()?.status ?: "",
                        deliveryOrderItems = districtItems
                    )
                }

                order.copy(
                    deliveryOrderSections = sections,
                    grandTotalQuantity = sections.sumOf { it.totalQuantity },
                    grandTotalDeliveredQuantity = sections.sumOf { it.totalDeliveredQuantity },
                    grandTotalInProgressQuantity = sections.sumOf { it.totalInProgressQuantity }
                )
            }, size, offset).also {
                logger.info("[APP] Listed ${it.size} delivery orders")
            }
        } catch (e: Exception) {
            logger.error("[APP] Error listing delivery orders", e)
            throw e
        }
    }
}