package br.com.movies_tek.data.db.tables

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import io.reactivex.Flowable

@Entity(
        tableName = "review",
        foreignKeys = [ForeignKey(
                entity = MovieEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("movie_id"),
                onDelete = CASCADE,
                onUpdate = CASCADE
        )],
        indices = [Index("movie_id")]
)
data class ReviewEntity(
        @ColumnInfo(name = "movie_id") val movieId: Int,
        val author: String,
        val content: String,
        val url: String,
        @PrimaryKey(autoGenerate = true) val id: Long = 0
)

@Dao
interface ReviewDao {

    @Query("SELECT id, movie_id, author, content, url FROM review " + "WHERE movie_id = :movieId")
    fun getByMovieId(movieId: Int): Flowable<List<ReviewEntity>>

    @Insert
    fun insertAll(reviews: List<ReviewEntity>)

    @Query("DELETE FROM review " + "WHERE movie_id IN (SELECT id FROM movie m WHERE id = :movieId)")
    fun deleteByMovieId(movieId: Int): Int
}
