package es.uc3m.duodating.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import es.uc3m.duodating.data.models.Duo
import es.uc3m.duodating.data.models.DuoInvite
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class DuoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val invitesCollection = firestore.collection("duo_invites")
    private val duosCollection = firestore.collection("duos")

    @OptIn(ExperimentalCoroutinesApi::class)
    fun listenToUserStatus(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flatMapLatest { uid ->
        if (uid == null) {
            flowOf(null)
        } else {
            usersCollection.document(uid).snapshots().map { snapshot ->
                snapshot.toObject(User::class.java)?.copy(uid = snapshot.id)
            }
        }
    }

    fun listenToUser(userId: String): Flow<User?> {
        return usersCollection.document(userId).snapshots().map { snapshot ->
            snapshot.toObject(User::class.java)?.copy(uid = snapshot.id)
        }
    }

    fun listenToDuo(duoId: String): Flow<Duo?> {
        return duosCollection.document(duoId).snapshots().map { it.toObject(Duo::class.java) }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject(User::class.java)?.copy(uid = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllActiveDuos(): Flow<List<Duo>> {
        return duosCollection
            .whereEqualTo("status", "ACTIVE")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Duo::class.java) }
            }
    }

    fun listenToIncomingInvites(phone: String): Flow<DuoInvite?> {
        Log.d("DuoRepository", "Listening for invites for phone: $phone")
        
        return invitesCollection
            .whereEqualTo("receiverPhone", phone.trim())
            .whereEqualTo("status", "pending")
            .snapshots()
            .map { snapshot ->
                val invite = snapshot.documents.firstOrNull()?.toObject(DuoInvite::class.java)
                Log.d("DuoRepository", "Found invite: ${invite?.inviteId}")
                invite
            }
    }

    suspend fun sendInvite(targetPhone: String): Result<Unit> = try {
        val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        val senderDoc = usersCollection.document(uid).get().await()
        val sender = senderDoc.toObject(User::class.java)
        val senderName = "${sender?.firstName} ${sender?.lastName}"

        val query = usersCollection.whereEqualTo("phoneNumber", targetPhone.trim()).get().await()
        if (query.isEmpty) {
            throw Exception("User with this phone number does not exist yet.")
        }
        val targetUserDoc = query.documents.first()
        val targetUid = targetUserDoc.id

        if (targetUid == uid) {
            throw Exception("You cannot invite yourself.")
        }

        val inviteId = invitesCollection.document().id
        val invite = DuoInvite(
            inviteId = inviteId,
            senderUid = uid,
            senderName = senderName, 
            receiverPhone = targetPhone.trim(),
            status = "pending"
        )

        firestore.runBatch { batch ->
            batch.set(invitesCollection.document(inviteId), invite)
            batch.update(usersCollection.document(uid), "status", "WAITING")
            batch.update(usersCollection.document(targetUid), "status", "RECEIVED")
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun cancelInvite(): Result<Unit> = try {
        val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        val query = invitesCollection
            .whereEqualTo("senderUid", uid)
            .whereEqualTo("status", "pending")
            .get().await()
            
        if (query.isEmpty) throw Exception("No pending invite found to cancel.")
        
        val inviteDoc = query.documents.first()
        val invite = inviteDoc.toObject(DuoInvite::class.java)
        val targetPhone = invite?.receiverPhone

        firestore.runBatch { batch ->
            batch.delete(inviteDoc.reference)
            batch.update(usersCollection.document(uid), "status", "READY_TO_LINK")
        }.await()
        
        if (targetPhone != null) {
            val receiverQuery = usersCollection.whereEqualTo("phoneNumber", targetPhone).get().await()
            if (!receiverQuery.isEmpty) {
                val receiverUid = receiverQuery.documents.first().id
                usersCollection.document(receiverUid).update("status", "READY_TO_LINK").await()
            }
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun acceptInvite(invite: DuoInvite): Result<Unit> = try {
        val myUid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        val duoId = duosCollection.document().id
        
        firestore.runTransaction { transaction ->
            val duo = mapOf(
                "duoId" to duoId,
                "userIds" to listOf(invite.senderUid, myUid),
                "user1Id" to invite.senderUid,
                "user2Id" to myUid,
                "status" to "ONBOARDING",
                "createdAt" to FieldValue.serverTimestamp(),
                "matches" to emptyList<String>(),
                "likesSent" to emptyList<String>(),
                "likesReceived" to emptyList<String>(),
            )
            transaction.set(duosCollection.document(duoId), duo)

            transaction.update(usersCollection.document(invite.senderUid), mapOf(
                "status" to "DUO_ONBOARDING",
                "linkedDuoId" to duoId
            ))
            transaction.update(usersCollection.document(myUid), mapOf(
                "status" to "DUO_ONBOARDING",
                "linkedDuoId" to duoId
            ))

            transaction.update(invitesCollection.document(invite.inviteId), "status", "accepted")
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun declineInvite(inviteId: String, senderUid: String): Result<Unit> = try {
        val myUid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        firestore.runBatch { batch ->
            batch.update(invitesCollection.document(inviteId), "status", "declined")
            batch.update(usersCollection.document(senderUid), "status", "READY_TO_LINK")
            batch.update(usersCollection.document(myUid), "status", "READY_TO_LINK")
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveDuoProfile(
        duoId: String,
        questionChoice: String,
        questionAnswer: String,
        imageUri: Uri?
    ): Result<Unit> = try {
        val duoUpdates = mutableMapOf<String, Any>(
            "questionChoice" to questionChoice,
            "questionAnswer" to questionAnswer,
            "status" to "ACTIVE"
        )

        if (imageUri != null) {
            val storageRef = storage.reference.child("duo_images/$duoId.jpg")
            storageRef.putFile(imageUri).await()
            val photoUrl = storageRef.downloadUrl.await().toString()
            duoUpdates["photoUrl"] = photoUrl
        }

        firestore.runTransaction { transaction ->
            val duoRef = duosCollection.document(duoId)
            val duoSnapshot = transaction.get(duoRef)
            val userIds = (duoSnapshot.get("userIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

            transaction.update(duoRef, duoUpdates)

            for (userId in userIds) {
                transaction.update(usersCollection.document(userId), "status", "LINKED")
            }
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendLike(targetDuoId: String): Result<Unit> = try {
        val myUid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        val userDoc = usersCollection.document(myUid).get().await()
        val myDuoId = userDoc.getString("linkedDuoId") ?: throw Exception("User not in a duo")

        firestore.runTransaction { transaction ->
            val targetDuoRef = duosCollection.document(targetDuoId)
            val myDuoRef = duosCollection.document(myDuoId)

            // --- STEP 1: READS (Must come first) ---
            val targetDuoSnapshot = transaction.get(targetDuoRef)
            val targetLikesSent = targetDuoSnapshot.get("likesSent") as? List<*>

            // --- STEP 2: WRITES (Must come last) ---
            transaction.update(myDuoRef, "likesSent", FieldValue.arrayUnion(targetDuoId))
            transaction.update(targetDuoRef, "likesReceived", FieldValue.arrayUnion(myDuoId))

            // Check for match logic
            if (targetLikesSent?.contains(myDuoId) == true) {
                // It's a match!
                transaction.update(myDuoRef, "matches", FieldValue.arrayUnion(targetDuoId))
                transaction.update(targetDuoRef, "matches", FieldValue.arrayUnion(myDuoId))
            }
        }.await()

        Log.d("DuoRepository", "Like sent successfully from $myDuoId to $targetDuoId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("DuoRepository", "Like failed: ${e.message}")
        Result.failure(e)
    }
}
