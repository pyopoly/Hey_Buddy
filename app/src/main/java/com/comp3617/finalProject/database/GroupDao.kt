package com.comp3617.finalProject.database

import androidx.room.*
import com.comp3617.finalProject.models.Group

@Dao
interface GroupDao {

    @get:Query("SELECT * FROM groupsTable")
    val allGroups: List<Group>

    @get:Query("SELECT * FROM groupsTable WHERE parentGroupId IS 0")
    val allTopLevelGroups: List<Group>

    @Query("SELECT * FROM groupsTable WHERE parentGroupId LIKE :id")
    fun findChildrenByParentGroupId(id: Long): List<Group>

    @Query("SELECT parentGroupId FROM groupsTable WHERE groupId LIKE :id")
    fun findParentIdByChildId(id: Long): Long?

    @Query("SELECT * FROM groupsTable WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): Group?

    @Query("SELECT * FROM groupsTable WHERE groupId = :id")
    fun findById(id: Long): Group?

    @Query("SELECT groupId FROM groupsTable WHERE title LIKE :title AND description IS :description AND date IS :date AND parentGroupId IS :parentGroupId")
    fun findId(title: String, description: String?, date: String?, parentGroupId: Long?): Long

    @Query("SELECT * FROM groupsTable WHERE title LIKE :title AND description IS :description AND date IS :date AND parentGroupId IS :parentGroupId")
    fun findGroupByAttributes(title: String, description: String?, date: String?, parentGroupId: Long?): Group?

    @Insert
    fun insertAll(groups: List<Group>): List<Long>

    @Insert
    fun insertOne(group: Group)

    @Update
    fun update(group: Group)

    @Delete
    fun delete(group: Group)
}