package id.yukelola.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import id.yukelola.core.database.entity.BusinessEntity

@Dao
interface BusinessDao {

    @Upsert
    fun upsert(business: BusinessEntity)

    @Query("SELECT * FROM businesses WHERE id = :id")
    fun findById(id: String): BusinessEntity?
}
