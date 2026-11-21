# Browse Catalog

Browse and search the Fake Store catalogue from a Kotlin Android app. Retrofit and OkHttp call `https://fakestoreapi.com/products` directly. Product photos load from the HTTPS URLs in the response.

## Run the Android app

Open the project in Android Studio with SDK 37 installed, start an API 26+ emulator with internet access, and run the debug app. From the command line:

```sh
./gradlew :app:installDebug
adb shell am start -n com.rishu.browsecatalog/.MainActivity
```

Gradle uses the project's JDK 25 toolchain. Home, categories, search, and product details work with the Android app alone.

## Try a sales enquiry

Install [Mailpit](https://mailpit.axllent.org/docs/install/) and start it with `mailpit`. In another terminal, forward its HTTP port to the emulator:

```sh
adb reverse tcp:8025 tcp:8025
```

Open a product, tap **Enquire**, enter your contact details and send. The app posts to `http://127.0.0.1:8025/api/v1/send` and shows **Thank you** when Mailpit accepts the message. The demo recipient is `sales@example.test`. Open the message to check its product ID, product name and customer reply address. [Local inbox](http://localhost:8025)

## Screen previews

With the debug variant selected in Android Studio, open `CataloguePreviews.kt` and select **Design** or **Split** to inspect the five screens with sample data. The running app loads product photos directly from Fake Store.

## Tests

- `./gradlew :app:testDebugUnitTest` runs the JVM tests for search, category ordering, Fake Store response mapping and enquiry submission. HTTP tests use an OkHttp interceptor.
- `./gradlew :app:connectedDebugAndroidTest` runs the Compose journey tests on a connected emulator. They use a test repository. To check live delivery, follow **Try a sales enquiry** above.

## Decisions

- The Android repository marks five product IDs as featured. Edit `featuredIds` in `RemoteCatalogue.kt` to change the selection. A category highlights all its editorial picks and lists its remaining products separately. A category without a pick highlights the highest customer rating, including ties. Products within each section are ordered by rating, then ID.
- Search runs on submit across titles, descriptions and category names, ignoring case. Prices use USD. The small catalogue fits in memory. Search scans it once. Sorting a category of k products costs O(k log k).
- The ViewModel depends on the Catalogue interface. A factory supplies the Retrofit repository, and Compose collects state with the screen lifecycle. Product selection and draft form fields survive rotation. Manual injection keeps the object graph small and lets tests substitute a repository. [Android architecture](https://developer.android.com/topic/architecture/recommendations) · [Dependency injection](https://developer.android.com/training/dependency-injection/manual)
- The original brief required a custom backend for product data. The revised Android-only design calls Fake Store directly and uses Mailpit as a local sales inbox. The debug build permits HTTP to loopback for that inbox.
- With another day, add durable enquiry IDs and deduplicated delivery before connecting a production mail provider. A lost acknowledgement can currently lead to a second email on retry. Production would also need HTTPS and request throttling.

See [AI usage and verification](AI_USAGE.md) for the implementation plan and test limits.
