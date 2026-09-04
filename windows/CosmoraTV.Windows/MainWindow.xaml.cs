using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using LibVLCSharp.Shared;

namespace CosmoraTV.Windows;

public partial class MainWindow : Window
{
    private LibVLC? _libVlc;
    private MediaPlayer? _player;
    private Media? _currentMedia;
    private IReadOnlyList<string> _currentSources = Array.Empty<string>();
    private int _sourceIndex;
    private int _selectedIndex;
    private bool _radioMode;
    private bool _fullscreen;
    private bool _closing;
    private WindowState _normalState = WindowState.Normal;
    private WindowStyle _normalStyle = WindowStyle.SingleBorderWindow;
    private ResizeMode _normalResizeMode = ResizeMode.CanResize;

    public MainWindow()
    {
        InitializeComponent();
        Loaded += MainWindow_Loaded;
    }

    private void MainWindow_Loaded(object sender, RoutedEventArgs e)
    {
        try
        {
            Core.Initialize();
            _libVlc = new LibVLC(
                "--network-caching=1400",
                "--live-caching=1200",
                "--clock-jitter=0",
                "--clock-synchro=0"
            );

            _player = new MediaPlayer(_libVlc)
            {
                Volume = (int)VolumeSlider.Value
            };

            VideoView.MediaPlayer = _player;
            _player.Playing += Player_Playing;
            _player.Paused += Player_Paused;
            _player.Stopped += Player_Stopped;
            _player.EncounteredError += Player_EncounteredError;

            RefreshSection();
            PlayCurrent();
        }
        catch (Exception ex)
        {
            ShowStatus("No se pudo iniciar el reproductor de Cosmora");
            MessageBox.Show(
                $"Cosmora TV no pudo iniciar el motor multimedia.\n\n{ex.Message}",
                "Cosmora TV",
                MessageBoxButton.OK,
                MessageBoxImage.Error
            );
        }
    }

    private void Player_Playing(object? sender, EventArgs e)
    {
        Dispatcher.Invoke(() =>
        {
            HideStatus();
            PlayPauseButton.Content = "❚❚  Pausa";
        });
    }

    private void Player_Paused(object? sender, EventArgs e)
    {
        Dispatcher.Invoke(() => PlayPauseButton.Content = "▶  Play");
    }

    private void Player_Stopped(object? sender, EventArgs e)
    {
        if (_closing) return;
        Dispatcher.Invoke(() => PlayPauseButton.Content = "▶  Play");
    }

    private void Player_EncounteredError(object? sender, EventArgs e)
    {
        if (_closing) return;

        Dispatcher.BeginInvoke(() =>
        {
            if (_sourceIndex + 1 < _currentSources.Count)
            {
                _sourceIndex++;
                ShowStatus("Probando fuente alternativa…");
                StartSource(_currentSources[_sourceIndex]);
            }
            else
            {
                ShowStatus(_radioMode
                    ? "Esta radio no está disponible en este momento"
                    : "Esta señal no está disponible en este momento");
            }
        });
    }

    private void TvButton_Click(object sender, RoutedEventArgs e)
    {
        if (!_radioMode) return;
        _radioMode = false;
        _selectedIndex = 0;
        RefreshSection();
        PlayCurrent();
    }

    private void RadioButton_Click(object sender, RoutedEventArgs e)
    {
        if (_radioMode) return;
        _radioMode = true;
        _selectedIndex = 0;
        RefreshSection();
        PlayCurrent();
    }

    private void RefreshSection()
    {
        TvButton.Background = BrushFromHex(_radioMode ? "#00000000" : "#1C2B42");
        RadioButton.Background = BrushFromHex(_radioMode ? "#1C2B42" : "#00000000");

        if (_radioMode)
        {
            SectionTitle.Text = "Radios";
            SectionSubtitle.Text = $"{Catalog.Radios.Count} estaciones chilenas";
            ListTitle.Text = "Estaciones";
            LiveBadge.Text = "RADIO";
            RadioPanel.Visibility = Visibility.Visible;
            VideoView.Visibility = Visibility.Hidden;
        }
        else
        {
            SectionTitle.Text = "Televisión";
            SectionSubtitle.Text = $"{Catalog.Channels.Count} señales chilenas";
            ListTitle.Text = "Canales";
            LiveBadge.Text = "EN VIVO";
            RadioPanel.Visibility = Visibility.Collapsed;
            VideoView.Visibility = Visibility.Visible;
        }

        RebuildCards();
    }

    private void RebuildCards()
    {
        ItemsPanel.Children.Clear();

        if (_radioMode)
        {
            for (var i = 0; i < Catalog.Radios.Count; i++)
            {
                var station = Catalog.Radios[i];
                ItemsPanel.Children.Add(CreateCard(i, station.Name, station.Subtitle, "●"));
            }
        }
        else
        {
            for (var i = 0; i < Catalog.Channels.Count; i++)
            {
                var channel = Catalog.Channels[i];
                ItemsPanel.Children.Add(CreateCard(i, channel.Name, channel.Category, "▣"));
            }
        }
    }

    private Button CreateCard(int index, string title, string subtitle, string glyph)
    {
        var button = new Button
        {
            Style = (Style)FindResource("CardButton"),
            Tag = index,
            Background = BrushFromHex(index == _selectedIndex ? "#1D2D47" : "#111823"),
            BorderBrush = BrushFromHex(index == _selectedIndex ? "#4E8CFF" : "#263247")
        };

        var row = new Grid();
        row.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(48) });
        row.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

        var icon = new Border
        {
            Width = 40,
            Height = 40,
            CornerRadius = new CornerRadius(11),
            Background = BrushFromHex("#23334D"),
            Child = new TextBlock
            {
                Text = glyph,
                Foreground = Brushes.White,
                FontSize = 18,
                HorizontalAlignment = HorizontalAlignment.Center,
                VerticalAlignment = VerticalAlignment.Center
            }
        };

        Grid.SetColumn(icon, 0);
        row.Children.Add(icon);

        var text = new StackPanel { VerticalAlignment = VerticalAlignment.Center };
        text.Children.Add(new TextBlock
        {
            Text = title,
            Foreground = Brushes.White,
            FontSize = 15,
            FontWeight = FontWeights.SemiBold,
            TextTrimming = TextTrimming.CharacterEllipsis
        });
        text.Children.Add(new TextBlock
        {
            Text = subtitle,
            Foreground = BrushFromHex("#8D9AAF"),
            FontSize = 10,
            Margin = new Thickness(0, 4, 0, 0),
            TextTrimming = TextTrimming.CharacterEllipsis
        });

        Grid.SetColumn(text, 1);
        row.Children.Add(text);
        button.Content = row;

        button.Click += (_, _) =>
        {
            _selectedIndex = (int)button.Tag;
            RebuildCards();
            PlayCurrent();
        };

        return button;
    }

    private void PlayCurrent()
    {
        if (_player is null || _libVlc is null) return;

        _player.Stop();
        _currentMedia?.Dispose();
        _currentMedia = null;
        _sourceIndex = 0;

        if (_radioMode)
        {
            var station = Catalog.Radios[_selectedIndex];
            _currentSources = new[] { station.StreamUrl };
            NowTitle.Text = station.Name;
            NowSubtitle.Text = station.Subtitle;
            RadioName.Text = station.Name;
            RadioSubtitle.Text = station.Subtitle;
            SetRadioArtwork(station.ArtworkUrl);
        }
        else
        {
            var channel = Catalog.Channels[_selectedIndex];
            _currentSources = channel.Sources;
            NowTitle.Text = channel.Name;
            NowSubtitle.Text = channel.Category;
        }

        ShowStatus("Conectando…");
        StartSource(_currentSources[0]);
    }

    private void StartSource(string source)
    {
        if (_player is null || _libVlc is null) return;

        try
        {
            _currentMedia?.Dispose();
            _currentMedia = new Media(_libVlc, new Uri(source));
            _currentMedia.AddOption(":network-caching=1400");
            _currentMedia.AddOption(":live-caching=1200");
            _player.Play(_currentMedia);
        }
        catch
        {
            Player_EncounteredError(this, EventArgs.Empty);
        }
    }

    private void SetRadioArtwork(string? artworkUrl)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(artworkUrl))
            {
                RadioArtwork.Source = new BitmapImage(new Uri("pack://application:,,,/Assets/cosmora.png"));
                return;
            }

            var image = new BitmapImage();
            image.BeginInit();
            image.CacheOption = BitmapCacheOption.OnLoad;
            image.UriSource = new Uri(artworkUrl, UriKind.Absolute);
            image.EndInit();
            image.Freeze();
            RadioArtwork.Source = image;
        }
        catch
        {
            RadioArtwork.Source = new BitmapImage(new Uri("pack://application:,,,/Assets/cosmora.png"));
        }
    }

    private void Previous_Click(object sender, RoutedEventArgs e) => ChangeItem(-1);
    private void Next_Click(object sender, RoutedEventArgs e) => ChangeItem(1);

    private void ChangeItem(int delta)
    {
        var count = _radioMode ? Catalog.Radios.Count : Catalog.Channels.Count;
        if (count == 0) return;
        _selectedIndex = (_selectedIndex + delta + count) % count;
        RebuildCards();
        PlayCurrent();
    }

    private void PlayPause_Click(object sender, RoutedEventArgs e)
    {
        if (_player is null) return;
        if (_player.IsPlaying) _player.Pause(); else _player.Play();
    }

    private void Mute_Click(object sender, RoutedEventArgs e)
    {
        if (_player is null) return;
        _player.Mute = !_player.Mute;
        MuteButton.Content = _player.Mute ? "🔇  Activar sonido" : "🔊  Sonido";
    }

    private void VolumeSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
    {
        if (_player is not null)
            _player.Volume = (int)e.NewValue;
    }

    private void Fullscreen_Click(object sender, RoutedEventArgs e) => ToggleFullscreen();
    private void VideoView_MouseDoubleClick(object sender, MouseButtonEventArgs e) => ToggleFullscreen();

    private void ToggleFullscreen()
    {
        if (!_fullscreen)
        {
            _normalState = WindowState;
            _normalStyle = WindowStyle;
            _normalResizeMode = ResizeMode;

            WindowState = WindowState.Normal;
            WindowStyle = WindowStyle.None;
            ResizeMode = ResizeMode.NoResize;
            SidebarColumn.Width = new GridLength(0);
            HeaderRow.Height = new GridLength(0);
            ListRow.Height = new GridLength(0);
            ContentGrid.Margin = new Thickness(0);
            PlayerShell.CornerRadius = new CornerRadius(0);
            FullscreenButton.Content = "⛶  Salir";
            WindowState = WindowState.Maximized;
            _fullscreen = true;
        }
        else
        {
            WindowState = WindowState.Normal;
            WindowStyle = _normalStyle;
            ResizeMode = _normalResizeMode;
            SidebarColumn.Width = new GridLength(230);
            HeaderRow.Height = new GridLength(78);
            ListRow.Height = new GridLength(172);
            ContentGrid.Margin = new Thickness(10, 14, 18, 14);
            PlayerShell.CornerRadius = new CornerRadius(22);
            FullscreenButton.Content = "⛶  Pantalla completa";
            WindowState = _normalState;
            _fullscreen = false;
        }
    }

    private void Window_KeyDown(object sender, KeyEventArgs e)
    {
        switch (e.Key)
        {
            case Key.Left:
                ChangeItem(-1);
                e.Handled = true;
                break;
            case Key.Right:
                ChangeItem(1);
                e.Handled = true;
                break;
            case Key.Space:
                PlayPause_Click(this, new RoutedEventArgs());
                e.Handled = true;
                break;
            case Key.F11:
                ToggleFullscreen();
                e.Handled = true;
                break;
            case Key.Escape when _fullscreen:
                ToggleFullscreen();
                e.Handled = true;
                break;
            case Key.Up:
                VolumeSlider.Value = Math.Min(100, VolumeSlider.Value + 5);
                e.Handled = true;
                break;
            case Key.Down:
                VolumeSlider.Value = Math.Max(0, VolumeSlider.Value - 5);
                e.Handled = true;
                break;
        }
    }

    private void ShowStatus(string text)
    {
        StatusText.Text = text;
        StatusPanel.Visibility = Visibility.Visible;
    }

    private void HideStatus() => StatusPanel.Visibility = Visibility.Collapsed;

    private static SolidColorBrush BrushFromHex(string hex)
    {
        return (SolidColorBrush)new BrushConverter().ConvertFromString(hex)!;
    }

    private void Window_Closing(object? sender, CancelEventArgs e)
    {
        _closing = true;
        try
        {
            if (_player is not null)
            {
                _player.Stop();
                _player.Dispose();
            }
            _currentMedia?.Dispose();
            _libVlc?.Dispose();
        }
        catch
        {
            // Cierre silencioso.
        }
    }
}
