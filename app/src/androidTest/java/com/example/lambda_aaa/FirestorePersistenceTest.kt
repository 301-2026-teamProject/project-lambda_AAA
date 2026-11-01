package com.example.lambda_aaa

import androidx.test.platform.app.InstrumentationRegistry
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Opt-in live test: creates only a unique test document and removes it in finally. */
class FirestorePersistenceTest {
    @Test fun campsiteCreateFetchUpdateDeleteRoundTrip() = runBlocking {
        assumeTrue("Live Firestore writes require -e firestoreIntegration true",
            InstrumentationRegistry.getArguments().getString("firestoreIntegration") == "true")
        val id = "integration-test-${UUID.randomUUID()}"
        val repository = CampsiteRepository()
        val document = Firebase.firestore.collection("campsites").document(id)
        val campsite = Campsite(
            cid = id,
            ownerId = id,
            name = "Integration test — temporary campsite",
            address = "Test location",
            maxOccupancy = 4,
            description = "Temporary record created by FirestorePersistenceTest; safe to remove.",
            tags = listOf("Integration test"),
        )
        try {
            withTimeout(30_000) { write { repository.addCampsite(campsite, it) } }
            val created = withTimeout(30_000) { document.get(Source.SERVER).await() }
            assertFalse("Read must come from Firestore, not disk cache", created.metadata.isFromCache)
            assertFalse("Write must be acknowledged by the server", created.metadata.hasPendingWrites())
            assertTrue(created.exists())
            assertEquals(campsite.name, created.getString("name"))
            assertEquals(4L, created.getLong("maxOccupancy"))
            assertNotNull("Server creation timestamp should be set", created.getTimestamp("creationDate"))

            val fetched = withTimeout(30_000) { read(repository, id) }
            assertEquals(id, fetched.cid)
            assertEquals(campsite.name, fetched.name)
            assertEquals(campsite.tags, fetched.tags)
            fetched.name = "Integration test — updated campsite"
            fetched.maxOccupancy = 8
            withTimeout(30_000) { write { repository.updateCampsite(fetched, it) } }

            val updated = withTimeout(30_000) { document.get(Source.SERVER).await() }
            assertFalse(updated.metadata.isFromCache)
            assertFalse(updated.metadata.hasPendingWrites())
            assertEquals(fetched.name, updated.getString("name"))
            assertEquals(8L, updated.getLong("maxOccupancy"))
            assertEquals(created.getTimestamp("creationDate"), updated.getTimestamp("creationDate"))
            assertNotNull(updated.getTimestamp("lastUpdatedDate"))

            val reloaded = withTimeout(30_000) { read(repository, id) }
            assertEquals(fetched.name, reloaded.name)
            assertEquals(8, reloaded.maxOccupancy)

            withTimeout(30_000) { write { repository.deleteCampsite(reloaded, it) } }
            val deleted = withTimeout(30_000) { document.get(Source.SERVER).await() }
            assertFalse(deleted.metadata.isFromCache)
            assertFalse("Temporary record must be removed", deleted.exists())
        } finally {
            repository.close()
            withContext(NonCancellable) {
                // A failed assertion must not leave test data behind.
                withTimeout(30_000) { document.delete().await() }
            }
        }
    }

    private suspend fun write(operation: ((Boolean, String?) -> Unit) -> Unit): Unit =
        suspendCancellableCoroutine { continuation ->
            operation { success, message ->
                if (continuation.isActive) {
                    if (success) continuation.resume(Unit)
                    else continuation.resumeWithException(AssertionError(message ?: "Firestore write failed"))
                }
            }
        }

    private suspend fun read(repository: CampsiteRepository, id: String): Campsite =
        suspendCancellableCoroutine { continuation ->
            repository.getCampsite(id) { success, campsite, message ->
                if (continuation.isActive) {
                    if (success && campsite != null) continuation.resume(campsite)
                    else continuation.resumeWithException(AssertionError(message ?: "Campsite was not returned"))
                }
            }
        }
}
