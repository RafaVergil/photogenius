package persist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.io.Serializable

/*
    We will use this class to persist Media data. As there will be no complex relations between
    this data and other database entities, we'll simply store it as JSON.toString().
    It prevents creating lots of entities to handle the complex objects. We can easily
    serialize/deserialize this data using GSON.
 */
@Entity(indices = [Index("id")])
data class MediaPersist(
        @NonNull
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        val id: String,
        val data: String,
        val tags: String) : Serializable

