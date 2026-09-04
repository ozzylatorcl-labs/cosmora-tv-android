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
            subtitle = "Rock & Metal · Villa Alemana",
            streamUrl = "https://stream.zeno.fm/fbf9aexghzzuv",
            metadataUrl = "https://api.zeno.fm/mounts/metadata/subscribe/fbf9aexghzzuv",
            artworkUrl = "https://radiosatanaz.ozzylatorcl.workers.dev/assets/logo-radio-s474n4zz-transparent.png",
            websiteUrl = "https://radiosatanaz.ozzylatorcl.workers.dev/"
        ),
        RadioStation(
            id = "futuro",
            name = "Radio Futuro",
            subtitle = "Rock · Santiago",
            streamUrl = "https://playerservices.streamtheworld.com/api/livestream-redirect/FUTURO_SC",
            websiteUrl = "https://www.futuro.cl/"
        ),
        RadioStation(
            id = "rock-and-pop",
            name = "Rock & Pop",
            subtitle = "Rock & Pop · Santiago",
            streamUrl = "https://playerservices.streamtheworld.com/api/livestream-redirect/ROCK_AND_POP_SC",
            websiteUrl = "https://www.rockandpop.cl/"
        ),
        RadioStation(
            id = "concierto",
            name = "Radio Concierto",
            subtitle = "Clásicos · Rock & Pop",
            streamUrl = "https://playerservices.streamtheworld.com/api/livestream-redirect/CONCIERTO_SC",
            websiteUrl = "https://www.concierto.cl/"
        ),
        RadioStation(
            id = "biobio",
            name = "Radio Bío-Bío",
            subtitle = "Noticias · Santiago",
            streamUrl = "https://redirector.dps.live/biobiosantiago/mp3/icecast.audio",
            websiteUrl = "https://www.biobiochile.cl/"
        ),
        RadioStation(
            id = "cooperativa",
            name = "Radio Cooperativa",
            subtitle = "Noticias · Deportes",
            streamUrl = "https://redirector.dps.live/cooperativafm/aac/icecast.audio",
            websiteUrl = "https://www.cooperativa.cl/"
        ),
        RadioStation(
            id = "adn",
            name = "ADN Radio Chile",
            subtitle = "Noticias · Deportes",
            streamUrl = "https://playerservices.streamtheworld.com/api/livestream-redirect/ADN_SC",
            websiteUrl = "https://www.adnradio.cl/"
        ),
        RadioStation(
            id = "radioactiva",
            name = "RadioActiva",
            subtitle = "Música · Humor",
            streamUrl = "https://playerservices.streamtheworld.com/api/livestream-redirect/ACTIVA_SC",
            websiteUrl = "https://www.radioactiva.cl/"
        )
    )
}
