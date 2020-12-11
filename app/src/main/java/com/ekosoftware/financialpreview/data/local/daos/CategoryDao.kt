package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table")
    fun getCategories(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(vararg category: Category)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategories(vararg category: Category)

    @Delete
    suspend fun deleteCategories(vararg category: Category)

}