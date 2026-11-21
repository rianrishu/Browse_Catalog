# AI usage

Codex (GPT-6.2 Astra) worked from the acceptance criteria and the [repository guidelines](AGENTS.md). The initial plan covered the data sources, the Android browsing and enquiry flows, then verification and reviewer setup.

## Decisions during development

1. The request to follow nearby Android projects led to a Compose UI, a ViewModel and an injected catalogue repository. The nearby Hilt movie project informed the package boundaries. A single constructor and lifecycle factory supply this app’s dependencies.
2. Reviewing the first implementation exposed two gaps. The five shopping flows shared one large composable, and categories displayed only one editorial pick. Each flow now has a screen and preview. Each category lists all its featured picks before its remaining products.
3. The original brief required a backend for product data. After a direct-access iteration, the backend-only requirement was reinstated. The TypeScript adapter owns Fake Store requests. Android fetches both catalogue records and photo bytes from the local Node.js server.
4. Real email delivery moved enquiries to the backend. Android submits the product and contact details. The server keeps SMTP credentials and the sales recipient in its environment. The ViewModel shows a confirmation after the mail provider accepts the message and offers a retry after failure.

## Verification

Five backend tests and seven JVM tests passed in the final code revision. The HTTP tests intercepted outgoing requests to verify the backend URL, product mapping, enquiry fields and handling of a rejected enquiry. The domain tests covered search, category ranking and ties. Production sources, debug previews and instrumented test sources compiled against cached Android libraries.

The runner blocked Gradle and adb socket access. APK packaging, emulator interaction, live Fake Store access and inbox delivery were unverified. The mail tests use an injected sender. Verifying Gmail delivery requires a configured sender account and a submission checked in the sales mailbox. The [README](README.md) covers both steps.

## Time spent

The initial implementation pass took about five minutes of elapsed tool and generation time. Planning took under a minute, building took about two minutes, and testing and review took about two minutes. Later screen and networking revisions were not timed, so a total for the full project is unavailable.
