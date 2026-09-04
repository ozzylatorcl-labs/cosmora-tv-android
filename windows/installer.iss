#define MyAppName "Cosmora TV"
#define MyAppVersion "1.1.0"
#define MyAppPublisher "Ozzylator Labs"
#define MyAppExeName "CosmoraTV.exe"

[Setup]
AppId={{A7DA2B3E-8FC7-4B44-A0BA-70A5613CC823}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\Ozzylator Labs\Cosmora TV
DefaultGroupName=Cosmora TV
DisableProgramGroupPage=yes
OutputDir=dist
OutputBaseFilename=Cosmora-TV-Windows-Setup-V1.1
SetupIconFile=CosmoraTV.Windows\cosmora.ico
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=admin
UninstallDisplayIcon={app}\{#MyAppExeName}

[Tasks]
Name: "desktopicon"; Description: "Crear acceso directo en el escritorio"; GroupDescription: "Accesos directos:"; Flags: unchecked

[Files]
Source: "publish\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\Cosmora TV"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\Cosmora TV"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Abrir Cosmora TV"; Flags: nowait postinstall skipifsilent
