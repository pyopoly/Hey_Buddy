package com.comp3617.finalProject.database

import androidx.room.*
import com.comp3617.finalProject.models.Block

@Dao
interface BlockDao {
    @get:Query("SELECT * FROM blocksTable")
    val allBlocks: List<Block>

    @Query("SELECT * FROM blocksTable WHERE belongsToGroupId LIKE :groupId AND belongsToDateId LIKE :dateId")
    fun getChildBlocks(groupId: Long, dateId: Long): List<Block>

    @Query("SELECT * FROM blocksTable WHERE description LIKE :title LIMIT 1")
    fun findByTitle(title: String): Block?

    @Query("SELECT * FROM blocksTable WHERE belongsToGroupId LIKE :groupId AND belongsToDateId LIKE :dateId LIMIT 1")
    fun findFirstBlock(groupId: Long, dateId: Long): Block?

    @Query("SELECT * FROM blocksTable WHERE BlockId = :id")
    fun findById(id: Long): Block?

    @Query("SELECT blockId FROM blocksTable WHERE title IS :title AND description IS :description AND duration IS :duration AND icon IS :icon AND lat IS :lat AND lng IS :lng AND belongsToGroupId IS :belongsToGroupId AND belongsToDateId IS :belongsToDateId")
    fun findBlockId(title: String, description: String?, duration: String?, icon: String?, lat: Double?, lng: Double?, belongsToGroupId: Long?, belongsToDateId: Long?): Long

    @Insert
    fun insertAll(blocks: List<Block>)

    @Update
    fun update(block: Block)

    @Delete
    fun delete(block: Block)
        
}