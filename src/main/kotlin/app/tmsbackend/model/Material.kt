package app.tmsbackend.model

data class Material(
    val id: String?,
    val name: String,
    val createdAt: Long?
) 

data class MaterialDTO(
    val id: String,
    val name: String,
    val createdAt: Long
) {
    fun toMaterial(): Material {
        return Material(id = id, name = name, createdAt = createdAt)
    }
}   