package daos

import android.arch.persistence.room.*
import persist.MediaPersist

@Dao
interface DaoMediaPersist {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(item: MediaPersist): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(item: List<MediaPersist>): List<Long>

    @Query("SELECT * FROM MediaPersist WHERE id = :id")
    fun selectById(id: String): MediaPersist

    @Query("SELECT * FROM MediaPersist")
    fun selectAll(): List<MediaPersist>

    @Query("SELECT * FROM MediaPersist WHERE tags LIKE '%' || :query || '%'")
    fun selectByTag(query: String): List<MediaPersist>

    @Update
    fun update(item: MediaPersist)

    @Delete
    fun delete(item: MediaPersist)

}
