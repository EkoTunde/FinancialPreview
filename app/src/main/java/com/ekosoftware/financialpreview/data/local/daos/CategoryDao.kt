package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.presentation.SimpleQueryData

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getCategories(): LiveData<List<Category>>

    @Query(
        """
        SELECT categoryId AS id,
        categoryName AS name,
        categoryDescription AS description,
        categoryColorResId AS color,
        categoryIconResId AS iconResId 
        FROM categories 
        WHERE categoryName LIKE '%' || :searchPhrase || '%'
    """
    )
    fun getCategoriesAsSimpleData(searchPhrase: String): LiveData<List<SimpleQueryData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(vararg category: Category)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategories(vararg category: Category)

    @Delete
    suspend fun deleteCategories(vararg category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInitialCategories(vararg category: Category)

}