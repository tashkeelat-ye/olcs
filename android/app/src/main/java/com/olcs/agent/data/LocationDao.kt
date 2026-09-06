package com.olcs.agent.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert(
        onConflict =
            OnConflictStrategy.IGNORE
    )
    suspend fun insert(
        location: LocationEntity
    )

    @Query(
        """
        SELECT *
        FROM location_events
        WHERE status = 'PENDING'
        OR status = 'FAILED'
        ORDER BY capturedAt ASC
        LIMIT 100
        """
    )
    suspend fun getPending():
        List<LocationEntity>

    @Query(
        """
        UPDATE location_events
        SET
          status = :status,
          attempts = attempts + 1,
          lastAttemptAt = :time,
          transport = :transport
        WHERE eventId = :eventId
        """
    )
    suspend fun updateStatus(
        eventId: String,
        status: String,
        transport: String?,
        time: Long
    )

    @Query(
        """
        SELECT COUNT(*)
        FROM location_events
        WHERE status = 'PENDING'
        OR status = 'FAILED'
        """
    )
    suspend fun pendingCount():
        Int
}
