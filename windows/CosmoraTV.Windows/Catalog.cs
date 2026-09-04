namespace CosmoraTV.Windows;

public sealed record TvChannel(string Name, string Category, IReadOnlyList<string> Sources);
public sealed record RadioStation(string Name, string Subtitle, string StreamUrl, string? ArtworkUrl = null);

public static class Catalog
{
    public static readonly IReadOnlyList<TvChannel> Channels = new List<TvChannel>
    {
        new("TVN", "Chile · TV Abierta · Nacional", new[] {
            "https://mdstrm.com/live-stream-playlist-v/555c9a91eb4886825b07ee7b.m3u8",
            "https://iptv2.intersurtv.cl/TVN/index.m3u8",
            "https://marine2.miplay.cl/tvnchile/index.m3u8"
        }),
        new("Mega", "Chile · TV Abierta · Nacional", new[] {
            "https://unlimited1-cl-isp.dps.live/mega/mega.smil/playlist.m3u8",
            "https://unlimited2-cl-isp.dps.live/mega/mega.smil/playlist.m3u8"
        }),
        new("Chilevisión", "Chile · TV Abierta · Nacional", new[] {
            "https://redirector.rudo.video/hls-video/10b92cafdf3646cbc1e727f3dc76863621a327fd/chv/chv.smil/playlist.m3u8"
        }),
        new("Canal 13", "Chile · TV Abierta · Nacional", new[] {
            "https://redirector.dps.live/hls/13cl/playlist.m3u8",
            "https://redirector.rudo.video/hls-video/ey6283je82983je9823je8jowowiekldk9838274/13popup/13popup.smil/playlist.m3u8"
        }),
        new("La Red", "Chile · TV Abierta · Entretención", new[] {
            "https://unlimited1-cl-isp.dps.live/lared/lared.smil/playlist.m3u8",
            "https://alba-cl-lared-lared.stream.mediatiquestream.com/index.m3u8",
            "https://unlimited2-cl-isp.dps.live/lared/lared.smil/playlist.m3u8"
        }),
        new("TV+", "Chile · TV Abierta · Entretención", new[] {
            "https://mdstrm.com/live-stream-playlist/5c0e8b19e4c87f3f2d3e6a59.m3u8",
            "https://jireh-8-hls-video-us-isp.dps.live/hls-video/ey6283je82983je9823je8jowowiekldk9838274/tvmas/tvmas.smil/playlist.m3u8"
        }),
        new("24 Horas", "Chile · Noticias · TVN", new[] {
            "https://mdstrm.com/live-stream-playlist/689ba606ecfe7915e1f8f741.m3u8",
            "https://mdstrm.com/live-stream-playlist/57d1a22064f5d85712b20dab.m3u8"
        }),
        new("TVN3", "Chile · TVN", new[] {
            "https://mdstrm.com/live-stream-playlist/5653641561b4eba30a7e4929.m3u8"
        }),
        new("TV Chile", "Chile · TVN Internacional", new[] {
            "https://mdstrm.com/live-stream-playlist/533adcc949386ce765657d7c.m3u8"
        }),
        new("NTV", "Chile · Cultura · TVN", new[] {
            "https://marine2.miplay.cl/ntv/playlist.m3u8",
            "https://mdstrm.com/live-stream-playlist/5aaabe9e2c56420918184c6d.m3u8"
        }),
        new("Canal 9 Bío Bío", "Chile · Regional", new[] {
            "https://unlimited6-cl.dps.live/c9/c9.smil/playlist.m3u8"
        }),
        new("TVU", "Chile · Universidad de Concepción", new[] {
            "https://unlimited6-cl.dps.live/tvu/tvu.smil/playlist.m3u8",
            "https://unlimited1-cl-isp.dps.live/tvu/tvu.smil/playlist.m3u8"
        }),
        new("UChile TV", "Chile · Universidad de Chile", new[] {
            "https://unlimited1-us.dps.live/uchiletv/uchiletv.smil/playlist.m3u8",
            "https://unlimited1-cl-isp.dps.live/uchiletv/uchiletv.smil/playlist.m3u8"
        }),
        new("Antofagasta TV", "Chile · Regional", new[] {
            "https://unlimited6-cl.dps.live/atv/atv.smil/playlist.m3u8"
        }),
        new("UCV TV", "Chile · Valparaíso · Cultural", new[] {
            "https://unlimited2-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8",
            "https://unlimited1-cl-isp.dps.live/ucvtv2/ucvtv2.smil/playlist.m3u8"
        })
    };

    public static readonly IReadOnlyList<RadioStation> Radios = new List<RadioStation>
    {
        new("Radio S474N4zZ", "Rock & Metal · Villa Alemana", "https://stream.zeno.fm/fbf9aexghzzuv", "https://radiosatanaz.ozzylatorcl.workers.dev/assets/logo-radio-s474n4zz-transparent.png"),
        new("Radio Futuro", "Rock · Santiago", "https://playerservices.streamtheworld.com/api/livestream-redirect/FUTURO_SC"),
        new("Rock & Pop", "Rock & Pop · Santiago", "https://playerservices.streamtheworld.com/api/livestream-redirect/ROCK_AND_POP_SC"),
        new("Radio Concierto", "Clásicos · Rock & Pop", "https://playerservices.streamtheworld.com/api/livestream-redirect/CONCIERTO_SC"),
        new("Radio Bío-Bío", "Noticias · Santiago", "https://redirector.dps.live/biobiosantiago/mp3/icecast.audio"),
        new("Radio Cooperativa", "Noticias · Deportes", "https://redirector.dps.live/cooperativafm/aac/icecast.audio"),
        new("ADN Radio Chile", "Noticias · Deportes", "https://playerservices.streamtheworld.com/api/livestream-redirect/ADN_SC"),
        new("RadioActiva", "Música · Humor", "https://playerservices.streamtheworld.com/api/livestream-redirect/ACTIVA_SC")
    };
}
