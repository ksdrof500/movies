package br.com.movies_tek.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import br.com.movies_tek.data.db.tables.*

@Database(
        entities = [MovieEntity::class, VideoEntity::class, ReviewEntity::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun reviewDao(): ReviewDao
    abstract fun videoDao(): VideoDao
}


