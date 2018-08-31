package br.com.movies_tek.data.db.tables

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import java.util.*

@Entity(tableName = "movie")
data class MovieEntity(
        @PrimaryKey val id: Int,
        val title: String,
        @ColumnInfo(name = "release_date") val releaseDate: Date,
        @ColumnInfo(name = "vote_average") val voteAverage: Double,
        val overview: String,
        val poster: String,
        val backdrop: String
)

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie")
    fun getAll(): Flowable<List<MovieEntity>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getById(id: Int): Flowable<MovieEntity>

    @Query("SELECT EXISTS(SELECT id FROM movie WHERE id = :id)")
    fun existsById(id: Int): Flowable<Int>

    @Insert(onConflict = REPLACE)
    fun insert(movie: MovieEntity): Long

    @Update
    fun update(movie: MovieEntity)

    @Query("DELETE FROM movie WHERE id = :id")
    fun deleteById(id: Int): Int
}
