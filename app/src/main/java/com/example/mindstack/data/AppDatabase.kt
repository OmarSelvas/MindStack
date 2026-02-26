package com.example.mindstack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mindstack.data.dao.MindStackDao
import com.example.mindstack.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        DailyCheckin::class,
        Rol::class,
        MoodEntity::class,
        Status::class,
        Juego::class,
        GameSession::class,
        Message::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mindStackDao(): MindStackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindstack_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insertar datos iniciales en un hilo secundario
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = database.mindStackDao()
                                // Roles
                                dao.insertRol(Rol(id = 1, rol = "Usuario"))
                                dao.insertRol(Rol(id = 2, rol = "Administrador"))
                                
                                // Moods (Para el check-in diario)
                                dao.insertMood(MoodEntity(id = 1, mood = "Feliz"))
                                dao.insertMood(MoodEntity(id = 2, mood = "Triste"))
                                dao.insertMood(MoodEntity(id = 3, mood = "Enojado"))
                                dao.insertMood(MoodEntity(id = 4, mood = "Ansioso"))
                                
                                // Status (Para el estado general)
                                dao.insertStatus(Status(id = 1, color = "#4CAF50", description = "Estable"))
                                dao.insertStatus(Status(id = 2, color = "#F44336", description = "Crítico"))
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
