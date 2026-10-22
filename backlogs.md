# Product Backlog

This backlog preserves the official user-story wording. Story points are relative estimates, risk describes implementation uncertainty, and **Halfway** indicates the recommended scope for the Part 3 prototype. Checkpoints are written as **UI acceptance criteria**: something the actor can see, select, enter, confirm, or receive in the application.

## Rating guide

- **Points:** 2 = small, 3 = normal, 5 = moderately complex, 8 = complex.
- **Low risk:** Mostly static UI or simple reads.
- **Medium risk:** Validation, navigation, or ordinary Firebase reads/writes.
- **High risk:** Security, transactions, concurrency, automation, device services, or unclear requirements.
- A story is **Done** only when every checkpoint can be demonstrated through the UI and the relevant UI/intent tests pass.
- Firebase transactions, rules, and storage remain part of the implementation risk, but the checkpoints describe their user-visible results.

## Entrant

### Search and discovery

### US 01.01.01 — Search for campsites

> As an entrant, I want to search for campsites.

- **Points:** 3
- **Risk:** Medium — requires a clearly defined Firestore search strategy.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Entrant can submit a campsite name or search term.
- [ ] Only published matching campsites are returned.
- [ ] The screen shows loading feedback, a clear no-results message, and a retryable error message.

### US 01.01.02 — Filter campsites

> As an entrant, I want to filter campsites by location, date, and number of guests.

- **Points:** 5
- **Risk:** High — combined date and availability filtering is difficult without SQL joins.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Entrant can enter location, a valid date range, and a positive guest count.
- [ ] Every displayed campsite satisfies all active filters.
- [ ] Filters can be changed or cleared and invalid input is explained.

### US 01.01.03 — View filtered campsite list

> As an entrant, I want to see a list of campsites based on my filters.

- **Points:** 3
- **Risk:** Medium — depends on the filtering query and result-state handling.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Results update when filters change.
- [ ] Only matching campsites are listed and an empty state is provided.
- [ ] Selecting a result opens the correct campsite details.

### US 01.01.04 — View campsite summaries

> As an entrant, I want to see campsite summary information including name, location, maximum occupancy, and rating.

- **Points:** 2
- **Risk:** Low — primarily presentation of existing data.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Every result shows name, location, maximum occupancy, and rating.
- [ ] A campsite without ratings displays an appropriate placeholder.
- [ ] Summary values match the information shown on the campsite-details screen.

### US 01.01.05 — View campsite details

> As an entrant, I want to see details of a campsite.

- **Points:** 3
- **Risk:** Medium — combines campsite, image, rating, location, and lottery data.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Details include the required campsite fields, rules, policy, and availability.
- [ ] Optional image and map location are shown when present.
- [ ] Missing optional data and loading failures are handled safely.

### US 01.01.06 — Join a lottery from campsite details

> As an entrant, I want to join an active lottery from the campsite details screen.

- **Points:** 3
- **Risk:** Medium — must reflect live lottery eligibility.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Active lotteries are visible on the campsite-details screen.
- [ ] Join is enabled only while the lottery accepts entrants.
- [ ] Successful joining updates the screen; closed or full lotteries show a reason.

### Waiting lists and lottery results

### US 01.02.01 — Join a waiting list

> As an entrant, I want to join the waiting list for a campsite lottery.

- **Points:** 5
- **Risk:** High — duplicate prevention and capped lists require an atomic transaction.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Eligible entrant sees an enabled **Join Waiting List** button while registration is open.
- [ ] After joining, the button changes to **Leave Waiting List** and a success message appears.
- [ ] Already-joined, full, or closed lotteries disable joining and explain why.

### US 01.02.02 — Leave a waiting list

> As an entrant, I want to leave the waiting list for a campsite lottery.

- **Points:** 3
- **Risk:** Medium — entry status and lottery count must remain consistent.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] A joined entrant can select **Leave Waiting List** and sees a confirmation dialog.
- [ ] After confirmation, the screen returns to the joinable state and shows success feedback.
- [ ] The lottery no longer appears in the entrant's joined-waitlists view.

### US 01.03.01 — Winner notification

> As an entrant, I want to receive a notification when I win the campsite lottery.

- **Points:** 5
- **Risk:** High — notification delivery and idempotency must be coordinated with the draw.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Selected entrant sees exactly one winner notification with campsite and lottery information.
- [ ] Selecting the notification opens the available-dates booking screen.
- [ ] The notification appears in history and its read/unread state is visible.

### US 01.03.02 — Non-winner notification

> As an entrant, I want to receive a notification when I lose the campsite lottery.

- **Points:** 5
- **Risk:** High — all remaining entrants must be updated without duplicates.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Non-selected entrant sees exactly one result notification identifying the lottery.
- [ ] The lottery displays a visible **Not selected** result instead of booking controls.
- [ ] The notification appears in history and its read/unread state is visible.

### Booking opportunities and reservations

### US 01.04.01 — View reservable dates

> As an entrant, if I win the lottery, I want to see available dates that I can reserve the campsite for.

- **Points:** 5
- **Risk:** High — availability can change concurrently while winners book.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Only selected winners can access remaining dates.
- [ ] Booked or out-of-period dates cannot be selected.
- [ ] Availability refreshes and an empty state is shown when none remain.

### US 01.04.02 — Choose campsite and dates

> As an entrant, if I win a multiple campsite lottery, I want to choose which campsite to reserve and which dates I want.

- **Points:** 8
- **Risk:** High — combines multi-campsite selection with concurrent availability.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Winner can choose among the lottery's eligible campsites.
- [ ] Available dates update for the selected campsite.
- [ ] Only a valid campsite/date combination can proceed to confirmation.

### US 01.04.03 — Decline reservation opportunity

> As an entrant, if I win the lottery, I want to decline a reservation if I changed my mind or if none of the remaining dates work for me.

- **Points:** 3
- **Risk:** Medium — declining must update lottery state without booking dates.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Winner can explicitly confirm a decline.
- [ ] Entry becomes `DECLINED` and no reservation is created.
- [ ] Declining leaves campsite dates available to others.

### US 01.05.01 — Receive reservation confirmation

> As an entrant, I want to receive confirmation of my campsite reservation.

- **Points:** 5
- **Risk:** High — reservation, availability, and winner state require an atomic update.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] The confirmation screen appears only after the app reports a successful reservation.
- [ ] It shows campsite, dates, guest count, and reservation status.
- [ ] If selected dates were taken by someone else, the entrant sees an explanation and must choose again.

### US 01.05.02 — Receive or export reservation

> As an entrant, I want to receive reservation by notification or downloadable CSV.

- **Points:** 3
- **Risk:** Medium — requires reliable notification and Android file sharing/export.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Entrant sees a reservation-confirmation notification in the application.
- [ ] Entrant can download or share a correctly formatted CSV.
- [ ] Exported values match the values displayed on the reservation-details screen.

### US 01.06.01 — View active reservations

> As an entrant, I want to view a list of my active reservations.

- **Points:** 3
- **Risk:** Medium — requires user-scoped status/date queries and indexes.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only the current entrant's active reservations are listed.
- [ ] Each item shows campsite, dates, and status and opens details.
- [ ] Empty, loading, and error states are handled.

### US 01.06.02 — View past reservations

> As an entrant, I want to view a list of my past reservations.

- **Points:** 3
- **Risk:** Medium — requires consistent past/completed classification.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only the current entrant's past/completed reservations are listed.
- [ ] Entries are ordered consistently and open their details.
- [ ] Empty, loading, and error states are handled.

### US 01.07.01 — Cancel a reservation

> As an entrant, I want to cancel my campsite reservation.

- **Points:** 5
- **Risk:** High — cancellation affects availability, status, owner notification, and refund policy.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Eligible reservation displays its policy and requires confirmation.
- [ ] After confirmation, the reservation visibly moves to the cancelled list and displays **Cancelled by entrant**.
- [ ] Owner is notified and cancellation cannot be applied twice.

### US 01.07.02 — Receive cancellation confirmation

> As an entrant, I want to receive confirmation of my campsite cancellation by notification or downloadable CSV.

- **Points:** 3
- **Risk:** Medium — confirmation must reflect the committed cancellation.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Successful cancellation creates a confirmation notification.
- [ ] Entrant can download/share a CSV with reservation ID, dates, and status.
- [ ] No confirmation is produced when cancellation fails.

### Profile, preferences, QR, and ratings

### US 01.07.03 — Update profile

> As an entrant, I want to update my personal information, such as name, email, and phone number, on my profile.

- **Points:** 3
- **Risk:** Medium — personal data requires validation and owner-only security rules.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Entrant can edit name, email, and phone with validation.
- [ ] Saving valid changes returns to the profile screen and displays the updated information.
- [ ] Reopening the profile shows the saved values; save failure leaves the previous values visible.

### US 01.08.01 — Delete profile

> As an entrant, I want to delete my profile.

- **Points:** 5
- **Risk:** High — deletion, historical records, images, and identity require a retention policy.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Consequences are explained and deletion requires explicit confirmation.
- [ ] Profile is removed or anonymized according to the agreed policy.
- [ ] Session is cleared while required reservation history remains consistent.

### US 01.09.01 — Device-based identification

> As an entrant, I want to be identified by my device, so that I do not have to use a username and password.

- **Points:** 5
- **Risk:** High — anonymous identity persistence and reinstall behavior need clarification.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] First launch enters the app without asking for a username or password.
- [ ] The same profile and user data appear after closing and reopening the app.
- [ ] Identification failure shows a retry screen instead of exposing an empty or incorrect profile.

### US 01.10.01 — Opt out of notifications

> As an entrant, I want to opt out of receiving notifications.

- **Points:** 3
- **Risk:** Medium — push delivery and in-app history need distinct behavior.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Entrant can enable or disable notifications.
- [ ] Preference persists in their profile.
- [ ] Optional push delivery respects the preference while required information remains in-app.

### US 01.11.01 — View policy and guidelines

> As an entrant, I want to see the app policy and guidelines.

- **Points:** 2
- **Risk:** Low — primarily static, read-only content.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Policy is accessible from an obvious location.
- [ ] Content is readable, scrollable, and available without a live query.
- [ ] Entrant can return to the previous screen.

### US 01.12.01 — Scan campsite QR code

> As an entrant, I want to view campsite details by scanning the promotional QR code.

- **Points:** 5
- **Risk:** High — requires camera permission, scanning, and safe deep-link validation.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Camera permission and denial are handled safely.
- [ ] Valid code opens the exact campsite details.
- [ ] Invalid, unsupported, or removed-campsite codes show an error.

### US 01.13.01 — Rate a campsite

> As an entrant, I want to give a rating for a campsite.

- **Points:** 5
- **Risk:** High — eligibility, duplicate prevention, and aggregate updates must agree.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only an eligible entrant can submit a rating in the allowed range.
- [ ] Duplicate rating behavior is prevented or explicitly treated as an update.
- [ ] After submission, the campsite screen visibly updates its average rating and rating count.

## Campsite owner

### Campsite management

### US 02.01.01 — Register a campsite

> As a campsite owner, I want to register my campsite with the app.

- **Points:** 5
- **Risk:** Medium — requires validation, ownership, and Firestore creation.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can submit all required campsite information.
- [ ] Successful submission opens the newly created campsite-details screen.
- [ ] The campsite appears under **My Campsites**; a failed save keeps the form and shows an error.

### US 02.01.02 — Provide campsite details

> As a campsite owner, I want to provide campsite details including name, location, description (amenities), maximum occupancy, map location, optional image, and tags.

- **Points:** 5
- **Risk:** Medium — combines form validation, GeoPoint data, tags, and optional storage upload.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Form captures every required field and validates occupancy/location.
- [ ] Optional image upload is handled without blocking image-free listings.
- [ ] Saved data matches the entrant and owner detail screens.

### US 02.01.03 — Update campsite details

> As a campsite owner, I want to update details about my campsite.

- **Points:** 3
- **Risk:** Medium — updates must be owner-authorized and validated.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Existing details are pre-filled and valid changes can be saved.
- [ ] Updated values appear to entrants.
- [ ] Owners cannot edit campsites they do not own.

### US 02.01.04 — View owned campsite details

> As a campsite owner, I want to view details of my campsite.

- **Points:** 2
- **Risk:** Low — primarily an owner-scoped read.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can open complete details from My Campsites.
- [ ] Availability and lottery information are accessible.
- [ ] Edit controls appear only for the owner.

### US 02.02.01 — Generate campsite QR code

> As a campsite owner, I want the system to generate a unique QR code that links to the campsite details.

- **Points:** 5
- **Risk:** High — encoding, sharing, and scanner/deep-link compatibility must agree.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Each campsite produces a stable, unique QR payload.
- [ ] Owner can view and share/save the code.
- [ ] Scanning resolves to the correct campsite and handles removal.

### US 02.03.01 — View owned campsites

> As a campsite owner, I want to view a list of my campsites.

- **Points:** 3
- **Risk:** Medium — requires owner-scoped querying and security.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Only current owner's campsites are listed.
- [ ] Every item shows useful summary information and opens details.
- [ ] Empty, loading, and error states are handled.

### Lottery management

### US 02.04.01 — Create a campsite lottery

> As a campsite owner, I want to list my campsite in a lottery for a certain period.

- **Points:** 5
- **Risk:** High — dates, ownership, lifecycle state, and visibility must be consistent.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can select an owned campsite and configure required lottery fields.
- [ ] Valid submission creates a lottery linked to that campsite and owner.
- [ ] Lottery becomes joinable only during its registration period.

### US 02.04.02 — Create a multiple-campsite lottery

> As a campsite owner, I want be able to list multiple campsites in a lottery for a certain period.

- **Points:** 5
- **Risk:** High — downstream winner booking must support multiple availability sets.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Owner can select multiple campsites they own.
- [ ] At least one campsite is required and every selection is visible on the review screen.
- [ ] Winner flow can identify every eligible campsite.

### US 02.04.03 — Set registration period

> As a campsite owner, I want to set a registration period.

- **Points:** 3
- **Risk:** Medium — timestamp and timezone boundaries affect eligibility.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] The date/time controls require the registration start to precede the end and show the chosen timezone.
- [ ] Entrants cannot join before the start or after the end.
- [ ] Times display consistently in the intended timezone.

### US 02.04.04 — Set booking period

> As a campsite owner, I want to set a booking period.

- **Points:** 3
- **Risk:** High — booking dates must agree with availability and timezone logic.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Booking start precedes booking end.
- [ ] Period is compatible with campsite availability.
- [ ] Winners cannot reserve outside the configured period.

### US 02.04.05 — Set winner count

> As a campsite owner, I want to set the number of lottery winners.

- **Points:** 2
- **Risk:** Medium — value must remain valid relative to capacity and entrants.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can enter a positive whole number.
- [ ] Value does not exceed an optional waitlist capacity.
- [ ] The lottery review and result screens display the configured winner count.

### US 02.04.06 — Update lottery listing

> As a campsite owner, I want to update my campsite lottery listing.

- **Points:** 5
- **Risk:** High — edits after entrants join or drawing begins can invalidate state.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can edit and validate a lottery they own.
- [ ] Changes appear to entrants.
- [ ] Unsafe fields are locked after registration/drawing reaches the agreed state.

### US 02.04.07 — Set optional waitlist capacity

> As a campsite owner, I want to optionally set a capacity for my lottery waitlist.

- **Points:** 3
- **Risk:** High — concurrent joins must never exceed capacity.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Blank capacity means unlimited; entered capacity is a positive whole number.
- [ ] Capacity cannot be below winner count or current entry count.
- [ ] Atomic join logic prevents the list from exceeding capacity.

### US 02.05.01 — Automatically draw winners

> As a campsite owner, I want the system to automatically draw winners after the registration period ends.

- **Points:** 8
- **Risk:** High — trusted scheduling, randomness, atomicity, and repeat prevention are unresolved.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Before registration closes, the owner sees that drawing is unavailable and why.
- [ ] After the draw, the owner sees the configured number of unique winners or all entrants if fewer joined.
- [ ] Refreshing or reopening the result screen shows the same winners and does not send duplicate results.
- [ ] Team confirms with the TA whether scheduled backend execution is required.

### Reservations and communication

### US 02.06.01 — View booked dates

> As a campsite owner, I want to view the currently booked dates for my campsite.

- **Points:** 5
- **Risk:** High — calendar must reflect live reservation/cancellation state.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Confirmed dates appear as booked for an owned campsite.
- [ ] Cancelled/rejected dates are represented according to availability policy.
- [ ] Selecting a booked date shows authorized reservation information.

### US 02.07.01 — View active reservations

> As a campsite owner, I want to view a list of active reservations.

- **Points:** 3
- **Risk:** Medium — requires owner/status/date indexes and scoped access.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only active reservations for owned campsites are listed.
- [ ] Each item shows campsite, entrant, dates, and status and opens details.
- [ ] Empty, loading, and error states are handled.

### US 02.07.02 — View past reservations

> As a campsite owner, I want to view a list of past reservations.

- **Points:** 3
- **Risk:** Medium — requires consistent past/completed classification.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only past/completed reservations for owned campsites are listed.
- [ ] Entries are ordered consistently and open details.
- [ ] Empty, loading, and error states are handled.

### US 02.07.03 — View cancelled reservations

> As a campsite owner, I want to view a list of cancelled reservations.

- **Points:** 3
- **Risk:** Medium — multiple cancellation/rejection statuses must be classified.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Cancelled and rejected reservations for owned campsites are listed.
- [ ] Cancellation type/date are visible and details can be opened.
- [ ] Empty, loading, and error states are handled.

### US 02.08.01 — Reject a reservation

> As a campsite owner, I want to reject a reservation.

- **Points:** 5
- **Risk:** High — rejection affects availability, entrant notification, and refund state.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Eligible reservation shows a **Reject Reservation** action and confirmation dialog.
- [ ] After rejection, it moves to the cancelled/rejected list and cannot be rejected again.
- [ ] Entrant sees the rejection reason, notification, and refund information when applicable.

### US 02.09.01 — View waitlist entrants

> As a campsite owner, I want to view a list of entrants who have joined the waitlist for my campsite lottery.

- **Points:** 3
- **Risk:** Medium — personally identifying data requires owner-scoped rules.
- **Halfway:** Yes

#### UI acceptance checkpoints

- [ ] Owner can view entrants and statuses for an owned lottery.
- [ ] Entrants from other lotteries are excluded.
- [ ] Unauthorized owners are denied; empty/loading/error states are handled.

### US 02.10.01 — Notify waitlist entrants

> As a campsite owner, I want to send notifications to entrants who have joined the waitlist for my campsite lottery.

- **Points:** 5
- **Risk:** High — recipient authorization, fan-out, preferences, and duplicates must be handled.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Owner can compose, preview, and confirm a notification from an owned lottery screen.
- [ ] The UI shows the intended recipient group before sending and then shows success feedback.
- [ ] Failure shows a retry option without adding duplicate entries to sent-notification history.

### US 02.10.02 — View sent notifications

> As a campsite owner, I want to view a list of all notifications I have sent.

- **Points:** 3
- **Risk:** Medium — requires sender-scoped logging and indexes.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Only notifications sent by the current owner are listed.
- [ ] Each item shows lottery, message, recipients, and time.
- [ ] Entries are ordered and can be opened for details.

### US 02.11.01 — Require rules acknowledgement

> As a campsite owner, I want entrants to confirm that they have read the campsite rules and policies before confirming their reservation.

- **Points:** 3
- **Risk:** Medium — acknowledgement must be enforced, not merely displayed.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Rules/policies are shown before final confirmation.
- [ ] Reservation cannot be confirmed until entrant explicitly acknowledges them.
- [ ] Reservation details display that the rules were accepted and when acceptance occurred.

### US 02.12.01 — Message a reserving entrant

> As a campsite owner, I want to message a user who is reserving my campsite.

- **Points:** 5
- **Risk:** High — participant authorization, delivery, content, and admin logging are required.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Owner can message only entrants with a reservation for their campsite.
- [ ] Non-empty message records sender, recipient, reservation, and timestamp.
- [ ] Entrant sees the message in their reservation conversation and admin can open it from message logs.

## Administrator

### Moderation and auditing

### US 03.01.01 — Remove campsites

> As an admin, I want to remove registered campsites.

- **Points:** 5
- **Risk:** High — removal affects listings, lotteries, reservations, and audit history.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Authorized admin can confirm removal of a selected campsite.
- [ ] Removed campsite no longer appears publicly and related state is handled.
- [ ] Non-admins are denied and the action is logged.

### US 03.02.01 — Remove profiles

> As an admin, I want to remove profiles.

- **Points:** 5
- **Risk:** High — identity removal must preserve or anonymize required history.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Authorized admin can confirm removal of a selected profile.
- [ ] Profile is removed/anonymized according to retention policy.
- [ ] Non-admins are denied and the action is logged.

### US 03.03.01 — Remove images

> As an admin, I want to remove images.

- **Points:** 5
- **Risk:** High — Cloud Storage file and Firestore reference must remain consistent.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Authorized admin can confirm removal of an uploaded image.
- [ ] After removal, the image disappears from the moderation gallery and its related screen.
- [ ] The related profile/campsite displays a placeholder and the admin sees removal confirmation.

### US 03.04.01 — Browse campsites

> As an admin, I want to browse campsites.

- **Points:** 3
- **Risk:** Medium — admin scope and potentially large result sets require care.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Admin can list and identify registered campsites.
- [ ] Selecting one opens complete moderation details.
- [ ] Non-admin access and loading/empty/error states are handled.

### US 03.05.01 — Browse profiles

> As an admin, I want to browse profiles.

- **Points:** 3
- **Risk:** Medium — exposes personal data and therefore needs strict rules.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Admin can list profiles with appropriate identity and roles.
- [ ] Selecting one opens the moderation details needed by the requirement.
- [ ] Non-admin access is denied and unnecessary private data is not exposed.

### US 03.06.01 — Browse uploaded images

> As an admin, I want to browse uploaded images so I can remove them if necessary.

- **Points:** 5
- **Risk:** High — cross-record Storage inventory and ownership metadata are required.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Admin can browse campsite/profile images with their associated records.
- [ ] Image can be previewed and sent to the removal flow.
- [ ] Missing files and non-admin access are handled safely.

### US 03.07.01 — Remove violating campsite owners

> As an admin, I want to remove campsite owners that violate app policy.

- **Points:** 8
- **Risk:** High — role removal affects campsites, lotteries, reservations, and possibly entrant access.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Admin confirms the owner and records a reason.
- [ ] Owner privileges/listings are disabled according to the agreed policy.
- [ ] Existing reservations remain consistent and the action is logged.

### US 03.08.01 — Review notification logs

> As an admin, I want to review logs of all notifications.

- **Points:** 3
- **Risk:** Medium — global logs require privileged access and retention rules.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] Admin can list notifications with sender, recipient, type, and time.
- [ ] Logs can be ordered/filtered and opened for details.
- [ ] Ordinary users cannot access global notification logs.

### US 03.11.01 — Use multiple roles

> As an admin, I should be able to be a campsite owner and/or entrant with my admin profile.

- **Points:** 5
- **Risk:** High — multi-role navigation must not weaken admin security.
- **Halfway:** No

#### UI acceptance checkpoints

- [ ] One profile can hold admin, owner, and/or entrant roles.
- [ ] User can access assigned role features without a second account.
- [ ] Active role is clear and ordinary role actions follow their normal rules.

### US 03.12.01 — Review message logs

> As an admin, I want to review logs of all messages from campsite owners to entrants who reserved their campsite.

- **Points:** 3
- **Risk:** Medium — global message content is privacy-sensitive.
- **Halfway:** No

#### UI acceptance checkpoints

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
