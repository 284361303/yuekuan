package m.fasion.ai.util.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import m.fasion.ai.MyApp

/**
 * 搜索页面中的搜索历史保存数据库中
 */
@Entity(tableName = "history")
data class History(@PrimaryKey @ColumnInfo(name = "search_name") val searchName: String)

@Dao
interface HistoryDao {

    @Query("SELECT * FROM HISTORY ORDER BY search_name ASC")
    fun getHistoryList(): Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Query("DELETE FROM history")
    suspend fun delete()
}

@Database(entities = [History::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    MyApp.instance,
                    HistoryDatabase::class.java,
                    "history_list")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}