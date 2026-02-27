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
        
        // Insertamos estados por defecto
        mindStackDao.insertStatus(Status(id = 1, color = "#4CAF50", description = "Estable"))
        mindStackDao.insertStatus(Status(id = 2, color = "#FFC107", description = "Alerta"))
        mindStackDao.insertStatus(Status(id = 3, color = "#F44336", description = "Crítico"))

        // Insertamos estados de ánimo por defecto
        mindStackDao.insertMood(MoodEntity(id = 1, mood = "Exhausto"))
        mindStackDao.insertMood(MoodEntity(id = 2, mood = "Triste"))
        mindStackDao.insertMood(MoodEntity(id = 3, mood = "Neutral"))
        mindStackDao.insertMood(MoodEntity(id = 4, mood = "Feliz"))
        mindStackDao.insertMood(MoodEntity(id = 5, mood = "Excelente"))
        
        // Insertamos los juegos por defecto si no existen
        mindStackDao.insertJuego(Juego(id = 1, nombre = "Memorama", descripcion = "Entrena tu memoria visual"))
        mindStackDao.insertJuego(Juego(id = 2, nombre = "Memoria de Trabajo", descripcion = "Secuencia de luces (3x3)"))
    }

    // Usuarios
    suspend fun insertUser(user: User) {
        checkAndInsertBasicData()
        mindStackDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): User? = mindStackDao.getUserByEmail(email)
    fun getUserById(id: Int): Flow<User?> = mindStackDao.getUserById(id)

    // Check-ins
    suspend fun insertDailyCheckin(checkin: DailyCheckin) {
        checkAndInsertBasicData()
        mindStackDao.insertDailyCheckin(checkin)
    }
    fun getCheckinsByUser(userId: Int): Flow<List<DailyCheckin>> = mindStackDao.getCheckinsByUser(userId)

    // Catálogos
    suspend fun insertRol(rol: Rol) = mindStackDao.insertRol(rol)
    suspend fun insertMood(mood: MoodEntity) = mindStackDao.insertMood(mood)
    suspend fun insertStatus(status: Status) = mindStackDao.insertStatus(status)

    // Juegos
    suspend fun insertJuego(juego: Juego) = mindStackDao.insertJuego(juego)
    suspend fun insertGameSession(session: GameSession) {
        checkAndInsertBasicData()
        mindStackDao.insertGameSession(session)
    }
    fun getGameSessionsByUser(userId: Int): Flow<List<GameSession>> = mindStackDao.getGameSessionsByUser(userId)

    // Mensajes
    suspend fun insertMessage(message: Message) = mindStackDao.insertMessage(message)
    fun getMessagesByUser(userId: Int): Flow<List<Message>> = mindStackDao.getMessagesByUser(userId)
}
