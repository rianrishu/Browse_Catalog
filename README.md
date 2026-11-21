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

Fake Store supplies the catalogue. A small Node service delivers enquiries to a local inbox. Install Node 22.9+ and [Mailpit](https://mailpit.axllent.org/docs/install/). Start `mailpit` in one terminal and run these commands in another:

```sh
adb reverse tcp:3000 tcp:3000
node --env-file-if-exists=.env backend/server.mjs
```

Open a product, tap **Enquire**, enter your contact details and send. Check the product and reply address in the [sales inbox](http://localhost:8025). To change the recipient, copy `.env.example` to `.env`, edit `SALES_EMAIL`, and restart Node.

## Screen previews

With the debug variant selected in Android Studio, open `CataloguePreviews.kt` and select **Design** or **Split** to inspect the five screens with sample data. The running app loads product photos directly from Fake Store.

## Tests

Run `node --test backend/server.test.mjs` with Node 22.9+ for enquiry validation and delivery. Run `./gradlew :app:testDebugUnitTest` for direct API mapping, product search and category ordering.

## Decisions

- The Android repository marks five product IDs as featured. Edit `featuredIds` in `RemoteCatalogue.kt` to change the selection. A category highlights all its editorial picks and lists its remaining products separately. A category without a pick highlights the highest customer rating, including ties. Products within each section are ordered by rating, then ID.
- Search runs on submit across titles, descriptions and category names, ignoring case. Prices use USD. The small catalogue fits in memory. Search scans it once. Sorting a category of k products costs O(k log k).
- The ViewModel depends on the Catalogue interface. A factory supplies the Retrofit repository, and Compose collects state with the screen lifecycle. Product selection and draft form fields survive rotation. Manual injection keeps the object graph small and lets tests substitute a repository. [Android architecture](https://developer.android.com/topic/architecture/recommendations) · [Dependency injection](https://developer.android.com/training/dependency-injection/manual)
- Direct catalogue access follows the revised Android requirement and departs from the original backend-only product-data constraint. The enquiry service owns the recipient and mail adapter. A successful enquiry means Mailpit accepted the message. Local delivery gives reviewers a real inbox without credentials. The debug build allows HTTP to loopback.
- With another day, add durable enquiry IDs and deduplicated delivery before connecting a production mail provider. A lost acknowledgement can currently lead to a second email on retry. Production would also need HTTPS and request throttling.

See [AI usage and verification](AI_USAGE.md) for the implementation plan and test limits.
