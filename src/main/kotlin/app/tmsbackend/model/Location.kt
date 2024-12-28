package app.tmsbackend.model

data class Location(
    val id: String?,
    val name: String,
    val pointOfContact: String?,
    val contactNumber: String?,
    val email: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val state: String?,
    val district: String,
    val taluka: String,
    val city: String?,
    val pinCode: String?,
    val createdAt: Long?
)

data class LocationDTO(
    val id: String,
    val name: String,
    val pointOfContact: String?,
    val contactNumber: String?,
    val email: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val state: String?,
    val district: String,
    val taluka: String,
    val city: String?,
    val pinCode: String?,
    val createdAt: Long
) {
    fun toLocation(): Location {
        return Location(
            id = id,
            name = name,
            pointOfContact = pointOfContact,
            contactNumber = contactNumber,
            email = email,
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            state = state,
            district = district,
            taluka = taluka,
            city = city,
            pinCode = pinCode,
            createdAt = createdAt
        )
    }
}

data class ListLocationsInput(
    val search: String = "",
    val page: Int = 1,
    val size: Int = 10,
    val states: List<String> = emptyList(),
    val districts: List<String> = emptyList(),
    val talukas: List<String> = emptyList(),
    val cities: List<String> = emptyList(),
    val returnAll: Boolean = false
)