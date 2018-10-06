package abstractions

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import daos.DaoMediaPersist
import persist.MediaPersist
import utils.CONSTANTS


@Database(entities = [MediaPersist::class], version = 1, exportSchema = false)
abstract class DatabaseConnection : RoomDatabase() {

    abstract fun daoMediaModel(): DaoMediaPersist

    companion object {

        private var instance: DatabaseConnection? = null

        @Synchronized
        fun getInstance(context: Context): DatabaseConnection {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            return instance!!
        }

        private fun buildDatabase(context: Context): DatabaseConnection {
            return Room.databaseBuilder(context,
                    DatabaseConnection::class.java, CONSTANTS.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }

}
