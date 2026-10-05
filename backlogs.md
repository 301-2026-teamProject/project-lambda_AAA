# Product Backlog

This backlog preserves the official user-story wording. Story points are relative estimates, risk describes implementation uncertainty, and **Halfway** indicates the recommended scope for the Part 3 prototype.

## Rating guide

- **Points:** 2 = small, 3 = normal, 5 = moderately complex, 8 = complex.
- **Low risk:** Mostly static UI or simple reads.
- **Medium risk:** Validation, navigation, or ordinary Firebase reads/writes.
- **High risk:** Security, transactions, concurrency, automation, device services, or unclear requirements.
- A story is **Done** only when every checkpoint passes and the relevant tests are included.

## Entrant

### Search and discovery

### US 01.01.01 — Search for campsites

> As an entrant, I want to search for campsites.

- **Points:** 3
- **Risk:** Medium — requires a clearly defined Firestore search strategy.
- **Halfway:** Yes

#### Checkpoints

- [ ] Entrant can submit a campsite name or search term.
- [ ] Only published matching campsites are returned.
- [ ] Loading, no-results, and Firestore-error states are handled.

### US 01.01.02 — Filter campsites

> As an entrant, I want to filter campsites by location, date, and number of guests.

- **Points:** 5
- **Risk:** High — combined date and availability filtering is difficult without SQL joins.
- **Halfway:** Yes

#### Checkpoints

- [ ] Entrant can enter location, a valid date range, and a positive guest count.
- [ ] Every displayed campsite satisfies all active filters.
- [ ] Filters can be changed or cleared and invalid input is explained.

### US 01.01.03 — View filtered campsite list

> As an entrant, I want to see a list of campsites based on my filters.

- **Points:** 3
- **Risk:** Medium — depends on the filtering query and result-state handling.
- **Halfway:** Yes

#### Checkpoints

- [ ] Results update when filters change.
- [ ] Only matching campsites are listed and an empty state is provided.
- [ ] Selecting a result opens the correct campsite details.

### US 01.01.04 — View campsite summaries

> As an entrant, I want to see campsite summary information including name, location, maximum occupancy, and rating.

- **Points:** 2
- **Risk:** Low — primarily presentation of existing data.
- **Halfway:** Yes

#### Checkpoints

- [ ] Every result shows name, location, maximum occupancy, and rating.
- [ ] A campsite without ratings displays an appropriate placeholder.
- [ ] Summary values match the stored campsite document.

### US 01.01.05 — View campsite details

> As an entrant, I want to see details of a campsite.

- **Points:** 3
- **Risk:** Medium — combines campsite, image, rating, location, and lottery data.
- **Halfway:** Yes

#### Checkpoints

- [ ] Details include the required campsite fields, rules, policy, and availability.
- [ ] Optional image and map location are shown when present.
- [ ] Missing optional data and loading failures are handled safely.

### US 01.01.06 — Join a lottery from campsite details

> As an entrant, I want to join an active lottery from the campsite details screen.

- **Points:** 3
- **Risk:** Medium — must reflect live lottery eligibility.
- **Halfway:** Yes

#### Checkpoints

- [ ] Active lotteries are visible on the campsite-details screen.
- [ ] Join is enabled only while the lottery accepts entrants.
- [ ] Successful joining updates the screen; closed or full lotteries show a reason.

### Waiting lists and lottery results

### US 01.02.01 — Join a waiting list

> As an entrant, I want to join the waiting list for a campsite lottery.

- **Points:** 5
- **Risk:** High — duplicate prevention and capped lists require an atomic transaction.
- **Halfway:** Yes

#### Checkpoints

- [ ] Eligible entrant can join once while registration is open.
- [ ] Capacity and duplicate-entry rules are enforced atomically.
- [ ] Firestore entry/count and the UI remain consistent on success or failure.

### US 01.02.02 — Leave a waiting list

> As an entrant, I want to leave the waiting list for a campsite lottery.

- **Points:** 3
- **Risk:** Medium — entry status and lottery count must remain consistent.
- **Halfway:** Yes

#### Checkpoints

- [ ] A waiting entrant can request and confirm leaving.
- [ ] Entry becomes withdrawn and the lottery count is updated safely.
- [ ] Withdrawn entrants cannot be selected as winners.

### US 01.03.01-A — Winner notification

> As an entrant, I want to receive a notification when I win the campsite lottery.

- **Points:** 5
- **Risk:** High — notification delivery and idempotency must be coordinated with the draw.
- **Halfway:** No

#### Checkpoints

- [ ] Every selected entrant receives exactly one winner notification.
- [ ] Notification identifies the lottery and opens the booking opportunity.
- [ ] Notification is recorded in history and respects preferences.

### US 01.03.01-B — Non-winner notification

> As an entrant, I want to receive a notification when I lose the campsite lottery.

- **Points:** 5
- **Risk:** High — all remaining entrants must be updated without duplicates.
- **Halfway:** No

#### Checkpoints

- [ ] Every eligible non-winner receives exactly one result notification.
- [ ] Their waitlist status becomes `NOT_SELECTED`.
- [ ] Notification is recorded in history and respects preferences.

> The `A` and `B` suffixes distinguish the duplicate ID in the official specification.

### Booking opportunities and reservations

### US 01.04.01 — View reservable dates

> As an entrant, if I win the lottery, I want to see available dates that I can reserve the campsite for.

- **Points:** 5
- **Risk:** High — availability can change concurrently while winners book.
- **Halfway:** No

#### Checkpoints

- [ ] Only selected winners can access remaining dates.
- [ ] Booked or out-of-period dates cannot be selected.
- [ ] Availability refreshes and an empty state is shown when none remain.

### US 01.04.02 — Choose campsite and dates

> As an entrant, if I win a multiple campsite lottery, I want to choose which campsite to reserve and which dates I want.

- **Points:** 8
- **Risk:** High — combines multi-campsite selection with concurrent availability.
- **Halfway:** No

#### Checkpoints

- [ ] Winner can choose among the lottery's eligible campsites.
- [ ] Available dates update for the selected campsite.
- [ ] Only a valid campsite/date combination can proceed to confirmation.

### US 01.04.03 — Decline reservation opportunity

> As an entrant, if I win the lottery, I want to decline a reservation if I changed my mind or if none of the remaining dates work for me.

- **Points:** 3
- **Risk:** Medium — declining must update lottery state without booking dates.
- **Halfway:** No

#### Checkpoints

- [ ] Winner can explicitly confirm a decline.
- [ ] Entry becomes `DECLINED` and no reservation is created.
- [ ] Declining leaves campsite dates available to others.

### US 01.05.01 — Receive reservation confirmation

> As an entrant, I want to receive confirmation of my campsite reservation.

- **Points:** 5
- **Risk:** High — reservation, availability, and winner state require an atomic update.
- **Halfway:** No

#### Checkpoints

- [ ] Confirmation appears only after a successful atomic reservation.
- [ ] It shows campsite, dates, guest count, and reservation status.
- [ ] Confirmed dates cannot be double booked.

### US 01.05.02 — Receive or export reservation

> As an entrant, I want to receive reservation by notification or downloadable CSV.

- **Points:** 3
- **Risk:** Medium — requires reliable notification and Android file sharing/export.
- **Halfway:** No

#### Checkpoints

- [ ] Confirmed reservation creates a notification.
- [ ] Entrant can download or share a correctly formatted CSV.
- [ ] Exported values match the stored reservation.

### US 01.06.01 — View active reservations

> As an entrant, I want to view a list of my active reservations.

- **Points:** 3
- **Risk:** Medium — requires user-scoped status/date queries and indexes.
- **Halfway:** No

#### Checkpoints

- [ ] Only the current entrant's active reservations are listed.
- [ ] Each item shows campsite, dates, and status and opens details.
- [ ] Empty, loading, and error states are handled.

### US 01.06.02 — View past reservations

> As an entrant, I want to view a list of my past reservations.

- **Points:** 3
- **Risk:** Medium — requires consistent past/completed classification.
- **Halfway:** No

#### Checkpoints

- [ ] Only the current entrant's past/completed reservations are listed.
- [ ] Entries are ordered consistently and open their details.
- [ ] Empty, loading, and error states are handled.

### US 01.07.01 — Cancel a reservation

> As an entrant, I want to cancel my campsite reservation.

- **Points:** 5
- **Risk:** High — cancellation affects availability, status, owner notification, and refund policy.
- **Halfway:** No

#### Checkpoints

- [ ] Eligible reservation displays its policy and requires confirmation.
- [ ] Status becomes `CANCELLED_BY_ENTRANT` and dates are released as appropriate.
- [ ] Owner is notified and cancellation cannot be applied twice.

### US 01.07.02 — Receive cancellation confirmation

> As an entrant, I want to receive confirmation of my campsite cancellation by notification or downloadable CSV.

- **Points:** 3
- **Risk:** Medium — confirmation must reflect the committed cancellation.
- **Halfway:** No

#### Checkpoints

- [ ] Successful cancellation creates a confirmation notification.
- [ ] Entrant can download/share a CSV with reservation ID, dates, and status.
- [ ] No confirmation is produced when cancellation fails.

### Profile, preferences, QR, and ratings

### US 01.07.03 — Update profile

> As an entrant, I want to update my personal information, such as name, email, and phone number, on my profile.

- **Points:** 3
- **Risk:** Medium — personal data requires validation and owner-only security rules.
- **Halfway:** Yes

#### Checkpoints

- [ ] Entrant can edit name, email, and phone with validation.
- [ ] Valid changes persist in the current user's Firestore profile.
- [ ] Another user cannot read or update unauthorized personal data.

### US 01.08.01 — Delete profile

> As an entrant, I want to delete my profile.

- **Points:** 5
- **Risk:** High — deletion, historical records, images, and identity require a retention policy.
- **Halfway:** No

#### Checkpoints

- [ ] Consequences are explained and deletion requires explicit confirmation.
- [ ] Profile is removed or anonymized according to the agreed policy.
- [ ] Session is cleared while required reservation history remains consistent.

### US 01.09.01 — Device-based identification

> As an entrant, I want to be identified by my device, so that I do not have to use a username and password.

- **Points:** 5
- **Risk:** High — anonymous identity persistence and reinstall behavior need clarification.
- **Halfway:** Yes

#### Checkpoints

- [ ] First launch creates an anonymous Firebase identity without credentials.
- [ ] The same profile loads after normal app restarts.
- [ ] Authentication failure is recoverable and protected data requires a valid UID.

### US 01.10.01 — Opt out of notifications

> As an entrant, I want to opt out of receiving notifications.

- **Points:** 3
- **Risk:** Medium — push delivery and in-app history need distinct behavior.
- **Halfway:** No

#### Checkpoints

- [ ] Entrant can enable or disable notifications.
- [ ] Preference persists in their profile.
- [ ] Optional push delivery respects the preference while required information remains in-app.

### US 01.11.01 — View policy and guidelines

> As an entrant, I want to see the app policy and guidelines.

- **Points:** 2
- **Risk:** Low — primarily static, read-only content.
- **Halfway:** No

#### Checkpoints

- [ ] Policy is accessible from an obvious location.
- [ ] Content is readable, scrollable, and available without a live query.
- [ ] Entrant can return to the previous screen.

### US 01.12.01 — Scan campsite QR code

> As an entrant, I want to view campsite details by scanning the promotional QR code.

- **Points:** 5
- **Risk:** High — requires camera permission, scanning, and safe deep-link validation.
- **Halfway:** No

#### Checkpoints

- [ ] Camera permission and denial are handled safely.
- [ ] Valid code opens the exact campsite details.
- [ ] Invalid, unsupported, or removed-campsite codes show an error.

### US 01.13.01 — Rate a campsite

> As an entrant, I want to give a rating for a campsite.

- **Points:** 5
- **Risk:** High — eligibility, duplicate prevention, and aggregate updates must agree.
- **Halfway:** No

#### Checkpoints

- [ ] Only an eligible entrant can submit a rating in the allowed range.
- [ ] Duplicate rating behavior is prevented or explicitly treated as an update.
- [ ] Campsite average and rating count update correctly.

## Campsite owner

### Campsite management

### US 02.01.01 — Register a campsite

> As a campsite owner, I want to register my campsite with the app.

- **Points:** 5
- **Risk:** Medium — requires validation, ownership, and Firestore creation.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can submit all required campsite information.
- [ ] Valid submission creates a campsite linked to the current owner.
- [ ] New campsite appears in the owner's list; failures show no false success.

### US 02.01.02 — Provide campsite details

> As a campsite owner, I want to provide campsite details including name, location, description (amenities), maximum occupancy, map location, optional image, and tags.

- **Points:** 5
- **Risk:** Medium — combines form validation, GeoPoint data, tags, and optional storage upload.
- **Halfway:** Yes

#### Checkpoints

- [ ] Form captures every required field and validates occupancy/location.
- [ ] Optional image upload is handled without blocking image-free listings.
- [ ] Saved data matches the entrant and owner detail screens.

### US 02.01.03 — Update campsite details

> As a campsite owner, I want to update details about my campsite.

- **Points:** 3
- **Risk:** Medium — updates must be owner-authorized and validated.
- **Halfway:** Yes

#### Checkpoints

- [ ] Existing details are pre-filled and valid changes can be saved.
- [ ] Updated values appear to entrants.
- [ ] Owners cannot edit campsites they do not own.

### US 02.01.04 — View owned campsite details

> As a campsite owner, I want to view details of my campsite.

- **Points:** 2
- **Risk:** Low — primarily an owner-scoped read.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can open complete details from My Campsites.
- [ ] Availability and lottery information are accessible.
- [ ] Edit controls appear only for the owner.

### US 02.02.01 — Generate campsite QR code

> As a campsite owner, I want the system to generate a unique QR code that links to the campsite details.

- **Points:** 5
- **Risk:** High — encoding, sharing, and scanner/deep-link compatibility must agree.
- **Halfway:** No

#### Checkpoints

- [ ] Each campsite produces a stable, unique QR payload.
- [ ] Owner can view and share/save the code.
- [ ] Scanning resolves to the correct campsite and handles removal.

### US 02.03.01 — View owned campsites

> As a campsite owner, I want to view a list of my campsites.

- **Points:** 3
- **Risk:** Medium — requires owner-scoped querying and security.
- **Halfway:** Yes

#### Checkpoints

- [ ] Only current owner's campsites are listed.
- [ ] Every item shows useful summary information and opens details.
- [ ] Empty, loading, and error states are handled.

### Lottery management

### US 02.04.01 — Create a campsite lottery

> As a campsite owner, I want to list my campsite in a lottery for a certain period.

- **Points:** 5
- **Risk:** High — dates, ownership, lifecycle state, and visibility must be consistent.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can select an owned campsite and configure required lottery fields.
- [ ] Valid submission creates a lottery linked to that campsite and owner.
- [ ] Lottery becomes joinable only during its registration period.

### US 02.04.02 — Create a multiple-campsite lottery

> As a campsite owner, I want be able to list multiple campsites in a lottery for a certain period.

- **Points:** 5
- **Risk:** High — downstream winner booking must support multiple availability sets.
- **Halfway:** No

#### Checkpoints

- [ ] Owner can select multiple campsites they own.
- [ ] At least one campsite is required and IDs are stored with the lottery.
- [ ] Winner flow can identify every eligible campsite.

### US 02.04.03 — Set registration period

> As a campsite owner, I want to set a registration period.

- **Points:** 3
- **Risk:** Medium — timestamp and timezone boundaries affect eligibility.
- **Halfway:** Yes

#### Checkpoints

- [ ] Start precedes end and both are stored as timestamps.
- [ ] Entrants cannot join before the start or after the end.
- [ ] Times display consistently in the intended timezone.

### US 02.04.04 — Set booking period

> As a campsite owner, I want to set a booking period.

- **Points:** 3
- **Risk:** High — booking dates must agree with availability and timezone logic.
- **Halfway:** Yes

#### Checkpoints

- [ ] Booking start precedes booking end.
- [ ] Period is compatible with campsite availability.
- [ ] Winners cannot reserve outside the configured period.

### US 02.04.05 — Set winner count

> As a campsite owner, I want to set the number of lottery winners.

- **Points:** 2
- **Risk:** Medium — value must remain valid relative to capacity and entrants.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can enter a positive whole number.
- [ ] Value does not exceed an optional waitlist capacity.
- [ ] Draw logic uses the stored winner count.

### US 02.04.06 — Update lottery listing

> As a campsite owner, I want to update my campsite lottery listing.

- **Points:** 5
- **Risk:** High — edits after entrants join or drawing begins can invalidate state.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can edit and validate a lottery they own.
- [ ] Changes appear to entrants.
- [ ] Unsafe fields are locked after registration/drawing reaches the agreed state.

### US 02.04.07 — Set optional waitlist capacity

> As a campsite owner, I want to optionally set a capacity for my lottery waitlist.

- **Points:** 3
- **Risk:** High — concurrent joins must never exceed capacity.
- **Halfway:** Yes

#### Checkpoints

- [ ] Blank capacity means unlimited; entered capacity is a positive whole number.
- [ ] Capacity cannot be below winner count or current entry count.
- [ ] Atomic join logic prevents the list from exceeding capacity.

### US 02.05.01 — Automatically draw winners

> As a campsite owner, I want the system to automatically draw winners after the registration period ends.

- **Points:** 8
- **Risk:** High — trusted scheduling, randomness, atomicity, and repeat prevention are unresolved.
- **Halfway:** No

#### Checkpoints

- [ ] Draw cannot occur before registration closes and uses only eligible entries.
- [ ] Configured number of unique winners is selected and all statuses update consistently.
- [ ] Draw is idempotent: retries cannot create a second result or duplicate notifications.
- [ ] Team confirms with the TA whether scheduled backend execution is required.

### Reservations and communication

### US 02.06.01 — View booked dates

> As a campsite owner, I want to view the currently booked dates for my campsite.

- **Points:** 5
- **Risk:** High — calendar must reflect live reservation/cancellation state.
- **Halfway:** No

#### Checkpoints

- [ ] Confirmed dates appear as booked for an owned campsite.
- [ ] Cancelled/rejected dates are represented according to availability policy.
- [ ] Selecting a booked date shows authorized reservation information.

### US 02.07.01 — View active reservations

> As a campsite owner, I want to view a list of active reservations.

- **Points:** 3
- **Risk:** Medium — requires owner/status/date indexes and scoped access.
- **Halfway:** No

#### Checkpoints

- [ ] Only active reservations for owned campsites are listed.
- [ ] Each item shows campsite, entrant, dates, and status and opens details.
- [ ] Empty, loading, and error states are handled.

### US 02.07.02 — View past reservations

> As a campsite owner, I want to view a list of past reservations.

- **Points:** 3
- **Risk:** Medium — requires consistent past/completed classification.
- **Halfway:** No

#### Checkpoints

- [ ] Only past/completed reservations for owned campsites are listed.
- [ ] Entries are ordered consistently and open details.
- [ ] Empty, loading, and error states are handled.

### US 02.07.03 — View cancelled reservations

> As a campsite owner, I want to view a list of cancelled reservations.

- **Points:** 3
- **Risk:** Medium — multiple cancellation/rejection statuses must be classified.
- **Halfway:** No

#### Checkpoints

- [ ] Cancelled and rejected reservations for owned campsites are listed.
- [ ] Cancellation type/date are visible and details can be opened.
- [ ] Empty, loading, and error states are handled.

### US 02.08.01 — Reject a reservation

> As a campsite owner, I want to reject a reservation.

- **Points:** 5
- **Risk:** High — rejection affects availability, entrant notification, and refund state.
- **Halfway:** No

#### Checkpoints

- [ ] Owner can confirm rejection only for an eligible owned reservation.
- [ ] Status and availability update consistently and cannot be rejected twice.
- [ ] Entrant receives a reason/notification and refund state is recorded if required.

### US 02.09.01 — View waitlist entrants

> As a campsite owner, I want to view a list of entrants who have joined the waitlist for my campsite lottery.

- **Points:** 3
- **Risk:** Medium — personally identifying data requires owner-scoped rules.
- **Halfway:** Yes

#### Checkpoints

- [ ] Owner can view entrants and statuses for an owned lottery.
- [ ] Entrants from other lotteries are excluded.
- [ ] Unauthorized owners are denied; empty/loading/error states are handled.

### US 02.10.01 — Notify waitlist entrants

> As a campsite owner, I want to send notifications to entrants who have joined the waitlist for my campsite lottery.

- **Points:** 5
- **Risk:** High — recipient authorization, fan-out, preferences, and duplicates must be handled.
- **Halfway:** No

#### Checkpoints

- [ ] Owner can compose/confirm a notification for an owned lottery.
- [ ] It is recorded and delivered only to that lottery's entrants.
- [ ] Failures and retries do not create unintended duplicates.

### US 02.10.02 — View sent notifications

> As a campsite owner, I want to view a list of all notifications I have sent.

- **Points:** 3
- **Risk:** Medium — requires sender-scoped logging and indexes.
- **Halfway:** No

#### Checkpoints

- [ ] Only notifications sent by the current owner are listed.
- [ ] Each item shows lottery, message, recipients, and time.
- [ ] Entries are ordered and can be opened for details.

### US 02.11.01 — Require rules acknowledgement

> As a campsite owner, I want entrants to confirm that they have read the campsite rules and policies before confirming their reservation.

- **Points:** 3
- **Risk:** Medium — acknowledgement must be enforced, not merely displayed.
- **Halfway:** No

#### Checkpoints

- [ ] Rules/policies are shown before final confirmation.
- [ ] Reservation cannot be confirmed until entrant explicitly acknowledges them.
- [ ] Acknowledgement and timestamp are stored with the reservation.

### US 02.12.01 — Message a reserving entrant

> As a campsite owner, I want to message a user who is reserving my campsite.

- **Points:** 5
- **Risk:** High — participant authorization, delivery, content, and admin logging are required.
- **Halfway:** No

#### Checkpoints

- [ ] Owner can message only entrants with a reservation for their campsite.
- [ ] Non-empty message records sender, recipient, reservation, and timestamp.
- [ ] Entrant can view it and admin can audit the stored message.

## Administrator

### Moderation and auditing

### US 03.01.01 — Remove campsites

> As an admin, I want to remove registered campsites.

- **Points:** 5
- **Risk:** High — removal affects listings, lotteries, reservations, and audit history.
- **Halfway:** No

#### Checkpoints

- [ ] Authorized admin can confirm removal of a selected campsite.
- [ ] Removed campsite no longer appears publicly and related state is handled.
- [ ] Non-admins are denied and the action is logged.

### US 03.02.01 — Remove profiles

> As an admin, I want to remove profiles.

- **Points:** 5
- **Risk:** High — identity removal must preserve or anonymize required history.
- **Halfway:** No

#### Checkpoints

- [ ] Authorized admin can confirm removal of a selected profile.
- [ ] Profile is removed/anonymized according to retention policy.
- [ ] Non-admins are denied and the action is logged.

### US 03.03.01 — Remove images

> As an admin, I want to remove images.

- **Points:** 5
- **Risk:** High — Cloud Storage file and Firestore reference must remain consistent.
- **Halfway:** No

#### Checkpoints

- [ ] Authorized admin can confirm removal of an uploaded image.
- [ ] Storage object and database reference are cleared consistently.
- [ ] A placeholder is shown and the action is logged.

### US 03.04.01 — Browse campsites

> As an admin, I want to browse campsites.

- **Points:** 3
- **Risk:** Medium — admin scope and potentially large result sets require care.
- **Halfway:** No

#### Checkpoints

- [ ] Admin can list and identify registered campsites.
- [ ] Selecting one opens complete moderation details.
- [ ] Non-admin access and loading/empty/error states are handled.

### US 03.05.01 — Browse profiles

> As an admin, I want to browse profiles.

- **Points:** 3
- **Risk:** Medium — exposes personal data and therefore needs strict rules.
- **Halfway:** No

#### Checkpoints

- [ ] Admin can list profiles with appropriate identity and roles.
- [ ] Selecting one opens the moderation details needed by the requirement.
- [ ] Non-admin access is denied and unnecessary private data is not exposed.

### US 03.06.01 — Browse uploaded images

> As an admin, I want to browse uploaded images so I can remove them if necessary.

- **Points:** 5
- **Risk:** High — cross-record Storage inventory and ownership metadata are required.
- **Halfway:** No

#### Checkpoints

- [ ] Admin can browse campsite/profile images with their associated records.
- [ ] Image can be previewed and sent to the removal flow.
- [ ] Missing files and non-admin access are handled safely.

### US 03.07.01 — Remove violating campsite owners

> As an admin, I want to remove campsite owners that violate app policy.

- **Points:** 8
- **Risk:** High — role removal affects campsites, lotteries, reservations, and possibly entrant access.
- **Halfway:** No

#### Checkpoints

- [ ] Admin confirms the owner and records a reason.
- [ ] Owner privileges/listings are disabled according to the agreed policy.
- [ ] Existing reservations remain consistent and the action is logged.

### US 03.08.01 — Review notification logs

> As an admin, I want to review logs of all notifications.

- **Points:** 3
- **Risk:** Medium — global logs require privileged access and retention rules.
- **Halfway:** No

#### Checkpoints

- [ ] Admin can list notifications with sender, recipient, type, and time.
- [ ] Logs can be ordered/filtered and opened for details.
- [ ] Ordinary users cannot access global notification logs.

### US 03.11.01 — Use multiple roles

> As an admin, I should be able to be a campsite owner and/or entrant with my admin profile.

- **Points:** 5
- **Risk:** High — multi-role navigation must not weaken admin security.
- **Halfway:** No

#### Checkpoints

- [ ] One profile can hold admin, owner, and/or entrant roles.
- [ ] User can access assigned role features without a second account.
- [ ] Active role is clear and ordinary role actions follow their normal rules.

### US 03.12.01 — Review message logs

> As an admin, I want to review logs of all messages from campsite owners to entrants who reserved their campsite.

- **Points:** 3
- **Risk:** Medium — global message content is privacy-sensitive.
- **Halfway:** No

#### Checkpoints

- [ ] Admin can list messages with sender, recipient, reservation, and time.
- [ ] Logs can be ordered/filtered and opened for complete details.
- [ ] Ordinary users cannot access global message logs.

## Highest-risk decisions to confirm with the TA

- [ ] Define what campsite “search” covers and how date/location filtering should behave.
- [ ] Confirm whether automatic winner drawing requires a scheduled backend function.
- [ ] Define device identity behavior after app reinstall or cleared app data.
- [ ] Define profile/campsite deletion versus soft deletion and historical retention.
- [ ] Define refund behavior; no real payment requirement is currently specified.
- [ ] Confirm whether required messaging is one-way owner messaging or a two-way conversation.
- [ ] Define notification opt-out behavior for push notifications versus in-app records.
