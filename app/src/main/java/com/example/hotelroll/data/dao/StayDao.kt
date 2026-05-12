package com.example.hotelroll.data.dao
import androidx.room.*
import com.example.hotelroll.data.model.Reservation
import java.time.LocalDate
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.dto.StayWithRoomNumber


@Dao
interface StayDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(stay: Stay): Long

    // function to check if its overlapping
    @Query("""
        SELECT * FROM stays
        WHERE roomId = :roomId
        AND checkInDate < :endDate
        AND checkOutDate > :startDate
    """)
    suspend fun getOverlapping(
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Stay>

    @Query("""
        SELECT * FROM stays
        WHERE stayId = :stayId
    """)
    suspend fun getById(
        stayId: Long
    ): Stay?

    // function that updates status of a certain stay
    @Query("""
        UPDATE stays 
        SET status = :status 
        WHERE stayId = :stayId
    """)
    suspend fun updateStatus(
        stayId: Long,
        status: StayStatus
    )

    @Delete
    suspend fun delete(stay: Stay)

    @Update
    suspend fun update(stay: Stay)

    // helps to obtain all the rooms / stays under a reservation
    @Query("""
        SELECT * FROM stays
        WHERE reservationId = :resId
    """)
    suspend fun getByResId(
        resId: Long
    ): List<Stay>

    @Query("""
        UPDATE stays
        SET notes = :notes
        WHERE stayId= :stayId
    """)
    suspend fun updateStayNotes(
        stayId: Long,
        notes: String
    )

    @Query("SELECT COUNT(*) FROM stays")
    suspend fun countStays(): Int

    @Query("""
        SELECT 
            s.stayId AS stayId,
            r.roomNumber AS roomNumber,
            s.peopleInRoom AS peopleInRoom,
            s.checkInDate AS checkInDate,
            s.checkOutDate AS checkOutDate, 
            s.kidsInRoom AS kidsInRoom
        FROM stays s
        INNER JOIN rooms r ON s.roomId = r.roomId
        WHERE s.reservationId = :reservationId
    """)
    suspend fun getStaysWithRoomNumber(
        reservationId: Long
    ): List<StayWithRoomNumber>

    // function that checks overlap
    @Query("""
SELECT EXISTS(
    SELECT 1 FROM stays
    WHERE roomId = :roomId
    AND stayId != :excludeStayId
    AND :checkIn < checkOutDate
    AND :checkOut > checkInDate
)
""")
    suspend fun hasOverlap(
        roomId: Long,
        checkIn: LocalDate,
        checkOut: LocalDate,
        excludeStayId: Long
    ): Boolean

    @Query(
        """
            SELECT EXISTS(
            SELECT 1 FROM stays 
            WHERE reservationId =:resId
            AND status = :status
            )
        """
    )
    suspend fun hasConfirmedStay(
        resId: Long,
        status: StayStatus = StayStatus.CONFIRMED
    ): Boolean

    @Query("""
    UPDATE stays
    SET roomId = :newRoomId
    WHERE stayId = :stayId
""")
    suspend fun updateRoom(
        stayId: Long,
        newRoomId: Long
    )

    @Query("""
        SELECT DISTINCT roomId FROM stays
        WHERE stayId != :excludeStayId
        AND :checkIn < checkOutDate
        AND :checkOut > checkInDate
    """)
    suspend fun getBlockedRoomIds(
        checkIn: LocalDate,
        checkOut: LocalDate,
        excludeStayId: Long
    ): List<Long>

}
