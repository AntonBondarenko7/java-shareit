package ru.practicum.shareit.booking.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findByBookerIdOrderByEndDesc(Long bookerId, Pageable page);

    List<Booking> findByBookerIdAndStatusOrderByEndDesc(
            Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndEndIsAfter(
            Long bookerId, LocalDateTime end, Pageable page);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBefore(Long itemId, BookingStatus status,
                                                                 LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfter(Long itemId, BookingStatus status,
                                                                LocalDateTime end, Sort sort);

    @Query("select count(b.id) as count " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < ?3 " +
            "group by b.id ")
    Long isFindBooking(Long itemId, Long userId, LocalDateTime end);

}
