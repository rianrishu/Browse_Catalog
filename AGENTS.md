# Repository Guidelines

## Project Structure & Module Organization

Browse Catalog is a single-module Android app built with Kotlin, Jetpack Compose, and Material 3. The `app` module keeps production code under `app/src/main/java/com/rishu/browsecatalog/`. `MainActivity.kt` hosts the Compose UI, and `ui/theme/` defines colors, typography, and theming. The Android manifest is in `app/src/main/`, and string resources, icons, and other Android resources are in `app/src/main/res/`. Add or update dependency versions in `gradle/libs.versions.toml`.

## Build, Test, and Development Commands

Open the project in Android Studio and sync Gradle. Install Android SDK 37, use the Gradle daemon's configured JDK 25, and keep the machine-specific SDK path in the ignored `local.properties` file. Run these commands from the repository root with the checked-in Gradle wrapper.

- `./gradlew :app:assembleDebug` builds the debug APK.
- `./gradlew :app:installDebug` installs the debug APK on a connected device or emulator. You can also run the `app` configuration in Android Studio on an API 26+ device.
- `./gradlew :app:lintDebug` runs Android Lint for the debug variant.
- `./gradlew :app:testDebugUnitTest` runs local JVM tests.
- `./gradlew :app:connectedDebugAndroidTest` runs instrumented tests on a connected device or emulator.

## Coding Style & Naming Conventions

Use four-space indentation in Kotlin and Gradle Kotlin DSL files. Use PascalCase for classes and UI composables such as `Greeting`, lower camel case for ordinary functions and properties, and lowercase snake case for Android resource names. Follow the surrounding code and use Android Studio's Kotlin formatter.

## Testing Guidelines

Put JUnit 4 tests for JVM-only behavior in `app/src/test/java/`. Put device-dependent and Compose UI tests in `app/src/androidTest/java/`, which uses AndroidX Test. Mirror the production package hierarchy, give test classes a `Test` suffix, and name test methods for the behavior they verify. Cover the behavior changed by your patch.

## Commit & Pull Request Guidelines

Use short, imperative commit subjects. Describe the change and verification in the pull request, link any related issue, and include screenshots for visible UI changes. For commits created or amended by Codex, include the final trailer `Co-Authored-By: Codex <noreply@openai.com>` exactly once and verify it before pushing.
