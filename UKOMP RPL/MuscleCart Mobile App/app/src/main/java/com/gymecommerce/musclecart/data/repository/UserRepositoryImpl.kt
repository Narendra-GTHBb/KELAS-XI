package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.local.dao.UserDao
import com.gymecommerce.musclecart.data.local.entity.UserEntity
import com.gymecommerce.musclecart.data.mapper.toDomain
import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun authenticateUser(
        email: String,
        password: String
    ): NetworkResult<User> {
        return try {
            val userEntity = userDao.getUserByEmailAndPasswordFlow(email, password).first()
            if (userEntity != null) {
                NetworkResult.Success(userEntity.toDomain())
            } else {
                NetworkResult.Error("Invalid email or password")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Authentication failed: ${e.message}", e)
        }
    }

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): NetworkResult<User> {
        return try {
            val existingUser = userDao.getUserByEmailFlow(email).first()
            if (existingUser != null) {
                return NetworkResult.Error("User with this email already exists")
            }

            val userEntity = UserEntity(
                name = name,
                email = email,
                password = password, // FIXED
                phone = null,
                address = null,
                isAdmin = false
            )

            userDao.insertUser(userEntity)
            val createdUser = userDao.getUserByEmailFlow(email).first()

            if (createdUser != null) {
                NetworkResult.Success(createdUser.toDomain())
            } else {
                NetworkResult.Error("Failed to create user")
            }

        } catch (e: Exception) {
            NetworkResult.Error("Registration failed: ${e.message}", e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val result = authenticateUser(email, password)) {
            is NetworkResult.Success -> Result.Success(result.data)
            is NetworkResult.Error -> Result.Error(Exception(result.message), result.message)
            is NetworkResult.Loading -> Result.Loading
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        return when (val result = registerUser(name, email, password)) {
            is NetworkResult.Success -> Result.Success(result.data)
            is NetworkResult.Error -> Result.Error(Exception(result.message), result.message)
            is NetworkResult.Loading -> Result.Loading
        }
    }

    override suspend fun logout(): Result<Unit> = Result.Success(Unit)

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get current user")
        }
    }

    override fun getCurrentUserFlow(): Flow<User?> = flowOf(null)

    override suspend fun getUserById(userId: Int): Result<User?> {
        return try {
            Result.Success(userDao.getUserByIdFlow(userId).first()?.toDomain())
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get user")
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            Result.Success(userDao.getUserByEmailFlow(email).first()?.toDomain())
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get user")
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val existingEntity = userDao.getUserByIdFlow(user.id).first()
            if (existingEntity != null) {
                val updatedEntity = existingEntity.copy(
                    name = user.name,
                    email = user.email,
                    password = existingEntity.password, // keep old password
                    isAdmin = user.isAdmin(),
                    updatedAt = System.currentTimeMillis()
                )
                userDao.updateUser(updatedEntity)
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"), "User not found")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to update user")
        }
    }

    override suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            userDao.deleteUserById(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to delete user")
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val list = userDao.getAllUsersFlow().first().map { it.toDomain() }
            Result.Success(list)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get users")
        }
    }

    override fun getAllUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsersFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getUserCount(): Result<Int> {
        return try {
            Result.Success(userDao.getUserCountFlow().first())
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get user count")
        }
    }

    override suspend fun getAdminCount(): Result<Int> {
        return try {
            Result.Success(userDao.getAdminCountFlow().first())
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get admin count")
        }
    }

    override suspend fun isLoggedIn(): Boolean = false

    override suspend fun saveUserSession(user: User): Result<Unit> =
        Result.Success(Unit)

    override suspend fun clearUserSession(): Result<Unit> =
        Result.Success(Unit)
}