# Delta-V 🚀

A modern Android application for tracking upcoming rocket launches across the globe, built with Jetpack Compose and Android best practices.

## Features

- **Upcoming Launches**: View scheduled rocket launches from various providers like SpaceX, Rocket Lab, and more
- **Offline-First**: Caches launch data locally for offline viewing using Room database
- **Real-Time Updates**: Pull-to-refresh support for the latest launch information
- **Beautiful UI**: Modern Material 3 design with dynamic color support
- **Launch Details**: View mission summaries, launch windows, countdowns, and launch locations
- **Status Tracking**: Monitor launch status (Go, Hold, Success, TBD)
- **Mission Info**: Access detailed mission information including provider, rocket type, and location

## Tech Stack

### Core Technologies
- **Kotlin**: 2.2.21
- **Jetpack Compose**: Modern UI toolkit for Android
- **Material 3**: Material Design 3 components

### Architecture
- **MVVM Architecture**: Model-View-ViewModel pattern
- **Repository Pattern**: Single source of truth for data
- **Offline-First**: Room database with Retrofit as the source of truth
- **Clean Architecture**: Separated layers (data, domain, ui)

### Dependency Injection
- **Hilt**: Google's recommended dependency injection library

### Data Layer
- **Room Database**: Local persistence with Flow support
- **Retrofit**: Type-safe HTTP client for API calls
- **Gson**: JSON serialization/deserialization
- **OkHttp**: HTTP client with logging interceptor

### UI & Threading
- **Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams
- **Coil**: Image loading library for Compose

### Testing
- **JUnit**: Unit testing
- **Espresso**: Android UI testing
- **Compose Testing**: UI component testing

## Architecture

This project follows the **Now in Android** architecture pattern:

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  (Compose Screens + ViewModels + UiState)               │
└────────────────────────┬────────────────────────────────┘
                         │ observes
                         ▼
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                          │
│  (Repository + Database + Network)                      │
│  ┌──────────────┐         ┌──────────────┐             │
│  │   Room DB    │ ◄─────► │  Retrofit API│             │
│  │   (Cache)    │         │  (Source)    │             │
│  └──────────────┘         └──────────────┘             │
└─────────────────────────────────────────────────────────┘
```

### Key Architecture Principles
1. **UI observes Database**: UI components observe `Flow` from Room database, not the API directly
2. **Repository orchestrates**: Repository handles the sync between network and database
3. **Offline-First**: App works without network; shows cached data with an offline banner
4. **Transaction Safety**: Database updates happen inside transactions to prevent partial updates

## API

This app uses the **Launch Library 2** API to fetch launch data:
- **Base URL**: `https://ll.thespacedevs.com/2.2.0/`
- **Endpoint**: `/launch/upcoming/?limit={limit}&mode=detailed`

For more API documentation, visit: [Launch Library API](https://thespacedevs.com/llapi)

## Project Structure

```
app/src/main/java/dev/lvhung14/delta_v/
├── data/
│   ├── LaunchesRepository.kt          # Repository implementation
│   └── di/
│       └── RepositoryModule.kt        # Dependency injection
├── database/
│   ├── DeltaVDatabase.kt             # Room database
│   ├── dao/
│   │   └── LaunchDao.kt              # Database access object
│   ├── model/
│   │   └── LaunchEntity.kt           # Room entity & mappers
│   └── di/
│       ├── DatabaseModule.kt
│       └── DaoModule.kt
├── network/
│   ├── DeltaVNetworkDataSource.kt    # Network data source interface
│   ├── model/
│   │   └── NetworkLaunchModels.kt    # API DTOs
│   ├── retrofit/
│   │   └── DeltaVRetrofit.kt         # Retrofit implementation
│   └── di/
│       └── NetworkModule.kt          # Network DI module
├── feature/
│   └── launches/
│       ├── LaunchesViewModel.kt      # ViewModel
│       ├── LaunchesUiState.kt        # UI state models
│       ├── ui/
│       │   ├── LaunchesScreen.kt     # Main screen
│       │   ├── LaunchCard.kt         # Card composable
│       │   └── LaunchesRoute.kt      # Navigation route
├── model/
│   └── Launch.kt                     # Domain model
├── ui/
│   └── theme/
│       ├── Color.kt                  # Color definitions
│       ├── Theme.kt                  # Theme composition
│       └── Type.kt                   # Typography
├── di/
│   └── DispatchersModule.kt          # Coroutine dispatchers
├── DeltaVApplication.kt              # Application class
└── MainActivity.kt                   # Main activity
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK 36
- Minimum SDK: 26 (Android 8.0)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/lvhung14/Delta-V.git
   cd Delta-V
   ```

2. **Open the project in Android Studio**

3. **Sync Gradle**
   - Android Studio will prompt you to sync Gradle dependencies
   - Click "Sync Now" or use `File > Sync Project with Gradle Files`

4. **Run the app**
   - Connect an Android device (API 26+) or create an emulator
   - Click the Run button or press `Shift + F10`

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest
```

## Configuration

### Build Variants

The project uses standard Android build types:
- **Debug**: For development with logging enabled
- **Release**: For production builds

### Permissions

The app requires the following permission:
- `INTERNET`: To fetch launch data from the API

Listed in `AndroidManifest.xml`.

## Key Features Details

### Offline-First Strategy

The app implements an offline-first architecture:
1. On app launch, checks the Room database for cached launches
2. Attempts to fetch fresh data from the API
3. If the API call succeeds:
   - Replaces cached data inside a database transaction
   - Displays fresh data
4. If the API call fails:
   - Shows cached data with an offline banner
   - User can retry when back online

### Data Flow

```
User Trigger (Refresh)
        ↓
refreshUpcomingLaunches()
        ↓
Repository.getUpcomingLaunches() [Retrofit]
        ↓
Database.withTransaction { deleteAll(), upsert() }
        ↓
observeLaunches() [Flow]
        ↓
ViewModel combines with offline status
        ↓
UI updates automatically
```

### Launch Data Model

Core launch information includes:
- Launch ID and display name
- Mission name and description
- Launch provider (SpaceX, Rocket Lab, etc.)
- Rocket configuration
- Launch pad and location
- NET (No Earlier Than) timestamp
- Launch window
- Launch status
- Mission images
- Detail URLs

## Future Improvements

Potential enhancements under consideration:
- [ ] Pagination support for large launch lists
- [ ] Search and filter functionality
- [ ] Launch reminders and notifications
- [ ] Map visualization of launch locations
- [ ] Detailed launch information screen
- [ ] WorkManager for periodic background sync
- [ ] Network connectivity monitoring
- [ ] Dark/Light theme toggle
- [ ] Accessibility improvements
- [ ] Unit and integration tests
- [ ] CI/CD pipeline setup

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- **Launch Library 2**: For providing comprehensive launch data API
- **Now in Android**: Architecture patterns and best practices
- **Jetpack Compose**: Modern UI toolkit
- **Android Team**: For excellent documentation and samples

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

---

Built with ❤️ for space enthusiasts