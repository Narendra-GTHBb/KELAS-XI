package com.gymecommerce.musclecart.data.local.dao

import androidx.room.*
import com.gymecommerce.musclecart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    // SELECT queries - use Flow instead of suspend
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: Int): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmailFlow(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    fun getUserByEmailAndPasswordFlow(email: String, password: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE isAdmin = 1")
    fun getAdminUsersFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT COUNT(*) FROM users")
    fun getUserCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE isAdmin = 1")
    fun getAdminCountFlow(): Flow<Int>
    
    // WRITE operations - suspend is OK for these
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
