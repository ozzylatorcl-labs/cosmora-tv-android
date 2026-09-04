package cl.ozzylatorlabs.cosmoratv

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

private val Bg = Color(0xFF080B12)
private val Panel = Color(0xFF141A26)
private val Panel2 = Color(0xFF202B3E)
private val Focus = Color(0xFF4D8DFF)
private val Muted = Color(0xFFAAB4C6)
private val Live = Color(0xFFE63C4C)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { CosmoraRoot() }
    }
}

@Composable
private fun CosmoraRoot() {
    var splash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(900)
        splash = false
    }

    if (splash) {
        Box(
            Modifier.fillMaxSize().background(Bg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.cosmora_icon),
                contentDescription = "Cosmora TV",
                modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        CosmoraApp()
    }
}

@Composable
private fun CosmoraApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val channels = remember { ChannelCatalog.channels }

    var selected by remember { mutableIntStateOf(0) }
    var streamAttempt by remember { mutableIntStateOf(0) }
    var fullScreen by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(selected) {
        streamAttempt = 0
        error = null
    }

    LaunchedEffect(selected, streamAttempt) {
        error = null
        val channel = channels[selected]
        val sources = channel.playbackUrls

        if (sources.isEmpty()) {
            player.stop()
            player.clearMediaItems()
            error = "Esta señal no tiene una fuente reproducible"
            return@LaunchedEffect
        }

        val source = sources[streamAttempt.coerceIn(0, sources.lastIndex)]
        player.stop()
        player.clearMediaItems()
        player.setMediaItem(MediaItem.fromUri(source))
        player.prepare()
        player.play()
    }

    DisposableEffect(player, selected, streamAttempt) {
        val listener = object : Player.Listener {
            override fun onPlayerError(playbackError: PlaybackException) {
                val sources = channels[selected].playbackUrls
                if (streamAttempt < sources.lastIndex) {
                    error = null
                    streamAttempt += 1
                } else {
                    error = "La señal no está disponible en este momento"
                }
            }
        }

        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    LaunchedEffect(muted) {
        player.volume = if (muted) 0f else 1f
    }

    fun changeChannel(delta: Int) {
        selected = (selected + delta + channels.size) % channels.size
    }

    BackHandler(fullScreen) {
        fullScreen = false
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Panel,
            primary = Focus
        )
    ) {
        if (fullScreen) {
            FullScreenPlayer(
                player = player,
                channel = channels[selected],
                error = error,
                muted = muted,
                onPrevious = { changeChannel(-1) },
                onNext = { changeChannel(1) },
                onMute = { muted = !muted },
                onExit = { fullScreen = false }
            )
        } else {
            BoxWithConstraints(Modifier.fillMaxSize().background(Bg)) {
                val tv = maxWidth >= 820.dp

                if (tv) {
                    Row(
                        Modifier.fillMaxSize().padding(22.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        TvRail(Modifier.width(185.dp))
                        MainContent(
                            channels = channels,
                            selected = selected,
                            player = player,
                            error = error,
                            muted = muted,
                            tv = true,
                            onSelect = { selected = it },
                            onPrevious = { changeChannel(-1) },
                            onNext = { changeChannel(1) },
                            onMute = { muted = !muted },
                            onFullScreen = { fullScreen = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    MainContent(
                        channels = channels,
                        selected = selected,
                        player = player,
                        error = error,
                        muted = muted,
                        tv = false,
                        onSelect = { selected = it },
                        onPrevious = { changeChannel(-1) },
                        onNext = { changeChannel(1) },
                        onMute = { muted = !muted },
                        onFullScreen = { fullScreen = true },
                        modifier = Modifier.fillMaxSize().padding(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FullScreenPlayer(
    player: ExoPlayer,
    channel: Channel,
    error: String?,
    muted: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onExit: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        PlayerPanel(
            player = player,
            channel = channel,
            error = error,
            modifier = Modifier.fillMaxSize(),
            rounded = false,
            onDoubleTap = onExit
        )

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            color = Color(0xB0000000),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(channel.name, color = Color.White, fontSize = 20.sp)
                Text(channel.category, color = Color(0xFFD3D7DF), fontSize = 11.sp)
            }
        }

        FocusButton(
            onClick = onExit,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.FullscreenExit, null)
            Spacer(Modifier.width(6.dp))
            Text("Salir")
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            color = Color(0xB0000000),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, null)
                    Spacer(Modifier.width(5.dp))
                    Text("Anterior")
                }

                FocusButton(onClick = {
                    if (player.isPlaying) player.pause() else player.play()
                }) {
                    Icon(if (player.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(5.dp))
                    Text(if (player.isPlaying) "Pausa" else "Play")
                }

                FocusButton(onClick = onNext) {
                    Text("Siguiente")
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.Default.SkipNext, null)
                }

                FocusButton(onClick = onMute) {
                    Icon(if (muted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null)
                }
            }
        }
    }
}

@Composable
private fun TvRail(modifier: Modifier) {
    Column(
        modifier
            .fillMaxHeight()
            .background(Panel, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.cosmora_icon),
            contentDescription = "Cosmora TV",
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(10.dp),
            contentScale = ContentScale.Fit
        )
        NavRow("Inicio", Icons.Default.Home, true)
        NavRow("Canales", Icons.Default.LiveTv, false)
        Spacer(Modifier.weight(1f))
        Text(
            "Ozzylator Labs",
            color = Muted,
            fontSize = 12.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun NavRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (active) Panel2 else Color.Transparent, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (active) Color.White else Muted)
        Spacer(Modifier.width(9.dp))
        Text(text, color = if (active) Color.White else Muted)
    }
}

@Composable
private fun MainContent(
    channels: List<Channel>,
    selected: Int,
    player: ExoPlayer,
    error: String?,
    muted: Boolean,
    tv: Boolean,
    onSelect: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onFullScreen: () -> Unit,
    modifier: Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Header(channels.size)

        PlayerPanel(
            player = player,
            channel = channels[selected],
            error = error,
            modifier = if (tv) {
                Modifier.fillMaxWidth().weight(1f)
            } else {
                Modifier.fillMaxWidth().aspectRatio(16f / 9f)
            },
            onDoubleTap = onFullScreen
        )

        Controls(
            player = player,
            muted = muted,
            onPrevious = onPrevious,
            onNext = onNext,
            onMute = onMute,
            onFullScreen = onFullScreen
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Canales", color = Color.White, fontSize = 22.sp, modifier = Modifier.weight(1f))
            Text("${channels.size} señales", color = Muted, fontSize = 13.sp)
        }

        if (tv) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(82.dp)
            ) {
                itemsIndexed(channels) { index, channel ->
                    ChannelCard(
                        channel = channel,
                        selected = index == selected,
                        onClick = { onSelect(index) },
                        modifier = Modifier.width(190.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                itemsIndexed(channels) { index, channel ->
                    ChannelCard(
                        channel = channel,
                        selected = index == selected,
                        onClick = { onSelect(index) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(count: Int) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.cosmora_icon),
            contentDescription = "Cosmora TV",
            modifier = Modifier.size(52.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text("Cosmora TV", color = Color.White, fontSize = 29.sp)
            Text("Tu universo en una pantalla · $count señales", color = Muted, fontSize = 13.sp)
        }
        Surface(color = Live, shape = RoundedCornerShape(18.dp)) {
            Text(
                "EN VIVO",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun PlayerPanel(
    player: ExoPlayer,
    channel: Channel,
    error: String?,
    modifier: Modifier,
    rounded: Boolean = true,
    onDoubleTap: (() -> Unit)? = null
) {
    val shape = if (rounded) RoundedCornerShape(22.dp) else RoundedCornerShape(0.dp)

    Box(modifier.background(Color.Black, shape)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { it.player = player }
        )

        if (onDoubleTap != null) {
            Box(
                Modifier
                    .matchParentSize()
                    .pointerInput(onDoubleTap) {
                        detectTapGestures(
                            onDoubleTap = { onDoubleTap() }
                        )
                    }
            )
        }

        if (rounded) {
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                color = Color(0xCC10141D),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text(channel.name, color = Color.White, fontSize = 17.sp)
                    Text(channel.category, color = Muted, fontSize = 10.sp)
                }
            }
        }

        if (error != null) {
            Box(
                Modifier.fillMaxSize().background(Color(0xB810141D)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SignalWifiConnectedNoInternet4, null, tint = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun Controls(
    player: ExoPlayer,
    muted: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMute: () -> Unit,
    onFullScreen: () -> Unit
) {
    var playing by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FocusButton(onClick = onPrevious) {
                Icon(Icons.Default.SkipPrevious, null)
                Spacer(Modifier.width(5.dp))
                Text("Anterior")
            }
        }
        item {
            FocusButton(onClick = {
                if (player.isPlaying) player.pause() else player.play()
            }) {
                Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                Spacer(Modifier.width(5.dp))
                Text(if (playing) "Pausa" else "Play")
            }
        }
        item {
            FocusButton(onClick = onMute) {
                Icon(if (muted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null)
                Spacer(Modifier.width(5.dp))
                Text(if (muted) "Sonido" else "Silenciar")
            }
        }
        item {
            FocusButton(onClick = onFullScreen) {
                Icon(Icons.Default.Fullscreen, null)
                Spacer(Modifier.width(5.dp))
                Text("Pantalla")
            }
        }
        item {
            FocusButton(onClick = onNext) {
                Text("Siguiente")
                Spacer(Modifier.width(5.dp))
                Icon(Icons.Default.SkipNext, null)
            }
        }
    }
}

@Composable
private fun ChannelCard(
    channel: Channel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val border = when {
        focused -> Focus
        selected -> Color(0xFF5573A7)
        else -> Color.Transparent
    }

    Row(
        modifier
            .heightIn(min = 66.dp)
            .background(if (focused || selected) Panel2 else Panel, RoundedCornerShape(17.dp))
            .border(if (focused) 3.dp else 1.dp, border, RoundedCornerShape(17.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(42.dp).background(Color(0xFF34425C), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.LiveTv, null, tint = Color.White)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(channel.name, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(channel.category, color = Muted, fontSize = 10.sp, maxLines = 1)
        }
        if (selected) {
            Icon(Icons.Default.PlayCircle, null, tint = Focus, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun FocusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    Row(
        modifier
            .background(if (focused) Color(0xFF2B3B59) else Panel2, RoundedCornerShape(15.dp))
            .border(
                if (focused) 3.dp else 1.dp,
                if (focused) Focus else Color(0xFF39445A),
                RoundedCornerShape(15.dp)
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
