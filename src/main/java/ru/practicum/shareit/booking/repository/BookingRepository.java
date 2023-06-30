package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingClosest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("UPDATE Booking b SET " +
            "b.status = CASE WHEN :available = TRUE THEN 'APPROVED' " +
            "ELSE 'REJECTED' END " +
            "WHERE b.id = :bookingId")
    void updateBookingStatusById(Long bookingId, Boolean available);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, StatusType status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdCurrent(long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdPast(long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdFuture(long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdCurrentOrderByStartDesc(long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdPastOrderByStartDesc(long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdFutureOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, StatusType status);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingClosest(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC ")
    List<BookingClosest> findNextClosestBookingByOwnerId(long ownerId, long itemId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingClosest(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC ")
    List<BookingClosest> findLastClosestBookingByOwnerId(long ownerId, long itemId);

}
