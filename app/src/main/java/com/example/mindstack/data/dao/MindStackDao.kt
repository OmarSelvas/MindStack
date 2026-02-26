package com.example.mindstack.data.dao

import androidx.room.*
import com.example.mindstack.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MindStackDao {
    // USUARIOS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<User?>

    // CHECK-INS DIARIOS
    @Insert
    suspend fun insertDailyCheckin(checkin: DailyCheckin)

    @Query("SELECT * FROM daily_checkin WHERE id_user = :userId ORDER BY date_time DESC")
    fun getCheckinsByUser(userId: Int): Flow<List<DailyCheckin>>

    // ROLES, MOODS Y STATUS
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRol(rol: Rol)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMood(mood: MoodEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatus(status: Status)

    // JUEGOS Y SESIONES
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJuego(juego: Juego)

    @Insert
    suspend fun insertGameSession(session: GameSession)

    @Query("SELECT * FROM game_session WHERE id_user = :userId ORDER BY date_time DESC")
    fun getGameSessionsByUser(userId: Int): Flow<List<GameSession>>

    // MENSAJES
    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM messages WHERE id_user = :userId ORDER BY date_time ASC")
    fun getMessagesByUser(userId: Int): Flow<List<Message>>
}