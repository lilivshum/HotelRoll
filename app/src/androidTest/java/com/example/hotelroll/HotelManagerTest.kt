package com.example.hotelroll
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.domain.HotelManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// quick file to test hotel manager logic

class HotelManagerTest {

    private val manager = HotelManager()

    private fun stay(
        roomId: Long,
        start: LocalDate,
        end: LocalDate
    ) = Stay(
        stayId = 0,
        reservationId = 1,
        roomId = roomId,
        peopleInRoom = 2,
        checkInDate = start,
        checkOutDate = end
    )

    @Test
    fun roomAvailableWhenNoStaysExist() {
        val available = manager.isRoomAvailable(
            roomId = 101,
            checkInDate = LocalDate.of(2025, 1, 10),
            checkOutDate = LocalDate.of(2025, 1, 12),
            existingStays = emptyList()
        )

        assertTrue(available)
    }

    @Test
    fun roomUnavailableWhenDatesExactlyMatch() {
        val stays = listOf(
            stay(101,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 10),
            LocalDate.of(2025, 1, 15),
            stays
        )

        assertFalse(available)
    }



    @Test
    fun roomUnavailableWhenNewStayInsideExistingStay() {
        val stays = listOf(
            stay(101,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 20)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 12),
            LocalDate.of(2025, 1, 15),
            stays
        )

        assertFalse(available)
    }

    @Test
    fun roomUnavailableWhenExistingStayInsideNewStay() {
        val stays = listOf(
            stay(101,
                LocalDate.of(2025, 1, 12),
                LocalDate.of(2025, 1, 15)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 10),
            LocalDate.of(2025, 1, 20),
            stays
        )

        assertFalse(available)
    }

    @Test
    fun roomAvailableWhenCheckoutEqualsCheckin() {
        val stays = listOf(
            stay(101,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 15),
            LocalDate.of(2025, 1, 18),
            stays
        )

        assertTrue(available)
    }

    @Test
    fun roomAvailableWhenOtherRoomHasStay() {
        val stays = listOf(
            stay(102,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 12),
            LocalDate.of(2025, 1, 14),
            stays
        )

        assertTrue(available)
    }

    @Test
    fun roomUnavailableWhenOverlapsAnyStay() {
        val stays = listOf(
            stay(101,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 5)
            ),
            stay(101,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
            )
        )

        val available = manager.isRoomAvailable(
            101,
            LocalDate.of(2025, 1, 12),
            LocalDate.of(2025, 1, 13),
            stays
        )

        assertFalse(available)
    }

    // this test case fails but it usually wouldn't occur, reservation is sert first and the check is
    // in the reservation 
    @Test(expected = IllegalArgumentException::class)
    fun createStayFailsWhenCheckoutBeforeCheckin() {
        manager.createStay(
            resId = 1,
            roomId = 101,
            peopleInRoom = 2,
            checkInDate = LocalDate.of(2025, 1, 10),
            checkOutDate = LocalDate.of(2025, 1, 5)
        )
    }




}