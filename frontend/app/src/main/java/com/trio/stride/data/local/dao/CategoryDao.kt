package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import com.trio.stride.data.local.entity.CategoryEntity
import com.trio.stride.data.local.entity.SportEntity

data class CategoryWithSports(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val sports: List<SportEntity>
)

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(sports: List<CategoryEntity>)

    @Query("SELECT * FROM category")
    suspend fun getAllCategories(): List<CategoryEntity>

//    @Transaction
//    @Query("SELECT * FROM category")
//    suspend fun getAllCategoryWithSports(): List<CategoryWithSports>

//    @Query("SELECT * FROM category WHERE id = :id")
//    suspend fun getCategoryById(id: String): CategoryEntity?
}