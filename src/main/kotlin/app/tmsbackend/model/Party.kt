package app.tmsbackend.model

data class Party(
    val id: String?,
    val name: String,
    val pointOfContact: String?,
    val contactNumber: String?,
    val email: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val state: String?,
    val district: String?,
    val taluka: String?,
    val city: String?,
    val pinCode: String?,
    val createdAt: Long?
)

data class PartyDTO(
    val id: String,
    val name: String,
    val pointOfContact: String?,
    val contactNumber: String?,
    val email: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val state: String?,
    val district: String?,
    val taluka: String?,
    val city: String?,
    val pinCode: String?,
    val createdAt: Long
) {
    fun toParty(): Party {
        return Party(
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

data class ListPartiesInput(
    val search: String = "",
    val page: Int = 1,
    val size: Int = 10
)