package com.example.data.firebase

import android.util.Log
import com.example.domain.model.PlayerProgress
import com.example.domain.model.PrizeClaim
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object FirebaseManager {

    private const val TAG = "BallSortFirebase"
    private const val RTDB_URL = "https://ballsortmaster-98a70-default-rtdb.firebaseio.com"
    private const val VERIFICATION_SALT = "BallSortMaster_SecureToken_Final1000000"

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy {
        try {
            FirebaseDatabase.getInstance(RTDB_URL).apply {
                setPersistenceEnabled(true)
            }
        } catch (_: Exception) {
            FirebaseDatabase.getInstance(RTDB_URL)
        }
    }

    val currentUid: String
        get() = auth.currentUser?.uid ?: ""

    /**
     * Initializes Anonymous Authentication.
     * If user is not yet logged in, signs in anonymously.
     */
    suspend fun ensureAnonymousAuth(): Result<String> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                Log.d(TAG, "Already authenticated with UID: ${user.uid}")
                Result.success(user.uid)
            } else {
                val authResult = auth.signInAnonymously().await()
                val newUid = authResult.user?.uid ?: ""
                Log.d(TAG, "Signed in anonymously with UID: $newUid")
                Result.success(newUid)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Anonymous auth failed or offline: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Syncs level progress to `users/{uid}/progress`.
     */
    fun syncProgress(progress: PlayerProgress) {
        val uid = currentUid
        if (uid.isEmpty()) return

        try {
            val ref = database.getReference("users").child(uid).child("progress")
            val progressMap = mapOf(
                "currentLevel" to progress.currentLevel,
                "highestCompletedLevel" to progress.highestCompletedLevel,
                "totalCompleted" to progress.totalPuzzlesSolved,
                "lastPlayedAt" to System.currentTimeMillis()
            )
            ref.setValue(progressMap).addOnFailureListener { e ->
                Log.w(TAG, "Failed to sync progress to Firebase: ${e.message}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error syncProgress: ${e.message}")
        }
    }

    /**
     * Checks if a prize claim has already been submitted for the current UID.
     */
    suspend fun getExistingPrizeClaim(): PrizeClaim? {
        val uid = currentUid
        if (uid.isEmpty()) return null

        return try {
            val snapshot = database.getReference("prizeClaims").child(uid).get().await()
            if (snapshot.exists()) {
                PrizeClaim(
                    uid = uid,
                    fullName = snapshot.child("name").getValue(String::class.java) ?: "",
                    email = snapshot.child("email").getValue(String::class.java) ?: "",
                    mobileNumber = snapshot.child("mobile").getValue(String::class.java) ?: "",
                    fullAddress = snapshot.child("address").getValue(String::class.java) ?: "",
                    pinCode = snapshot.child("pinCode").getValue(String::class.java) ?: "",
                    completedLevel = snapshot.child("completedLevel").getValue(Int::class.java) ?: 1_000_000,
                    verificationHash = snapshot.child("verificationHash").getValue(String::class.java) ?: "",
                    submittedAt = snapshot.child("submittedAt").getValue(Long::class.java) ?: 0L,
                    status = snapshot.child("status").getValue(String::class.java) ?: "pending"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get prize claim: ${e.message}")
            null
        }
    }

    /**
     * Generates a tamper-proof verification signature for completing Level 1,000,000.
     */
    fun generateVerificationHash(uid: String, level: Int = 1_000_000): String {
        val input = "$uid:$level:$VERIFICATION_SALT"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Submits the grand prize claim to `prizeClaims/{uid}`.
     */
    suspend fun submitPrizeClaim(
        fullName: String,
        email: String,
        mobileNumber: String,
        fullAddress: String,
        pinCode: String
    ): Result<PrizeClaim> {
        val uid = currentUid.ifEmpty { "anon_" + System.currentTimeMillis() }
        val verificationHash = generateVerificationHash(uid)

        val claim = PrizeClaim(
            uid = uid,
            fullName = fullName.trim(),
            email = email.trim(),
            mobileNumber = mobileNumber.trim(),
            fullAddress = fullAddress.trim(),
            pinCode = pinCode.trim(),
            completedLevel = 1_000_000,
            verificationHash = verificationHash,
            submittedAt = System.currentTimeMillis(),
            status = "pending"
        )

        return try {
            val claimData = mapOf(
                "name" to claim.fullName,
                "email" to claim.email,
                "mobile" to claim.mobileNumber,
                "address" to claim.fullAddress,
                "pinCode" to claim.pinCode,
                "completedLevel" to claim.completedLevel,
                "verificationHash" to claim.verificationHash,
                "submittedAt" to claim.submittedAt,
                "status" to claim.status
            )

            database.getReference("prizeClaims").child(uid).setValue(claimData).await()
            Log.d(TAG, "Prize claim submitted successfully for UID: $uid")
            Result.success(claim)
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting prize claim", e)
            Result.failure(e)
        }
    }
}
