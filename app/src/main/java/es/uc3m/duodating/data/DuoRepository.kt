package es.uc3m.duodating.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.duodating.data.models.Duo
import kotlinx.coroutines.tasks.await
import java.util.UUID

class DuoRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val duosCollection = firestore.collection("duos")
    private val usersCollection = firestore.collection("users")

    suspend fun createDuoInvite(questionChoice: String, questionAnswer: String, photoUrl: String): Result<String> = try {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        
        val inviteCode = UUID.randomUUID().toString().substring(0, 6).uppercase()
        val duoId = duosCollection.document().id
        
        val duo = Duo(
            duoId = duoId,
            user1Id = currentUserId,
            status = "PENDING",
            inviteCode = inviteCode,
            questionChoice = questionChoice,
            questionAnswer = questionAnswer,
            photoUrl = photoUrl
        )
        
        firestore.runBatch { batch ->
            batch.set(duosCollection.document(duoId), duo)
            batch.update(usersCollection.document(currentUserId), "duoId", duoId)
        }.await()
        
        Result.success(inviteCode)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun joinDuo(inviteCode: String): Result<Unit> = try {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        
        val query = duosCollection.whereEqualTo("inviteCode", inviteCode)
            .whereEqualTo("status", "PENDING")
            .get().await()
            
        if (query.isEmpty) throw Exception("Invalid or expired invite code")
        
        val duoDoc = query.documents.first()
        val duoId = duoDoc.id
        
        firestore.runBatch { batch ->
            batch.update(duosCollection.document(duoId), mapOf(
                "user2Id" to currentUserId,
                "status" to "ACTIVE"
            ))
            batch.update(usersCollection.document(currentUserId), "duoId", duoId)
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendLike(myDuoId: String, targetDuoId: String): Result<Boolean> = try {
        val targetDuoRef = duosCollection.document(targetDuoId)
        val myDuoRef = duosCollection.document(myDuoId)

        firestore.runTransaction { transaction ->
            val targetDuo = transaction.get(targetDuoRef).toObject(Duo::class.java)
            
            transaction.update(myDuoRef, "likesSent", FieldValue.arrayUnion(targetDuoId))
            transaction.update(targetDuoRef, "likesReceived", FieldValue.arrayUnion(myDuoId))

            if (targetDuo?.likesSent?.contains(myDuoId) == true) {
                transaction.update(myDuoRef, "matches", FieldValue.arrayUnion(targetDuoId))
                transaction.update(targetDuoRef, "matches", FieldValue.arrayUnion(myDuoId))
                true 
            } else {
                false
            }
        }.await().let { Result.success(it) }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
