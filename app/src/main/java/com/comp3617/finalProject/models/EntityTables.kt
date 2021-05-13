package com.comp3617.finalProject.models

import androidx.room.*

@Entity(tableName = "groupsTable")
data class Group
    (
    var title: String,
    var description: String?,
    var date: String?,
    val parentGroupId: Long?,
) {
    @PrimaryKey(autoGenerate = true) var groupId: Long = 0
}

@Entity(tableName = "blocksTable")
data class Block(
    var title: String,
    var description: String?,
    var duration: String?,
    var icon: String?,
    var lat: Double?,
    var lng: Double?,
    val belongsToGroupId: Long,
    val belongsToDateId: Long
) {
    @PrimaryKey(autoGenerate = true) var blockId: Long = 0
}

@Entity(tableName = "mDatesTable")
data class MDate(
    var day: Int,
    var month: Int,
    var year: Int,
    var datOfWeek: Int,
    var iconDrawableId: Int?,
    val parentGroupId: Long
) {
    @PrimaryKey(autoGenerate = true) var dateId: Long = 0
}

/**
 * Relationship between Group and Block
 */
data class GroupAndBlock(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "belongsToGroupId"
    )
    val block: Block
)


@Entity(tableName = "placesTable")
class MPlace(
    var parentBlockId : Long,
    val NAME : String?,
    val ADDRESS: String?,
    val PHONE_NUMBER: String?,
    val OPENING_HOURS: String?,
    val latitude: Double?,
    val longitude: Double?,
    val RATING: Double?,
    val WEBSITE_URI: String?
   )
{
    @PrimaryKey(autoGenerate = true) var placeId: Long = 0
}