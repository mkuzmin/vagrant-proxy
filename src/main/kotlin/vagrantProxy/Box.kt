package vagrantProxy

data class Box(
    val name: String,
    val versions: List<Version>
)

data class Version(
    val version: String,
    val providers: List<Provider>
)

data class Provider(
    val name: String,
    val url: String,
    val checksum: String,
    val checksum_type: String
)
