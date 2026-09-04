package cl.ozzylatorlabs.cosmoratv

object ChannelCatalog {
    // Cosmora TV V1.5
    // Catálogo chileno reproducido de forma nativa por HLS dentro de la app.
    // Las señales principales incluyen fuentes alternativas para reducir caídas.
    val channels = listOf(
        Channel(
            name = "TVN",
            category = "Chile · TV Abierta · Nacional",
            streamUrl = "https://mdstrm.com/live-stream-playlist-v/555c9a91eb4886825b07ee7b.m3u8",
            backupStreamUrls = listOf(
                "https://iptv2.intersurtv.cl/TVN/index.m3u8",
                "https://marine2.miplay.cl/tvnchile/index.m3u8"
            ),
            websiteUrl = "https://www.tvn.cl/en-vivo",
            note = "Señal en vivo"
        ),
        Channel(
            name = "Mega",
            category = "Chile · TV Abierta · Nacional",
            streamUrl = "https://unlimited1-cl-isp.dps.live/mega/mega.smil/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://unlimited2-cl-isp.dps.live/mega/mega.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.mega.cl/",
            note = "Señal en vivo"
        ),
        Channel(
            name = "Chilevisión",
            category = "Chile · TV Abierta · Nacional",
            streamUrl = "https://redirector.rudo.video/hls-video/10b92cafdf3646cbc1e727f3dc76863621a327fd/chv/chv.smil/playlist.m3u8",
            websiteUrl = "https://www.chilevision.cl/senal-online",
            note = "Señal en vivo"
        ),
        Channel(
            name = "Canal 13",
            category = "Chile · TV Abierta · Nacional",
            streamUrl = "https://redirector.dps.live/hls/13cl/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://redirector.rudo.video/hls-video/ey6283je82983je9823je8jowowiekldk9838274/13popup/13popup.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.13.cl/en-vivo",
            note = "Señal en vivo"
        ),
        Channel(
            name = "La Red",
            category = "Chile · TV Abierta · Entretención",
            streamUrl = "https://unlimited1-cl-isp.dps.live/lared/lared.smil/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://alba-cl-lared-lared.stream.mediatiquestream.com/index.m3u8",
                "https://unlimited2-cl-isp.dps.live/lared/lared.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.lared.cl/senal-online",
            note = "Señal en vivo"
        ),
        Channel(
            name = "TV+",
            category = "Chile · TV Abierta · Entretención",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5c0e8b19e4c87f3f2d3e6a59.m3u8",
            backupStreamUrls = listOf(
                "https://jireh-8-hls-video-us-isp.dps.live/hls-video/ey6283je82983je9823je8jowowiekldk9838274/tvmas/tvmas.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.tvmas.tv/",
            note = "Señal en vivo"
        ),
        Channel(
            name = "24 Horas",
            category = "Chile · Noticias · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            backupStreamUrls = listOf(
                "https://mdstrm.com/live-stream-playlist/57d1a22064f5d85712b20dab.m3u8"
            ),
            websiteUrl = "https://www.24horas.cl/envivo/",
            note = "Noticias 24/7"
        ),
        Channel(
            name = "TVN3",
            category = "Chile · TVN",
            streamUrl = "https://mdstrm.com/live-stream-playlist/5653641561b4eba30a7e4929.m3u8",
            websiteUrl = "https://www.tvn.cl/tvn3",
            note = "Señal en vivo"
        ),
        Channel(
            name = "TV Chile",
            category = "Chile · TVN Internacional",
            streamUrl = "https://mdstrm.com/live-stream-playlist/533adcc949386ce765657d7c.m3u8",
            websiteUrl = "https://www.tvn.cl/tvchile/envivo",
            note = "Señal en vivo"
        ),
        Channel(
            name = "NTV",
            category = "Chile · Cultura · TVN",
            streamUrl = "https://marine2.miplay.cl/ntv/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://mdstrm.com/live-stream-playlist/5aaabe9e2c56420918184c6d.m3u8"
            ),
            websiteUrl = "https://www.tvn.cl/ntv",
            note = "Señal en vivo"
        ),
        Channel(
            name = "Canal 9 Bío Bío",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/c9/c9.smil/playlist.m3u8",
            websiteUrl = "https://www.canal9.cl/",
            note = "Señal en vivo"
        ),
        Channel(
            name = "TVU",
            category = "Chile · Universidad de Concepción",
            streamUrl = "https://unlimited6-cl.dps.live/tvu/tvu.smil/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://unlimited1-cl-isp.dps.live/tvu/tvu.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.tvu.cl/",
            note = "Señal en vivo"
        ),
        Channel(
            name = "UChile TV",
            category = "Chile · Universidad de Chile",
            streamUrl = "https://unlimited1-us.dps.live/uchiletv/uchiletv.smil/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://unlimited1-cl-isp.dps.live/uchiletv/uchiletv.smil/playlist.m3u8"
            ),
            websiteUrl = "https://tv.uchile.cl/",
            note = "Señal en vivo"
        ),
        Channel(
            name = "Antofagasta TV",
            category = "Chile · Regional",
            streamUrl = "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8",
            websiteUrl = "https://www.antofagasta.tv/senal-en-vivo",
            note = "Señal en vivo"
        ),
        Channel(
            name = "UCV TV",
            category = "Chile · Valparaíso · Cultural",
            streamUrl = "https://unlimited2-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8",
            backupStreamUrls = listOf(
                "https://unlimited1-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8"
            ),
            websiteUrl = "https://www.ucvtv.cl/",
            note = "Señal en vivo"
        )
    )
}
