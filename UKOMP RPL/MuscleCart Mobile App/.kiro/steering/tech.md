# Technology Stack

## Build System
- **Gradle** with Kotlin DSL (build.gradle.kts)
- **Android Gradle Plugin** 8.13.2
- Version catalog management via `gradle/libs.versions.toml`

## Core Technologies
- **Kotlin** 2.0.21 (primary language)
- **Android SDK** targeting API 36, minimum API 24
- **Java 11** compatibility (source/target)
- **AndroidX** libraries for modern Android development

## Key Dependencies
- **Material Design Components** for UI
- **AndroidX Core KTX** for Kotlin extensions
- **ConstraintLayout** for flexible layouts
- **AppCompat** for backward compatibility

## Testing Framework
- **JUnit 4** for unit tests
- **AndroidX Test** with JUnit and Espresso for instrumentation tests

## Common Commands

### Build & Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build to connected device
./gradlew installDebug
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

### Development
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Generate build reports
./gradlew build --scan
```

## Build Configuration
- **Namespace**: `com.example.musclecart`
- **Application ID**: `com.example.musclecart`
- **Kotlin Code Style**: Official
- **ProGuard**: Disabled in debug, available for release builds