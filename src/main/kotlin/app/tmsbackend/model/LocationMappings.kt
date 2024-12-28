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


data class ListDistrictsInput(
    val states: List<String> = emptyList()
)

data class ListTalukasInput(
    val states: List<String> = emptyList(),
    val districts: List<String> = emptyList()
)

data class ListCitiesInput(
    val states: List<String> = emptyList(),
    val districts: List<String> = emptyList(),
    val talukas: List<String> = emptyList()
)
