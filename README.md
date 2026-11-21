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

Enable 2-Step Verification on the Gmail account that will send enquiries. Generate an app password for Browse Catalog and store it in the backend's local environment. [Google app password instructions](https://support.google.com/accounts/answer/185833)

Copy `.env.example` to `.env` in the repository root and fill in these settings. Use your full Gmail address for both `SMTP_USER` and `MAIL_FROM`. For a first test, you can also use that address as `SALES_EMAIL`.

```dotenv
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-account@gmail.com
SMTP_PASSWORD=your-google-app-password
MAIL_FROM=your-account@gmail.com
SALES_EMAIL=sales@example.com
```

Restart the backend to load the settings. It connects to Gmail over STARTTLS. [Google SMTP settings](https://support.google.com/a/answer/176600)

Open a product, tap **Enquire**, enter a name and contact email, then submit. **Thank you** appears when Gmail accepts the message. In the sales mailbox, look for **Product enquiry #<product ID>** and check the product name. Click **Reply** to verify that Gmail addresses the reply to the customer. Check Spam if the message is missing from the inbox.

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
