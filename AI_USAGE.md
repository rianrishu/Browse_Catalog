# AI usage

Codex (GPT-6.2 Astra) implemented this change with the repository's [contributor instructions](AGENTS.md). The plan had three parts: establish the backend contract, build the Android browsing and enquiry flow, then test the boundaries and document the local setup.

## Decisions shaped by the brief

1. The request for Retrofit and OkHttp initially left the destination open. Reading the acceptance criteria settled the boundary: the phone talks to our service, which owns the third-party catalogue and sales inbox.
2. The nearby Hilt MVVM movie project separates network setup, repositories and presentation. This project follows that dependency direction. One repository and one ViewModel need only a constructor and a lifecycle factory.
3. The original emulator setup used the host's virtual LAN address. Reviewing Android 17 network permissions exposed an unnecessary permission dependency. The final setup forwards a loopback port with adb.
4. The first backend test opened a listening socket. The restricted runner rejected it before reaching any assertions. The revised tests pass request streams directly to the HTTP handler. They exercise routing and validation while leaving real network delivery for the emulator smoke test.

## Verification and time

Four Node tests and two JVM domain tests passed. The production Kotlin sources also compiled with the local Compose compiler and cached Android libraries. Gradle could not acquire a socket in the runner. APK packaging, the instrumented journey and live Mailpit delivery still need an emulator run. Run the journey with `./gradlew :app:connectedDebugAndroidTest` after starting an emulator.

The implementation session took roughly five minutes of elapsed tool and generation time: under a minute to read and plan, about two minutes building, and about two minutes testing and reviewing. This excludes the earlier repository setup and any later human review.

The screen review added five separate Compose screens and Android Studio previews. Starting Node and Mailpit directly removes the container prerequisite. The ADB daemon could not open its local socket during verification.
