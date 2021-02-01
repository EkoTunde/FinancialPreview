package com.ekosoftware.financialpreview.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.financialpreview.core.BaseDao
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.presentation.SimpleQueryData

@Dao
interface CategoryDao : BaseDao<Category> {

    @Query("SELECT * FROM categories")
    fun getCategories(): LiveData<List<Category>>

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(vararg category: Category)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategories(vararg category: Category)

    @Delete
    suspend fun deleteCategories(vararg category: Category)*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInitialCategories(vararg category: Category)

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    fun getCategory(id: String): LiveData<Category>

    @Query("SELECT categoryName FROM categories WHERE categoryId = :id")
    fun getCategoryName(id: String): LiveData<String>

    @Query("""
        SELECT 
            categoryId AS id,
            categoryName AS name,
            NULL as currencyCode,
            NULL AS amount,
            NULL AS typeId,
            categoryDescription AS description,
            categoryColorResId AS color,
            categoryIconResId AS iconResId 
        FROM categories
        WHERE categoryName LIKE '%' || :searchPhrase || '%'
        AND categoryDescription LIKE '%' || :searchPhrase || '%'
        ORDER BY categoryName
    """)
    fun getCategoriesAsSimpleData(searchPhrase: String) : LiveData<List<SimpleQueryData>>
}