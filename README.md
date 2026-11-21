# Browse Catalog

A Kotlin Android catalogue for browsing products and contacting sales. The Node.js backend reads Fake Store and sends enquiries to a local Mailpit inbox. Compose suits the existing Android project. Node's HTTP server and built-in fetch keep the backend runnable without an npm install.

## Run locally

Install Android Studio with SDK 37, start an API 26+ emulator, and have Docker Compose and Android platform-tools available. From the repository root:

```sh
docker compose up -d
adb reverse tcp:3000 tcp:3000
./gradlew :app:installDebug
adb shell am start -n com.rishu.browsecatalog/.MainActivity
```

Gradle uses the project's JDK 25 toolchain. Allow extra time for the first SDK, toolchain and image downloads. The backend needs internet access to Fake Store. To diagnose a loading error, open `http://localhost:3000/products` on the host and check `adb reverse --list`.

Choose a featured product, open **Enquire**, enter a name and email, and send it. Open the [local sales inbox](http://localhost:8025) and check the product and reply address. To change the recipient, copy `.env.example` to `.env`, edit `SALES_EMAIL`, and rerun Compose. Stop the containers with `docker compose down`.

## Tests

Run `node --test backend/server.test.mjs` with Node 22+ for catalogue mapping, validation and delivery. Run `./gradlew :app:testDebugUnitTest` for product search and category ordering.

## Decisions

- Home features five editorial picks, identified by stable product IDs in the backend. Each category highlights its highest-rated product and lists the rest in rating order. Ties use product ID.
- Search runs on submit across titles and descriptions, ignoring case. Prices use USD. The small catalogue fits in memory. Search scans it once. Sorting a category of k products costs O(k log k).
- The ViewModel depends on the Catalogue interface. A factory supplies the Retrofit repository, and Compose collects state with the screen lifecycle. Product selection and draft form fields survive rotation. Manual injection keeps the object graph small and lets tests substitute a repository. [Android architecture](https://developer.android.com/topic/architecture/recommendations) · [Dependency injection](https://developer.android.com/training/dependency-injection/manual)
- The backend owns the product source, recipient and mail adapter. A successful enquiry means Mailpit accepted the message. Local delivery gives reviewers a real inbox without credentials. The debug build allows HTTP to loopback.
- With another day, add durable enquiry IDs and deduplicated delivery before connecting a production mail provider. A lost acknowledgement can currently lead to a second email on retry. Production would also need HTTPS and request throttling.

See [AI usage and verification](AI_USAGE.md) for the implementation plan and test limits.
