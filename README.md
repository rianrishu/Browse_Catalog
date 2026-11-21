# Browse Catalog

Browse and search the Fake Store catalogue from a Kotlin Android app. Retrofit and OkHttp fetch the catalogue from a local Node.js backend written in TypeScript. The backend calls Fake Store, maps its product records and streams the product photos.

## Run the Android app

Start the backend from the repository root with Node.js 22.18+. Install the mail dependency once:

```sh
npm install --prefix backend
node --env-file-if-exists=.env backend/server.ts
```

Open the project in Android Studio with SDK 37 installed and start an API 26+ emulator. In a second terminal:

```sh
adb reverse tcp:3000 tcp:3000
./gradlew :app:installDebug
adb shell am start -n com.rishu.browsecatalog/.MainActivity
```

Gradle uses the project's JDK 25 toolchain. The host needs internet access to Fake Store. Open `http://127.0.0.1:3000/products` on the host to check the backend.

## Try a sales enquiry

Copy `.env.example` to `.env` at the repository root. Enter your mail provider’s SMTP hostname, username and password or app password. Use port 587 for STARTTLS or 465 for implicit TLS. Set `MAIL_FROM` to an address your provider authorizes and `SALES_EMAIL` to the inbox that should receive enquiries. Restart the backend so it reads the settings.

Open a product, tap **Enquire**, enter your contact details and send. Android submits to `POST /enquiries` on the same backend as the catalogue. The backend addresses the email to sales and sets the customer's address as Reply-To. **Thank you** appears after the SMTP provider accepts the message. Check the destination mailbox, including its spam folder, to confirm arrival.

## Screen previews

With the debug variant selected in Android Studio, open `CataloguePreviews.kt` and select **Design** or **Split** to inspect the five screens with sample data. The running app loads product photos through the local backend.

## Tests

- `node --test backend/*.test.ts` checks upstream mapping, photo delivery, enquiry validation and mail-provider success and failure.
- `./gradlew :app:testDebugUnitTest` runs the JVM tests for search, category ordering, Fake Store response mapping and enquiry submission. HTTP tests use an OkHttp interceptor.
- `./gradlew :app:connectedDebugAndroidTest` runs the Compose journey tests on a connected emulator. They use a test repository. To check live delivery, follow **Try a sales enquiry** above.

## Decisions

- The backend marks five product IDs as featured. Edit `featuredIds` in `backend/catalogue.ts` to change the selection. A category highlights all its editorial picks and lists its remaining products separately. A category without a pick highlights the highest customer rating, including ties. Products within each section are ordered by rating, then ID.
- Search runs on submit across titles, descriptions and category names, ignoring case. Prices use USD. The small catalogue fits in memory. Search scans it once. Sorting a category of k products costs O(k log k).
- The ViewModel depends on the Catalogue interface. A factory supplies the Retrofit repository, and Compose collects state with the screen lifecycle. Product selection and draft form fields survive rotation. Manual injection keeps the object graph small and lets tests substitute a repository. [Android architecture](https://developer.android.com/topic/architecture/recommendations) · [Dependency injection](https://developer.android.com/training/dependency-injection/manual)
- The backend owns the upstream product contract. Replacing Fake Store requires an adapter change on the server. The debug build permits HTTP to loopback for the backend.
- With another day, add durable enquiry IDs and deduplicated delivery before a public launch. A lost acknowledgement can currently lead to a second email on retry. Production would also need HTTPS and request throttling.

See [AI usage and verification](AI_USAGE.md) for the implementation plan and test limits.
