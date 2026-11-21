# Browse Catalog

A Kotlin Android catalogue for browsing products and contacting sales. The Node.js backend reads Fake Store and sends enquiries to a local Mailpit inbox. Compose suits the existing Android project. Node's HTTP server and built-in fetch keep the backend runnable without an npm install.

## Run locally

Install Android Studio with SDK 37, start an API 26+ emulator, and install Android platform-tools, Node 22.9+ and [Mailpit](https://mailpit.axllent.org/docs/install/). Start the inbox with `mailpit` in one terminal, then run the backend from the repository root in another:

```sh
node --env-file-if-exists=.env backend/server.mjs
```

With the emulator running:

```sh
adb reverse tcp:3000 tcp:3000
./gradlew :app:installDebug
adb shell am start -n com.rishu.browsecatalog/.MainActivity
```

Gradle uses the project's JDK 25 toolchain. Allow extra time for the first SDK, toolchain and image downloads. The backend needs internet access to Fake Store. To diagnose a loading error, open `http://localhost:3000/products` on the host and check `adb reverse --list`.

Choose a featured product, open **Enquire**, enter a name and email, and send it. Open the [local sales inbox](http://localhost:8025) and check the product and reply address. To change the recipient, copy `.env.example` to `.env`, edit `SALES_EMAIL`, and restart Node. Stop the backend and inbox with Ctrl+C in their terminals.

## Screen previews

With the debug variant selected in Android Studio, open `CataloguePreviews.kt` and select **Design** or **Split** to inspect the five screens with sample data. The running app loads product photos through the backend.

## Tests

Run `node --test backend/server.test.mjs` with Node 22.9+ for catalogue mapping, validation and delivery. Run `./gradlew :app:testDebugUnitTest` for product search and category ordering.

## Decisions

- Set `FEATURED_PRODUCT_IDS` in `.env` to choose five distinct IDs from the backend catalogue, then restart Node. Home shows those five products. A category highlights all its editorial picks and lists its remaining products separately. A category without a pick highlights the highest customer rating, including ties. Products within each section are ordered by rating, then ID.
- Search runs on submit across titles, descriptions and category names, ignoring case. Prices use USD. The small catalogue fits in memory. Search scans it once. Sorting a category of k products costs O(k log k).
- The ViewModel depends on the Catalogue interface. A factory supplies the Retrofit repository, and Compose collects state with the screen lifecycle. Product selection and draft form fields survive rotation. Manual injection keeps the object graph small and lets tests substitute a repository. [Android architecture](https://developer.android.com/topic/architecture/recommendations) · [Dependency injection](https://developer.android.com/training/dependency-injection/manual)
- The backend owns the product source, recipient and mail adapter. A successful enquiry means Mailpit accepted the message. Local delivery gives reviewers a real inbox without credentials. The debug build allows HTTP to loopback.
- With another day, add durable enquiry IDs and deduplicated delivery before connecting a production mail provider. A lost acknowledgement can currently lead to a second email on retry. Production would also need HTTPS and request throttling.

See [AI usage and verification](AI_USAGE.md) for the implementation plan and test limits.
