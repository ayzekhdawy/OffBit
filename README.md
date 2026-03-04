# OffBit

An open-source P2P voice calling application for Android with decentralized architecture.

## Features

- **P2P Voice Calling**: Secure peer-to-peer voice communication without central servers
- **Decentralized Identity**: Cryptographic identity management using Android Keystore
- **Local Network Discovery**: Automatic discovery of peers on the same network
- **Call History**: Persistent storage of call records with duration and status
- **Contacts Management**: Add, remove, and favorite contacts
- **Web Browser**: Built-in browser with HTTP proxy support
- **iOS-inspired UI**: Modern, clean interface with light/dark theme support
- **Minimal Authentication**: Simple username-based identification

## Architecture

### Core Components

1. **User Profile Management**
   - Cryptographic key generation and storage
   - Display name management
   - Profile persistence

2. **Peer Discovery**
   - UDP-based local network discovery
   - Automatic peer presence detection
   - Connection state management

3. **Voice Calling**
   - WebRTC implementation for P2P audio
   - Secure signaling over local network
   - Call history recording

4. **Data Storage**
   - SQLite database for call records and contacts
   - Android Keystore for cryptographic keys
   - Local persistence of user preferences

5. **Web Browser**
   - WebView-based browsing
   - HTTP proxy server for enhanced privacy
   - Standard browser navigation controls

## Security

- **End-to-End Encryption**: All voice communications are encrypted
- **Secure Key Storage**: Android Keystore protects private keys
- **No Central Servers**: P2P architecture eliminates server vulnerabilities
- **Minimal Data Collection**: No personal data stored externally

## Requirements

- Android 11 (API level 30) or higher
- WiFi connectivity for local network discovery
- Microphone permission for voice calls

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and deploy to device

## Usage

1. **Setup**: Launch the app and create your profile
2. **Calling**: Enter a peer's username or select from contacts
3. **Contacts**: Manage your contact list and favorites
4. **Browsing**: Use the built-in browser for web access

## Development

### Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/offbit/offbit/
│   │   │   ├── model/          # Data models
│   │   │   ├── network/        # Networking components
│   │   │   ├── webrtc/         # WebRTC implementation
│   │   │   ├── database/       # Database helpers
│   │   │   ├── manager/        # Business logic managers
│   │   │   ├── service/        # Background services
│   │   │   ├── receiver/       # Broadcast receivers
│   │   │   ├── utils/          # Utility classes
│   │   │   └── ui/             # Activities and fragments
│   │   └── res/                # Resources
│   ├── test/                   # Unit tests
│   └── androidTest/            # Instrumentation tests
└── build.gradle                # Build configuration
```

### Dependencies

- WebRTC for voice calling
- NanoHTTPD for proxy server
- Material Design components
- SQLite for local storage

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- WebRTC for P2P communication framework
- NanoHTTPD for lightweight HTTP server
- Google's Material Design for UI components
