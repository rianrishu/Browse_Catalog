# AI usage

Codex (GPT-6.2 Astra) worked from the acceptance criteria and the [repository guidelines](AGENTS.md). The initial plan covered the data sources, the Android browsing and enquiry flows, then verification and reviewer setup.

## Decisions during development

1. The request to follow nearby Android projects led to a Compose UI, a ViewModel and an injected catalogue repository. The nearby Hilt movie project informed the package boundaries. A single constructor and lifecycle factory supply this app’s dependencies.
2. Reviewing the first implementation exposed two gaps. The five shopping flows shared one large composable, and categories displayed only one editorial pick. Each flow now has a screen and preview. Each category lists all its featured picks before its remaining products.
3. The original brief required a backend for product data. The later request for direct Android networking changed that decision. Retrofit now fetches products from Fake Store, and the app loads photos from the HTTPS URLs in the response.
4. The request to send enquiries directly removed the Node service. Android posts the selected product and customer reply address to Mailpit over a port forwarded by adb. The ViewModel shows a confirmation after Mailpit accepts the request and offers a retry after failure.

## Verification

Seven JVM tests passed in the final code revision. The HTTP tests intercepted outgoing requests to verify the Fake Store URL, product mapping, Mailpit message fields and handling of a rejected enquiry. The domain tests covered search, category ranking and ties. Production sources, debug previews and instrumented test sources compiled against cached Android libraries.

The runner blocked Gradle and adb socket access. APK packaging, emulator interaction, live Fake Store access and inbox delivery were unverified. The [README](README.md) includes the emulator commands and a manual inbox check.

## Time spent

The initial implementation pass took about five minutes of elapsed tool and generation time. Planning took under a minute, building took about two minutes, and testing and review took about two minutes. Later screen and networking revisions were not timed, so a total for the full project is unavailable.
