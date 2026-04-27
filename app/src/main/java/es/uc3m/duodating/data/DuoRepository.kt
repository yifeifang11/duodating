package es.uc3m.duodating.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import es.uc3m.duodating.data.models.Duo
import es.uc3m.duodating.data.models.DuoInvite
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DuoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val invitesCollection = firestore.collection("duo_invites")
    private val duosCollection = firestore.collection("duos")

    fun listenToUserStatus(): Flow<User?> {
        val uid = auth.currentUser?.uid ?: return kotlinx.coroutines.flow.flowOf(null)
        return usersCollection.document(uid).snapshots().map { it.toObject(User::class.java) }
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
            // 1. Create Duo with requested fields
            val duo = mapOf(
                "duoId" to duoId,
                "userIds" to listOf(invite.senderUid, myUid),
                "status" to "ACTIVE",
                "createdAt" to FieldValue.serverTimestamp(),
                "matches" to emptyList<String>()
            )
            transaction.set(duosCollection.document(duoId), duo)

            // 2. Update both users
            transaction.update(usersCollection.document(invite.senderUid), mapOf(
                "status" to "LINKED",
                "linkedDuoId" to duoId
            ))
            transaction.update(usersCollection.document(myUid), mapOf(
                "status" to "LINKED",
                "linkedDuoId" to duoId
            ))

            // 3. Mark Invite as accepted
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
}
