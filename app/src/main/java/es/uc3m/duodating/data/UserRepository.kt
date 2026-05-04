package es.uc3m.duodating.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    suspend fun saveUserProfile(
        firstName: String,
        lastName: String,
        dob: String,
        phoneNumber: String,
        questionChoice: String,
        questionAnswer: String,
        imageUri: Uri?,
        fcmToken: String? = null
    ): Result<Unit> = try {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated.")
        
        val userUpdates = mutableMapOf<String, Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "dob" to dob,
            "phoneNumber" to phoneNumber,
            "questionChoice" to questionChoice,
            "questionAnswer" to questionAnswer,
            "status" to "READY_TO_LINK"
        )

        fcmToken?.let {
            userUpdates["fcmToken"] = it
        }

        if (imageUri != null) {
            Log.d("UserRepository", "Uploading image: $imageUri")
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(imageUri).await()
            val photoUrl = storageRef.downloadUrl.await().toString()
            userUpdates["photoUrl"] = photoUrl
        }

        usersCollection.document(uid).update(userUpdates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("UserRepository", "Error saving profile", e)
        Result.failure(e)
    }

    suspend fun getUserProfile(): Result<User?> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User not authenticated")
        val snapshot = usersCollection.document(uid).get().await()
        Result.success(snapshot.toObject(User::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateFcmToken(token: String): Result<Unit> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not authenticated")

        usersCollection.document(uid)
            .update("fcmToken", token)
            .await()

        Log.d("UserRepository", "FCM Token updated successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("UserRepository", "Error updating FCM token", e)
        Result.failure(e)
    }
}
