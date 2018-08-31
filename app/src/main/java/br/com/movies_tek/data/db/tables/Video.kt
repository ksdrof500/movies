package br.com.movies_tek.data.db.tables

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import io.reactivex.Flowable

@Entity(
        tableName = "video",
        foreignKeys = [ForeignKey(
                entity = MovieEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("movie_id"),
                onDelete = CASCADE,
                onUpdate = CASCADE
        )],
        indices = [Index("movie_id")]
)
data class VideoEntity(
        @ColumnInfo(name = "movie_id") val movieId: Int,
        val name: String,
        val key: String,
        val site: String,
        val size: Int,
        val type: String,
        @PrimaryKey(autoGenerate = true) val id: Long = 0
)

@Dao
interface VideoDao {

    @Query("SELECT id, movie_id, name, key, site, size, type FROM video " + "WHERE movie_id = :movieId")
    fun getByMovieId(movieId: Int): Flowable<List<VideoEntity>>

    @Insert
    fun insertAll(videos: List<VideoEntity>)

    @Query("DELETE FROM video " + "WHERE movie_id IN (SELECT id FROM movie WHERE id = :movieId)")
    fun deleteByMovieId(movieId: Int): Int
}
