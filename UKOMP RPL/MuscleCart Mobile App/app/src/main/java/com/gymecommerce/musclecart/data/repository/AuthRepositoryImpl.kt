package com.gymecommerce.musclecart.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.gymecommerce.musclecart.data.local.TokenManager
import com.gymecommerce.musclecart.data.remote.api.AuthApiService
import com.gymecommerce.musclecart.data.remote.dto.LoginRequest
import com.gymecommerce.musclecart.data.remote.dto.RegisterRequest
import com.gymecommerce.musclecart.data.remote.dto.UpdateProfileRequest
import com.gymecommerce.musclecart.data.remote.dto.UserDto
import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.model.UserRole
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
    }

    private fun userDtoToDomain(dto: UserDto): User {
        val now = System.currentTimeMillis()
        return User(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            email = dto.email ?: "",
            phone = dto.phone,
            address = dto.address,
            city = dto.city,
            postalCode = dto.postalCode,
            provinceId = dto.provinceId,
            cityId = dto.cityId,
            role = UserRole.fromString(dto.role ?: "user"),
            createdAt = now,
            updatedAt = now,
            points = dto.points
        )
    }

    override suspend fun login(email: String, password: String): NetworkResult<User> {
        return try {
            val response = authApiService.login(LoginRequest(email = email, password = password))

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val loginData = apiResponse.data
                    val user = userDtoToDomain(loginData.user)

                    // Save real Sanctum token
                    saveAuthToken(loginData.token)
                    saveUser(user)
                    tokenManager.saveUserId(user.id)
                    _currentUser.value = user

                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Login failed")
                }
            } else {
                val body = response.errorBody()?.string()
                val msg = try {
                    gson.fromJson(body, Map::class.java)["message"] as? String
                } catch (e: Exception) { null }
                NetworkResult.Error(msg ?: "Invalid email or password")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun register(name: String, email: String, password: String): NetworkResult<User> {
        return try {
            val response = authApiService.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    passwordConfirmation = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val loginData = apiResponse.data
                    val user = userDtoToDomain(loginData.user)

                    // Save real Sanctum token
                    saveAuthToken(loginData.token)
                    saveUser(user)
                    tokenManager.saveUserId(user.id)
                    _currentUser.value = user

                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Registration failed")
                }
            } else {
                val body = response.errorBody()?.string()
                val msg = try {
                    gson.fromJson(body, Map::class.java)["message"] as? String
                } catch (e: Exception) { null }
                NetworkResult.Error(msg ?: "Registration failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        return try {
            // Try to call backend logout (best-effort)
            try { authApiService.logout() } catch (e: Exception) { /* ignore */ }
            clearAuthToken()
            clearUser()
            _currentUser.value = null
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error("Logout failed: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        if (_currentUser.value != null) {
            return _currentUser.value
        }
        return try {
            val userData = dataStore.data.first()[USER_DATA_KEY]
            if (userData != null) {
                val user = gson.fromJson(userData, User::class.java)
                _currentUser.value = user
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        return _currentUser.asStateFlow()
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) return false

        // Validate token against server — if 401, the token is expired/invalid
        return try {
            val response = authApiService.getUser()
            if (response.code() == 401) {
                // Token rejected by server → clear it and force re-login
                clearAuthToken()
                clearUser()
                _currentUser.value = null
                false
            } else {
                response.isSuccessful
            }
        } catch (e: Exception) {
            // Network error (server down, no connection) → treat as still logged in
            // so user isn't kicked out due to connectivity issues
            true
        }
    }

    override suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
        tokenManager.saveToken(token)
    }

    override suspend fun getAuthToken(): String? {
        return tokenManager.getToken()
            ?: dataStore.data.first()[AUTH_TOKEN_KEY]
    }

    override suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
        tokenManager.clearToken()
    }

    override suspend fun refreshToken(): NetworkResult<String> {
        return try {
            val currentToken = getAuthToken()
            if (currentToken != null) {
                NetworkResult.Success(currentToken)
            } else {
                NetworkResult.Error("No token available")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Token refresh failed: ${e.message}", e)
        }
    }

    override suspend fun updateProfile(
        name: String,
        email: String,
        phone: String?,
        address: String?,
        city: String?,
        postalCode: String?,
        provinceId: String?,
        cityId: String?,
        password: String?
    ): NetworkResult<User> {
        return try {
            val response = authApiService.updateProfile(
                UpdateProfileRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    city = city,
                    postalCode = postalCode,
                    provinceId = provinceId,
                    cityId = cityId,
                    password = password,
                    passwordConfirmation = password
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success") {
                    // Unwrap nested user from data.user
                    val currentUser = getCurrentUser()
                    val now = System.currentTimeMillis()
                    val updatedUser = apiResponse.data?.user?.let { dto ->
                        User(
                            id = dto.id ?: currentUser?.id ?: 0,
                            name = dto.name?.takeIf { it.isNotBlank() } ?: name,
                            email = dto.email?.takeIf { it.isNotBlank() } ?: email,
                            phone = dto.phone ?: phone,
                            address = dto.address ?: address,
                            city = dto.city ?: city,
                            postalCode = dto.postalCode ?: postalCode,
                            provinceId = dto.provinceId ?: provinceId,
                            cityId = dto.cityId ?: cityId,
                            role = UserRole.fromString(dto.role ?: currentUser?.role?.toString() ?: "user"),
                            createdAt = currentUser?.createdAt ?: now,
                            updatedAt = now,
                            points = dto.points
                        )
                    } ?: User(
                        id = currentUser?.id ?: 0,
                        name = name,
                        email = email,
                        phone = phone ?: currentUser?.phone,
                        address = address ?: currentUser?.address,
                        city = city ?: currentUser?.city,
                        postalCode = postalCode ?: currentUser?.postalCode,
                        provinceId = provinceId ?: currentUser?.provinceId,
                        cityId = cityId ?: currentUser?.cityId,
                        role = currentUser?.role ?: UserRole.USER,
                        createdAt = currentUser?.createdAt ?: now,
                        updatedAt = now
                    )
                    saveUser(updatedUser)
                    _currentUser.value = updatedUser
                    NetworkResult.Success(updatedUser)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Update failed")
                }
            } else {
                val body = response.errorBody()?.string()
                val msg = try {
                    gson.fromJson(body, Map::class.java)["message"] as? String
                } catch (e: Exception) { null }
                NetworkResult.Error(msg ?: "Update failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun refreshUser(): NetworkResult<User> {
        return try {
            val response = authApiService.getUser()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data?.user != null) {
                    val user = userDtoToDomain(apiResponse.data.user)
                    saveUser(user)
                    _currentUser.value = user
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Failed to refresh user")
                }
            } else {
                NetworkResult.Error("Failed to refresh user")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun saveUser(user: User) {
        try {
            val userData = gson.toJson(user)
            dataStore.edit { preferences ->
                preferences[USER_DATA_KEY] = userData
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    override suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(USER_DATA_KEY)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): NetworkResult<User> {
        return try {
            val response = authApiService.loginWithGoogle(
                com.gymecommerce.musclecart.data.remote.dto.GoogleLoginRequest(idToken = idToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val loginData = apiResponse.data
                    val user = userDtoToDomain(loginData.user)
                    saveAuthToken(loginData.token)
                    saveUser(user)
                    tokenManager.saveUserId(user.id)
                    _currentUser.value = user
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Google login failed")
                }
            } else {
                val body = response.errorBody()?.string()
                val msg = try {
                    gson.fromJson(body, Map::class.java)["message"] as? String
                } catch (e: Exception) { null }
                NetworkResult.Error(msg ?: "Google login failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun registerFcmToken(token: String) {
        try {
            authApiService.registerFcmToken(
                com.gymecommerce.musclecart.data.remote.dto.FcmTokenRequest(fcmToken = token)
            )
        } catch (e: Exception) {
            // Silently ignore — FCM token registration is best-effort
        }
    }
}