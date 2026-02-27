package com.example.mindstack.data.repository

import com.example.mindstack.data.dao.MindStackDao
import com.example.mindstack.data.entities.*
import kotlinx.coroutines.flow.Flow

class MindStackRepository(private val mindStackDao: MindStackDao) {

    // Función crítica para evitar el error de Foreign Key
    suspend fun checkAndInsertBasicData() {
        // Insertamos el rol por defecto (id=1) para que el registro no falle
        mindStackDao.insertRol(Rol(id = 1, rol = "Usuario"))
        mindStackDao.insertRol(Rol(id = 2, rol = "Administrador"))
        mindStackDao.insertStatus(Status(id = 1, color = "#4CAF50", description = "Estable"))
        mindStackDao.insertMood(MoodEntity(id = 1, mood = "Neutral"))
    }

    // Usuarios
    suspend fun insertUser(user: User) {
        checkAndInsertBasicData() // Nos aseguramos que el Rol exista antes de insertar al usuario
        mindStackDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): User? = mindStackDao.getUserByEmail(email)
    fun getUserById(id: Int): Flow<User?> = mindStackDao.getUserById(id)

    // Check-ins
    suspend fun insertDailyCheckin(checkin: DailyCheckin) = mindStackDao.insertDailyCheckin(checkin)
    fun getCheckinsByUser(userId: Int): Flow<List<DailyCheckin>> = mindStackDao.getCheckinsByUser(userId)

    // Catálogos
    suspend fun insertRol(rol: Rol) = mindStackDao.insertRol(rol)
    suspend fun insertMood(mood: MoodEntity) = mindStackDao.insertMood(mood)
    suspend fun insertStatus(status: Status) = mindStackDao.insertStatus(status)

    // Juegos
    suspend fun insertJuego(juego: Juego) = mindStackDao.insertJuego(juego)
    suspend fun insertGameSession(session: GameSession) = mindStackDao.insertGameSession(session)
    fun getGameSessionsByUser(userId: Int): Flow<List<GameSession>> = mindStackDao.getGameSessionsByUser(userId)

    // Mensajes
    suspend fun insertMessage(message: Message) = mindStackDao.insertMessage(message)
    fun getMessagesByUser(userId: Int): Flow<List<Message>> = mindStackDao.getMessagesByUser(userId)
}
