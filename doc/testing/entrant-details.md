# Entrant details and joining (stories 5 and 6)

The detail view uses `CampsiteDetailController`, which loads campsites through
`CampsiteRepository`. `FirestoreCampsiteDetailDataSource` reads the current flat
`Lottery.toMap()` fields through the shared `Lottery.fromMap()` reader and preserves the Firestore document ID. It resolves the
saved user document by installation ID and delegates membership and joining to
the team's `EntryRepository`. The current model uses a single `Lottery.campsiteID` string; `Campsite.lottery` remains optional.

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

## Current-main campsite compatibility

`CampsiteRepository` uses `CampsiteFirestoreMapper` for single, owner, all-campsite
and listener reads, and for creates/updates. Availability writes use
`availablePeriod: {start: "YYYY-MM-DD", end: "YYYY-MM-DD"}`. Reads also accept the
nested LocalDate maps produced by the previous automatic serializer. Documents
without availability retain the shared model's default; that default is not proof
that an owner has configured availability.

An attached lottery is saved as its existing `toMap()` fields plus its `id`, with
`campsiteID` instead of recursively saving the campsite. Reads preserve the
lottery's campsite ID, original lottery ID, name and capacity. Creates use server timestamps; updates preserve the
stored creation timestamp. No bulk migration or production rules are deployed.
Older clients using `toObject(Campsite)` cannot decode these dates reliably; ship
the repository and mapper together. Rolling back to automatic mapping would
restore the loading bug, so retain the compatible reader for stored documents.

The emulator suite covers old automatic-date documents through the detail/join
flow and new repository create/read/update/list/listener/delete operations with
availability, price, an attached lottery and creation-time preservation.

Lottery details still query the separate `lotteries` collection because the
current `LotteryRepository.create()` writes there. The new `Campsite.lottery`
field alone does not establish a replacement authoritative lottery write path.

## Lottery ID and waitlist compatibility

The shared lottery reader accepts Firestore numeric counts as `Number`, validates
nonnegative integer values, and supports both flat date strings and older nested
periods. Missing legacy names/capacities remain readable. Create and update use
`toMap()` consistently; repository get/getAll and the detail adapter use `fromMap()`.

The joined-state button invokes Leave, then reloads membership. Only WAITING entries
may be withdrawn; join cannot reset a completed lottery result. Emulator tests cover
join/leave/rejoin persistence, full capacity for new and withdrawn entries, terminal
status protection, and the shared lottery repository's create/update/read format.

## Team integration limits

- The deployed rules rechecked November 5 deny lottery and entry access. Live joining
  needs appropriate team-owned rules; emulator success does not validate production rules.
- Joining fetches the latest lottery capacity and passes it to `joinWaitlist`.
  Missing capacity on legacy documents retains 0 (unlimited); withdrawn entrants
  must pass the same capacity check before rejoining.
- The shared entry repository is not transactional. Client checks cannot prevent
  simultaneous-device duplicate entries, result changes, or deadline races.
- This adapter supports the current flat schema. Update it when the team changes
  the campsite/lottery relationship or stored dates.
- Tests enter the detail screen directly; they do not validate the existing app
  startup/profile flow. A unique saved profile is required for joining.
