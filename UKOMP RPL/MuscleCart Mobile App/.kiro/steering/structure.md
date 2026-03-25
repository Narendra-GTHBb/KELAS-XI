# Project Structure

## Root Level
```
MuscleCart/
├── app/                    # Main application module
├── gradle/                 # Gradle wrapper and version catalog
├── build.gradle.kts        # Root build configuration
├── settings.gradle.kts     # Project settings and modules
├── gradle.properties       # Global Gradle properties
└── gradlew[.bat]          # Gradle wrapper scripts
```

## App Module Structure
```
app/
├── build.gradle.kts        # App-specific build configuration
├── proguard-rules.pro      # ProGuard configuration
└── src/
    ├── main/
    │   ├── java/com/example/musclecart/    # Main source code
    │   ├── res/                            # Android resources
    │   │   ├── layout/                     # XML layouts
    │   │   ├── values/                     # Strings, colors, styles
    │   │   ├── drawable/                   # Images and drawables
    │   │   └── mipmap-*/                   # App icons
    │   └── AndroidManifest.xml             # App manifest
    ├── test/                               # Unit tests
    └── androidTest/                        # Instrumentation tests
```

## Key Conventions

### Package Structure
- **Base package**: `com.example.musclecart`
- Follow standard Android package organization
- Separate packages by feature or layer (activities, fragments, models, etc.)

### Resource Organization
- **Layouts**: Use descriptive names (activity_main.xml, fragment_workout.xml)
- **Values**: Organize strings, colors, and dimensions in respective files
- **Drawables**: Vector drawables preferred over bitmap images
- **Icons**: Use adaptive icons in mipmap directories

### Build Configuration
- **Version catalog**: All dependencies managed in `gradle/libs.versions.toml`
- **Kotlin DSL**: All build files use .kts extension
- **Module separation**: Single app module structure (can be extended)

### Testing Structure
- **Unit tests**: `src/test/` for business logic testing
- **UI tests**: `src/androidTest/` for integration and UI testing
- Follow AAA pattern (Arrange, Act, Assert) in tests