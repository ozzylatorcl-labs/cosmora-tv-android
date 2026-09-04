package cl.ozzylatorlabs.cosmoratv

data class Channel(
    val name: String,
    val category: String,
    val streamUrl: String? = null,
    val backupStreamUrls: List<String> = emptyList(),
    val websiteUrl: String? = null,
    val note: String = "En vivo"
) {
    val playbackUrls: List<String>
        get() = buildList {
            streamUrl?.takeIf { it.isNotBlank() }?.let(::add)
            backupStreamUrls.filter { it.isNotBlank() }.forEach(::add)
        }.distinct()

    val playsInsideApp: Boolean get() = playbackUrls.isNotEmpty()
}
