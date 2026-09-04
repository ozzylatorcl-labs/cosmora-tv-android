package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Catálogo inicial de Cosmora TV.
    // Las señales sin stream directo verificable se abren en su sitio oficial.
    // No se incluyen proxies IPTV, listas Xtream ni retransmisiones de pago.
    val channels = listOf(
        Channel(
            name = "TVN",
            category = "Chile · Señal oficial",
            websiteUrl = "https://www.tvn.cl/en-vivo",
            note = "Abrir señal oficial"
        ),
        Channel(
            name = "Canal 13",
            category = "Chile · Señal oficial",
            websiteUrl = "https://www.13.cl/en-vivo",
            note = "Abrir señal oficial"
        ),
        Channel(
            name = "Canal 9 Bio Bio",
            category = "Chile",
            streamUrl = "https://unlimited6-cl.dps.live/c9/c9.smil/playlist.m3u8",
            websiteUrl = "https://www.canal9.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "UCV TV",
            category = "Chile",
            streamUrl = "https://unlimited2-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8",
            websiteUrl = "https://www.ucvtv.cl/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Antofagasta TV",
            category = "Chile",
            streamUrl = "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8",
            websiteUrl = "https://www.antofagasta.tv/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TV+",
            category = "Chile",
            streamUrl = "https://jireh-8-hls-video-us-isp.dps.live/hls-video/ey6283je82983je9823je8jowowiekldk9838274/tvmas/tvmas.smil/playlist.m3u8",
            websiteUrl = "https://www.tvmas.tv/envivo/",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Supermúsica TV",
            category = "Música",
            streamUrl = "https://backupmaxmedia.hvmultiplay.com/hls/stream4/supermusica.m3u8",
            websiteUrl = "https://supermusica.tv/",
            verifiedDirectStream = true
        )
    )
}
