package app.tmsbackend.model


data class Taluka(
    val name: String,
    val cities: List<String>
)

data class District(
    val name: String,
    val talukas: List<Taluka>
)

data class State(
    val name: String,
    val districts: List<District>
)