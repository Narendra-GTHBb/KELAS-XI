package com.apkfood.wavesoffoodadmin.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.apkfood.wavesoffoodadmin.model.AdminUser
import kotlinx.coroutines.tasks.await
import com.google.gson.Gson

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("admin_session", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_ADMIN_DATA = "admin_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    suspend fun loginAdmin(email: String, password: String): Result<AdminUser> {
        return try {
            // First authenticate with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            
            if (user != null) {
                // Check if user exists in admin_users collection
                val adminDoc = firestore.collection("admin_users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("isActive", true)
                    .get()
                    .await()
                
                if (!adminDoc.isEmpty) {
                    val adminUser = adminDoc.documents[0].toObject(AdminUser::class.java)
                    if (adminUser != null) {
                        // Update last login and login count
                        updateLastLogin(adminDoc.documents[0].id)
                        // Return admin with document ID
                        val adminWithId = adminUser.copy(id = adminDoc.documents[0].id)
                        // Save to local storage
                        saveAdminSession(adminWithId)
                        Result.success(adminWithId)
                    } else {
                        auth.signOut()
                        Result.failure(Exception("Invalid admin data"))
                    }
                } else {
                    auth.signOut()
                    Result.failure(Exception("Admin account not found or inactive"))
                }
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            auth.signOut()
            Result.failure(Exception(getErrorMessage(e)))
        }
    }
    
    suspend fun getCurrentAdmin(): AdminUser? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Search by email since we might not have the document ID as UID
                val adminQuery = firestore.collection("admin_users")
                    .whereEqualTo("email", currentUser.email)
                    .whereEqualTo("isActive", true)
                    .get()
                    .await()
                
                if (!adminQuery.isEmpty) {
                    val adminDoc = adminQuery.documents[0]
                    val adminUser = adminDoc.toObject(AdminUser::class.java)
                    adminUser?.copy(id = adminDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun registerAdmin(adminUser: AdminUser, password: String): Result<String> {
        return try {
            // Create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(adminUser.email, password).await()
            val user = authResult.user
            
            if (user != null) {
                // Add to admin_users collection
                val adminData = adminUser.copy(id = user.uid)
                firestore.collection("admin_users")
                    .document(user.uid)
                    .set(adminData)
                    .await()
                
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateLastLogin(adminId: String) {
        try {
            val currentTime = System.currentTimeMillis()
            val updates = mapOf(
                "lastLogin" to currentTime,
                "loginCount" to com.google.firebase.firestore.FieldValue.increment(1),
                "updatedAt" to currentTime
            )
            
            firestore.collection("admin_users")
                .document(adminId)
                .update(updates)
                .await()
        } catch (e: Exception) {
            // Log error but don't fail login
            android.util.Log.e("AuthRepository", "Failed to update last login", e)
        }
    }
    
    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Admin account not found"
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Account already exists"
            is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password is too weak"
            is com.google.firebase.auth.FirebaseAuthException -> exception.message ?: "Authentication error"
            is java.net.UnknownHostException -> "No internet connection"
            is java.net.SocketTimeoutException -> "Connection timeout"
            else -> exception.message ?: "Login failed"
        }
    }
    
    fun logout() {
        auth.signOut()
        clearAdminSession()
    }
    
    fun isLoggedIn(): Boolean {
        val isFirebaseLoggedIn = auth.currentUser != null
        val isLocalSessionValid = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
        return isFirebaseLoggedIn && isLocalSessionValid
    }
    
    fun getCachedAdmin(): AdminUser? {
        return try {
            val adminJson = sharedPrefs.getString(KEY_ADMIN_DATA, null)
            if (adminJson != null) {
                gson.fromJson(adminJson, AdminUser::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun saveAdminSession(admin: AdminUser) {
        val adminJson = gson.toJson(admin)
        sharedPrefs.edit()
            .putString(KEY_ADMIN_DATA, adminJson)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    private fun clearAdminSession() {
        sharedPrefs.edit()
            .remove(KEY_ADMIN_DATA)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                // Re-authenticate first
                val credential = com.google.firebase.auth.EmailAuthProvider
                    .getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()
                
                // Update password
                user.updatePassword(newPassword).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
