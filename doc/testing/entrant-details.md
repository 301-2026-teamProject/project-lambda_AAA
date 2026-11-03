# Entrant details and joining (stories 5 and 6)

The detail view uses `CampsiteDetailController`, which loads campsites through
`CampsiteRepository`. `FirestoreCampsiteDetailDataSource` reads the current flat
`Lottery.toMap()` fields and preserves the Firestore document ID. It resolves the
saved user document by installation ID and delegates membership and joining to
the team's `EntryRepository`. Shared models and repositories are unchanged.

The controller checks dates, saved identity, existing membership and fresh lottery
data before joining. Registration end dates are inclusive. Existing results are
not deliberately reset to WAITING; withdrawn entrants may rejoin.

## Run tests

Use Android Studio's bundled JDK and a running Android emulator.

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug
firebase emulators:start --only firestore --project demo-lambda-details --config app/src/androidTest/firestore/firebase.json
```

In another terminal, run the real UI → controller → Firestore emulator flow:

```sh
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.lambda_aaa.CampsiteJoinFlowTest \
  -Pandroid.testInstrumentationRunnerArguments.firestoreEmulator=true
```

Before running, use `adb reverse tcp:8085 tcp:8085`. The demo project uses port
8085 through this localhost tunnel. Tests seed
unique campsite, lottery and user records, verify the saved entry from the server,
reload membership, and clean up their fixtures. The permissive rules in this
test directory are only for the emulator; do not deploy them.

Separately, the existing opt-in live campsite CRUD test uses the app's configured
Firebase project and deletes its unique temporary document:

```sh
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.lambda_aaa.FirestorePersistenceTest \
  -Pandroid.testInstrumentationRunnerArguments.firestoreIntegration=true
```

## Team integration limits

- The deployed rules checked November 3 deny lottery and entry access. Live joining
  needs appropriate team-owned rules; emulator success does not validate production rules.
- `Lottery` has no capacity field, so `joinWaitlist` receives 0 (unlimited).
- The shared entry repository is not transactional. Client checks cannot prevent
  simultaneous-device duplicate entries, result changes, or deadline races.
- This adapter supports the current flat schema. Update it when the team changes
  the campsite/lottery relationship or stored dates.
- Tests enter the detail screen directly; they do not validate the existing app
  startup/profile flow. A unique saved profile is required for joining.
