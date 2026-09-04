package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Cosmora TV V1.4
    // Catálogo chileno: señales directas y players oficiales reproducidos dentro de la app.
    // Sin YouTube, sin señales extranjeras y sin accesos que abran el navegador externo.
    val channels = listOf(
        Channel(
            name = "24 Horas",
            category = "Chile · Noticias · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            websiteUrl = "https://www.24horas.cl/envivo/",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "La Red",
            category = "Chile · TV Abierta · Entretención",
            webEmbedUrl = "https://www.lared.cl/player",
            websiteUrl = "https://www.lared.cl/",
            note = "Player oficial dentro de Cosmora"
        ),
        Channel(
            name = "TV+",
            category = "Chile · TV Abierta · Entretención",
            webEmbedUrl = "https://rudo.video/live/tvmas",
            websiteUrl = "https://www.tvmas.tv/page/en-vivo/",
            note = "Player oficial dentro de Cosmora"
        ),
        Channel(
            name = "TVN",
            category = "Chile · TV Abierta · Nacional",
            webEmbedUrl = "https://live.tvn.cl/?volume=0",
            websiteUrl = "https://www.tvn.cl/",
            note = "Player oficial dentro de Cosmora"
        ),
        Channel(
            name = "TVN3",
            category = "Chile · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5653641561b4eba30a7e4929.m3u8",
            websiteUrl = "https://www.tvn.cl/tvn3",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TV Chile",
            category = "Chile · TVN Internacional",
            streamUrl = "https://mdstrm.com/live-stream-playlist/533adcc949386ce765657d7c.m3u8",
            websiteUrl = "https://www.tvn.cl/tvchile/envivo",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "NTV",
            category = "Chile · Cultura · TVN",
            streamUrl = "https://marine2.miplay.cl/ntv/playlist.m3u8",
            websiteUrl = "https://www.tvn.cl/ntv",
            note = "Señal online actual",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Canal 9 Bío Bío",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/c9/c9.smil/playlist.m3u8",
            websiteUrl = "https://www.canal9.cl/",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "TVU",
            category = "Chile · Universidad de Concepción",
            streamUrl = "https://unlimited6-cl.dps.live/tvu/tvu.smil/playlist.m3u8",
            websiteUrl = "https://www.tvu.cl/",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "UChile TV",
            category = "Chile · Universidad de Chile",
            streamUrl = "https://unlimited1-us.dps.live/uchiletv/uchiletv.smil/playlist.m3u8",
            websiteUrl = "https://tv.uchile.cl/",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "Antofagasta TV",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8",
            websiteUrl = "https://www.antofagasta.tv/senal-en-vivo",
            note = "Señal online",
            verifiedDirectStream = true
        ),
        Channel(
            name = "UCV TV",
            category = "Chile · Valparaíso · Cultural",
            streamUrl = "https://unlimited2-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8",
            websiteUrl = "https://www.ucvtv.cl/",
            note = "Señal online",
            verifiedDirectStream = true
        )
    )
}
