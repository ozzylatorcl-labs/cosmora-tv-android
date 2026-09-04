package cl.ozzylatorlabs.cosmoratv

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

enum class MediaSection { TV, RADIOS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 616)
        }
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
        Box(Modifier.fillMaxSize().background(Bg), contentAlignment = Alignment.Center) {
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
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val channels = remember { ChannelCatalog.channels }
    val stations = remember { RadioCatalog.stations }

    var section by remember { mutableStateOf(MediaSection.TV) }
    var selected by remember { mutableIntStateOf(0) }
    var streamAttempt by remember { mutableIntStateOf(0) }
    var fullScreen by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var lastBackPress by remember { mutableLongStateOf(0L) }

    val radioStationId by RadioPlaybackState.stationId.collectAsState()
    val radioStationName by RadioPlaybackState.stationName.collectAsState()
    val radioPlaying by RadioPlaybackState.isPlaying.collectAsState()
    val radioError by RadioPlaybackState.error.collectAsState()

    val player = remember {
        ExoPlayer.Builder(context).build().apply { playWhenReady = true }
    }

    DisposableEffect(section, activity) {
        if (section == MediaSection.TV) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose { }
    }

    DisposableEffect(lifecycleOwner, section, player) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && section == MediaSection.TV) {
                player.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(selected) {
        streamAttempt = 0
        error = null
    }

    LaunchedEffect(selected, streamAttempt, section) {
        if (section != MediaSection.TV) return@LaunchedEffect
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

    DisposableEffect(player, selected, streamAttempt, section) {
        val listener = object : Player.Listener {
            override fun onPlayerError(playbackError: PlaybackException) {
                if (section != MediaSection.TV) return
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

    fun stopRadio() {
        context.startService(Intent(context, RadioPlaybackService::class.java).apply {
            action = RadioPlaybackService.ACTION_STOP
        })
    }

    fun switchSection(newSection: MediaSection) {
        if (newSection == section) return
        if (newSection == MediaSection.RADIOS) {
            player.pause()
            fullScreen = false
        } else {
            stopRadio()
        }
        section = newSection
    }

    fun playRadio(station: RadioStation) {
        player.pause()
        val intent = Intent(context, RadioPlaybackService::class.java).apply {
            action = RadioPlaybackService.ACTION_PLAY_STATION
            putExtra(RadioPlaybackService.EXTRA_ID, station.id)
            putExtra(RadioPlaybackService.EXTRA_NAME, station.name)
            putExtra(RadioPlaybackService.EXTRA_SUBTITLE, station.subtitle)
            putExtra(RadioPlaybackService.EXTRA_STREAM, station.streamUrl)
            putExtra(RadioPlaybackService.EXTRA_ARTWORK, station.artworkUrl)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun toggleRadio() {
        context.startService(Intent(context, RadioPlaybackService::class.java).apply {
            action = RadioPlaybackService.ACTION_TOGGLE_PAUSE
        })
    }

    fun changeChannel(delta: Int) {
        selected = (selected + delta + channels.size) % channels.size
    }

    BackHandler(enabled = fullScreen) {
        fullScreen = false
    }

    BackHandler(enabled = !fullScreen) {
        val now = System.currentTimeMillis()
        if (now - lastBackPress <= 1800) {
            activity?.finish()
        } else {
            lastBackPress = now
            Toast.makeText(context, "Presiona Atrás otra vez para salir", Toast.LENGTH_SHORT).show()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Focus)) {
        if (fullScreen && section == MediaSection.TV) {
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
                        TvRail(
                            section = section,
                            onSection = ::switchSection,
                            modifier = Modifier.width(190.dp)
                        )
                        if (section == MediaSection.TV) {
                            TvContent(
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
                                onSection = ::switchSection,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            RadioContent(
                                stations = stations,
                                activeStationId = radioStationId,
                                activeStationName = radioStationName,
                                isPlaying = radioPlaying,
                                error = radioError,
                                tv = true,
                                onPlay = ::playRadio,
                                onToggle = ::toggleRadio,
                                onStop = ::stopRadio,
                                onSection = ::switchSection,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    if (section == MediaSection.TV) {
                        TvContent(
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
                            onSection = ::switchSection,
                            modifier = Modifier.fillMaxSize().padding(14.dp)
                        )
                    } else {
                        RadioContent(
                            stations = stations,
                            activeStationId = radioStationId,
                            activeStationName = radioStationName,
                            isPlaying = radioPlaying,
                            error = radioError,
                            tv = false,
                            onPlay = ::playRadio,
                            onToggle = ::toggleRadio,
                            onStop = ::stopRadio,
                            onSection = ::switchSection,
                            modifier = Modifier.fillMaxSize().padding(14.dp)
                        )
                    }
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
    var controlsVisible by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(controlsVisible, channel.name) {
        if (controlsVisible) {
            delay(4000)
            controlsVisible = false
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        PlayerPanel(
            player = player,
            channel = channel,
            error = error,
            modifier = Modifier.fillMaxSize(),
            rounded = false,
            onTap = { controlsVisible = !controlsVisible },
            onDoubleTap = onExit
        )

        if (controlsVisible) {
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                color = Color(0xA8000000),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                    Text(channel.name, color = Color.White, fontSize = 20.sp)
                    Text(channel.category, color = Color(0xFFD3D7DF), fontSize = 11.sp)
                }
            }

            RoundControl(
                icon = Icons.Default.FullscreenExit,
                description = "Salir de pantalla completa",
                onClick = onExit,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            )

            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
                color = Color(0x9A000000),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundControl(Icons.Default.SkipPrevious, "Canal anterior", onPrevious)
                    RoundControl(
                        if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (playing) "Pausar" else "Reproducir",
                        { if (player.isPlaying) player.pause() else player.play() },
                        large = true
                    )
                    RoundControl(Icons.Default.SkipNext, "Canal siguiente", onNext)
                    RoundControl(
                        if (muted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        if (muted) "Activar sonido" else "Silenciar",
                        onMute
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val size = if (large) 62.dp else 50.dp
    Box(
        modifier
            .size(size)
            .background(if (focused) Color(0xFF426DB4) else Color(0xCC202A3B), CircleShape)
            .border(if (focused) 3.dp else 1.dp, if (focused) Color.White else Color(0xFF526078), CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = Color.White, modifier = Modifier.size(if (large) 31.dp else 25.dp))
    }
}

@Composable
private fun TvRail(section: MediaSection, onSection: (MediaSection) -> Unit, modifier: Modifier) {
    Column(
        modifier.fillMaxHeight().background(Panel, RoundedCornerShape(22.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.cosmora_icon),
            contentDescription = "Cosmora TV",
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(10.dp),
            contentScale = ContentScale.Fit
        )
        NavRow("Televisión", Icons.Default.LiveTv, section == MediaSection.TV) { onSection(MediaSection.TV) }
        NavRow("Radios", Icons.Default.Radio, section == MediaSection.RADIOS) { onSection(MediaSection.RADIOS) }
        Spacer(Modifier.weight(1f))
        Text("Ozzylator Labs", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun NavRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (active || focused) Panel2 else Color.Transparent, RoundedCornerShape(14.dp))
            .border(if (focused) 2.dp else 0.dp, if (focused) Focus else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (active || focused) Color.White else Muted)
        Spacer(Modifier.width(9.dp))
        Text(text, color = if (active || focused) Color.White else Muted)
    }
}

@Composable
private fun SectionTabs(section: MediaSection, onSection: (MediaSection) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionButton(
            text = "Televisión",
            icon = Icons.Default.LiveTv,
            active = section == MediaSection.TV,
            onClick = { onSection(MediaSection.TV) },
            modifier = Modifier.weight(1f)
        )
        SectionButton(
            text = "Radios",
            icon = Icons.Default.Radio,
            active = section == MediaSection.RADIOS,
            onClick = { onSection(MediaSection.RADIOS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Row(
        modifier
            .background(if (active || focused) Color(0xFF263957) else Panel, RoundedCornerShape(15.dp))
            .border(if (focused) 2.dp else 1.dp, if (active || focused) Focus else Color(0xFF344055), RoundedCornerShape(15.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(7.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
private fun TvContent(
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
    onSection: (MediaSection) -> Unit,
    modifier: Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Header("Televisión", "${channels.size} señales")
        if (!tv) SectionTabs(MediaSection.TV, onSection)

        PlayerPanel(
            player = player,
            channel = channels[selected],
            error = error,
            modifier = if (tv) Modifier.fillMaxWidth().weight(1f) else Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            onDoubleTap = onFullScreen
        )

        Controls(player, muted, onPrevious, onNext, onMute, onFullScreen)

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Canales", color = Color.White, fontSize = 22.sp, modifier = Modifier.weight(1f))
            Text("${channels.size} señales", color = Muted, fontSize = 13.sp)
        }

        if (tv) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(82.dp)) {
                itemsIndexed(channels) { index, channel ->
                    ChannelCard(channel, index == selected, { onSelect(index) }, Modifier.width(190.dp))
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                itemsIndexed(channels) { index, channel ->
                    ChannelCard(channel, index == selected, { onSelect(index) }, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun RadioContent(
    stations: List<RadioStation>,
    activeStationId: String?,
    activeStationName: String?,
    isPlaying: Boolean,
    error: String?,
    tv: Boolean,
    onPlay: (RadioStation) -> Unit,
    onToggle: () -> Unit,
    onStop: () -> Unit,
    onSection: (MediaSection) -> Unit,
    modifier: Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Header("Radios", "${stations.size} estaciones")
        if (!tv) SectionTabs(MediaSection.RADIOS, onSection)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Panel,
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(if (tv) 76.dp else 64.dp).background(Color(0xFF293A58), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Radio, null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(activeStationName ?: "Elige una radio", color = Color.White, fontSize = if (tv) 23.sp else 19.sp)
                    Text(
                        when {
                            error != null -> error
                            activeStationId == null -> "La radio puede seguir sonando con la pantalla bloqueada"
                            isPlaying -> "EN VIVO · reproduciendo en segundo plano"
                            else -> "Pausada"
                        },
                        color = if (error != null) Color(0xFFFF8B8B) else Muted,
                        fontSize = 12.sp
                    )
                }
                if (activeStationId != null) {
                    RoundControl(
                        icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        description = if (isPlaying) "Pausar radio" else "Reproducir radio",
                        onClick = onToggle
                    )
                    Spacer(Modifier.width(8.dp))
                    RoundControl(Icons.Default.Stop, "Detener radio", onStop)
                }
            }
        }

        Text("Estaciones", color = Color.White, fontSize = 22.sp)

        if (tv) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(96.dp)) {
                itemsIndexed(stations) { _, station ->
                    RadioCard(
                        station = station,
                        active = activeStationId == station.id,
                        playing = activeStationId == station.id && isPlaying,
                        onClick = { onPlay(station) },
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                itemsIndexed(stations) { _, station ->
                    RadioCard(
                        station = station,
                        active = activeStationId == station.id,
                        playing = activeStationId == station.id && isPlaying,
                        onClick = { onPlay(station) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.cosmora_icon),
            contentDescription = "Cosmora TV",
            modifier = Modifier.size(52.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text("Cosmora TV", color = Color.White, fontSize = 29.sp)
            Text("$title · $subtitle", color = Muted, fontSize = 13.sp)
        }
        Surface(color = Live, shape = RoundedCornerShape(18.dp)) {
            Text("EN VIVO", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp))
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
    onTap: (() -> Unit)? = null,
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
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            },
            update = { it.player = player }
        )

        if (onTap != null || onDoubleTap != null) {
            Box(
                Modifier.matchParentSize().pointerInput(onTap, onDoubleTap) {
                    detectTapGestures(
                        onTap = { onTap?.invoke() },
                        onDoubleTap = { onDoubleTap?.invoke() }
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
            Box(Modifier.fillMaxSize().background(Color(0xB810141D)), contentAlignment = Alignment.Center) {
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
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FocusButton(onClick = onPrevious) { Icon(Icons.Default.SkipPrevious, null); Spacer(Modifier.width(5.dp)); Text("Anterior") } }
        item {
            FocusButton(onClick = { if (player.isPlaying) player.pause() else player.play() }) {
                Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                Spacer(Modifier.width(5.dp))
                Text(if (playing) "Pausa" else "Play")
            }
        }
        item { FocusButton(onClick = onMute) { Icon(if (muted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null); Spacer(Modifier.width(5.dp)); Text(if (muted) "Sonido" else "Silenciar") } }
        item { FocusButton(onClick = onFullScreen) { Icon(Icons.Default.Fullscreen, null); Spacer(Modifier.width(5.dp)); Text("Pantalla") } }
        item { FocusButton(onClick = onNext) { Text("Siguiente"); Spacer(Modifier.width(5.dp)); Icon(Icons.Default.SkipNext, null) } }
    }
}

@Composable
private fun ChannelCard(channel: Channel, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
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
        Box(Modifier.size(42.dp).background(Color(0xFF34425C), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.LiveTv, null, tint = Color.White)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(channel.name, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(channel.category, color = Muted, fontSize = 10.sp, maxLines = 1)
        }
        if (selected) Icon(Icons.Default.PlayCircle, null, tint = Focus, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun RadioCard(
    station: RadioStation,
    active: Boolean,
    playing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val border = when {
        focused -> Focus
        active -> Color(0xFF5573A7)
        else -> Color.Transparent
    }

    Row(
        modifier
            .heightIn(min = 76.dp)
            .background(if (focused || active) Panel2 else Panel, RoundedCornerShape(17.dp))
            .border(if (focused) 3.dp else 1.dp, border, RoundedCornerShape(17.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).background(Color(0xFF34425C), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Radio, null, tint = Color.White)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(station.name, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(station.subtitle, color = Muted, fontSize = 10.sp, maxLines = 1)
        }
        if (active) {
            Icon(if (playing) Icons.Default.GraphicEq else Icons.Default.PauseCircle, null, tint = Focus, modifier = Modifier.size(24.dp))
        } else {
            Icon(Icons.Default.PlayCircle, null, tint = Muted, modifier = Modifier.size(22.dp))
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
            .border(if (focused) 3.dp else 1.dp, if (focused) Focus else Color(0xFF39445A), RoundedCornerShape(15.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .focusable(interactionSource = interaction)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
