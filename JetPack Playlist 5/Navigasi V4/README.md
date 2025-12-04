# Navigation in Jetpack Compose - Complete Implementation

This project demonstrates a complete implementation of navigation in Jetpack Compose following the best practices outlined in your requirements.

## 🚀 Project Overview

This Android app showcases all the essential navigation concepts in Jetpack Compose:

- Basic navigation between screens
- Route management with a centralized Routes object
- Passing arguments between screens
- Navigation back stack management

## 📋 Implementation Components

### 1. Dependencies and Setup

The navigation functionality is enabled by adding the `navigation-compose` library to the project:

**In `gradle/libs.versions.toml`:**

```toml
navigationCompose = "2.8.3"
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
```

**In `app/build.gradle.kts`:**

```kotlin
implementation(libs.androidx.navigation.compose)
```

### 2. Routes Definition (`Routes.kt`)

All navigation routes are centralized in a single object to avoid hardcoding and typos:

```kotlin
object Routes {
    const val SCREEN_A = "screen_a"
    const val SCREEN_B = "screen_b"
    const val SCREEN_C = "screen_c"
    const val SCREEN_WITH_ARGS = "screen_with_args/{name}/{age}"

    fun createScreenWithArgsRoute(name: String, age: Int): String {
        return "screen_with_args/$name/$age"
    }
}
```

### 3. Main Navigation Setup (`MainActivity.kt`)

The navigation is set up at the top level of the app hierarchy:

- **`rememberNavController()`**: Creates and remembers the NavController
- **`NavHost`**: Acts as the container for all navigable screens
- **`startDestination`**: Sets the initial screen (Screen A)
- **`composable`**: Defines each screen and its route

### 4. Screen Navigation

#### Basic Navigation

- **Forward Navigation**: Uses `navController.navigate("route")` to move to a new screen
- **Back Navigation**: Uses `navController.popBackStack()` to return to the previous screen
- **Automatic Back Stack**: The system automatically manages the back button behavior

#### Navigation with Arguments

- **Sending Arguments**: Arguments are passed through the route URL
- **Receiving Arguments**: Arguments are extracted from `navBackStackEntry.arguments`
- **Type Safety**: Arguments are defined with specific types (String, Int, etc.)

## 🔄 Navigation Flow

```
Screen A (Start)
├── → Screen B
│   ├── → Screen C
│   └── ← Back to A
├── → Screen C
│   └── ← Back to A
└── → Screen with Args (name, age)
    └── ← Back to A
```

## 🎯 Key Features Demonstrated

### 1. **rememberNavController**

- Created at the top level in `MainActivity`
- Manages all navigation state and transitions
- Persists across configuration changes

### 2. **NavHost Configuration**

- Acts as the navigation container
- Defines all possible routes and their corresponding screens
- Sets the starting destination

### 3. **Route Management**

- Centralized route definitions prevent hardcoding
- Helper functions for parameterized routes
- Type-safe argument handling

### 4. **Screen Navigation**

- Simple navigation between screens using `navigate()`
- Automatic back stack management
- Support for programmatic back navigation

### 5. **Argument Passing**

- Pass data between screens through route parameters
- Type-safe argument extraction
- Support for multiple argument types (String, Int, etc.)

## 📱 App Screens

### Screen A (Home)

- Starting screen with navigation options
- Input fields for name and age (for argument demonstration)
- Buttons to navigate to other screens

### Screen B

- Intermediate screen accessible from Screen A
- Can navigate to Screen C or back to Screen A
- Demonstrates multi-level navigation

### Screen C

- Destination screen accessible from both A and B
- Shows flexible navigation paths
- Simple back navigation

### Screen with Arguments

- Displays received arguments (name and age)
- Demonstrates data passing between screens
- Shows how to extract and use navigation arguments

## 🛠️ Technical Implementation Details

### Navigation Architecture

```kotlin
NavHost(
    navController = navController,
    startDestination = Routes.SCREEN_A
) {
    composable(Routes.SCREEN_A) { ScreenA(...) }
    composable(Routes.SCREEN_B) { ScreenB(...) }
    composable(Routes.SCREEN_C) { ScreenC(...) }
    composable(
        route = Routes.SCREEN_WITH_ARGS,
        arguments = listOf(
            navArgument("name") { type = NavType.StringType },
            navArgument("age") { type = NavType.IntType }
        )
    ) { navBackStackEntry ->
        val name = navBackStackEntry.arguments?.getString("name") ?: ""
        val age = navBackStackEntry.arguments?.getInt("age") ?: 0
        ScreenWithArgs(name, age, ...)
    }
}
```

### Argument Handling

- Arguments are defined in the route pattern: `"screen/{param1}/{param2}"`
- Arguments must be declared with their types in the `composable` function
- Values are extracted from `navBackStackEntry.arguments`

## 🚀 Running the Project

1. Open the project in Android Studio
2. Sync the project to download dependencies
3. Run the app on an emulator or physical device
4. Start navigating between screens to see the implementation in action

## 📚 Learning Outcomes

After exploring this project, you will understand:

- How to set up navigation in Jetpack Compose
- Best practices for route management
- How to pass data between screens
- Navigation back stack management
- Creating a scalable navigation architecture

This implementation follows the exact structure and concepts outlined in your navigation tutorial, providing a complete working example of Jetpack Compose navigation.
