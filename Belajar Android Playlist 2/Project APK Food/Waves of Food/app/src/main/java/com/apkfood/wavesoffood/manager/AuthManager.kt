package com.apkfood.wavesoffood.manager

import com.apkfood.wavesoffood.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

/**
 * AuthManager - Mengelola autentikasi pengguna
 * Handle login, register, dan session management
 */
class AuthManager {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        // Default admin credentials
        private const val ADMIN_EMAIL = "admin@wavesoffood.com"
        private const val ADMIN_PASSWORD = "admin123"
    }
    
    /**
     * Login dengan email dan password
     */
    fun loginWithEmail(
        email: String, 
        password: String, 
        onSuccess: (User) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Check admin credentials first
        if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            val adminUser = User(
                uid = "admin_001",
                name = "Administrator", 
                email = ADMIN_EMAIL,
                phone = "+628123456789",
                address = "Admin Office",
                isAdmin = true
            )
            onSuccess(adminUser)
            return
        }
        
        // Try Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                getUserFromFirestore(uid, onSuccess, onFailure)
            }
            .addOnFailureListener { exception ->
                // If Firebase Auth fails, provide helpful message
                val errorMessage = when {
                    exception.message?.contains("password") == true -> "Password salah"
                    exception.message?.contains("user") == true -> "User tidak ditemukan. Silakan daftar akun baru."
                    exception.message?.contains("email") == true -> "Format email tidak valid"
                    exception.message?.contains("network") == true -> "Tidak ada koneksi internet"
                    else -> "Login gagal: ${exception.message}"
                }
                onFailure(errorMessage)
            }
    }
    
    /**
     * Get user data dari Firestore
     */
    private fun getUserFromFirestore(
        uid: String,
        onSuccess: (User) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val user = document.toObject(User::class.java)?.copy(uid = uid)
                        if (user != null) {
                            onSuccess(user)
                        } else {
                            // Create basic user profile if not found
                            val basicUser = User(
                                uid = uid,
                                name = auth.currentUser?.displayName ?: "User",
                                email = auth.currentUser?.email ?: "",
                                phone = auth.currentUser?.phoneNumber ?: "",
                                address = "",
                                isAdmin = false
                            )
                            // Save to Firestore
                            firestore.collection("users").document(uid).set(basicUser)
                            onSuccess(basicUser)
                        }
                    } catch (e: Exception) {
                        onFailure("Error parsing user data: ${e.message}")
                    }
                } else {
                    // Create new user profile
                    val newUser = User(
                        uid = uid,
                        name = auth.currentUser?.displayName ?: "User",
                        email = auth.currentUser?.email ?: "",
                        phone = auth.currentUser?.phoneNumber ?: "",
                        address = "",
                        isAdmin = false
                    )
                    // Save to Firestore
                    firestore.collection("users").document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            onSuccess(newUser)
                        }
                        .addOnFailureListener { exception ->
                            onFailure("Error creating user profile: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error connecting to database: ${exception.message}")
            }
    }
    
    /**
     * Register user baru
     */
    fun registerWithEmail(
        name: String,
        email: String, 
        password: String,
        phone: String,
        onSuccess: (User) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val newUser = User(
                    uid = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    address = "",
                    isAdmin = false
                )
                
                // Save to Firestore
                firestore.collection("users").document(uid)
                    .set(newUser)
                    .addOnSuccessListener {
                        onSuccess(newUser)
                    }
                    .addOnFailureListener { exception ->
                        onFailure("Error saving user data: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                onFailure("Error creating account: ${exception.message}")
            }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        auth.signOut()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Get current user
     */
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                phone = firebaseUser.phoneNumber ?: ""
            )
        } else null
    }
    
    /**
     * Hash password untuk keamanan (optional untuk implementasi future)
     */
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
