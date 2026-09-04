package cl.ozzylatorlabs.cosmoratv

data class Channel(
    val name: String,
    val category: String,
    val streamUrl: String? = null,
    val websiteUrl: String? = null,
    val note: String = "En vivo",
    val verifiedDirectStream: Boolean = false
) {
    val playsInsideApp: Boolean get() = !streamUrl.isNullOrBlank()
}
