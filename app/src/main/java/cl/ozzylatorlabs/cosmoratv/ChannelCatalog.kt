package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Cosmora TV V1.2
    // Catálogo centrado en Chile y reproducción dentro de la app.
    // Se eliminaron accesos que solo abrían páginas externas, YouTube y señales extranjeras.
    // FMH Broadcast queda pendiente hasta recibir autorización/licencia para sus señales de cine.
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
            streamUrl = "https://mdstrm.com/live-stream-playlist/5aaabe9e2c56420918184c6d.m3u8",
            websiteUrl = "https://www.tvn.cl/ntv",
            note = "Señal online",
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
