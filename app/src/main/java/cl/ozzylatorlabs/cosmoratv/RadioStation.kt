package cl.ozzylatorlabs.cosmoratv

data class RadioStation(
    val id: String,
    val name: String,
    val subtitle: String,
    val streamUrl: String,
    val metadataUrl: String? = null,
    val artworkUrl: String? = null,
    val websiteUrl: String? = null
)

object RadioCatalog {
    val stations = listOf(
        RadioStation(
            id = "radio-s474n4zz",
            name = "Radio S474N4zZ",
            subtitle = "Rock & Metal · Villa Alemana, Chile",
            streamUrl = "https://stream.zeno.fm/fbf9aexghzzuv",
            metadataUrl = "https://api.zeno.fm/mounts/metadata/subscribe/fbf9aexghzzuv",
            artworkUrl = "https://radiosatanaz.ozzylatorcl.workers.dev/assets/logo-radio-s474n4zz-transparent.png",
            websiteUrl = "https://radiosatanaz.ozzylatorcl.workers.dev/"
        )
    )
}
