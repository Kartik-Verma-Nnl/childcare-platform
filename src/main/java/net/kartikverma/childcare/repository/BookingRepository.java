package net.kartikverma.childcare.repository;

import net.kartikverma.childcare.enums.BookingStatus;
import net.kartikverma.childcare.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByParentId(Long parentId);

    List<Booking> findByCaregiverId(Long caregiverId);

    Page<Booking> findAll(Pageable pageable);

    List<Booking> findByParentIdAndStatus(Long parentId, BookingStatus status);

    List<Booking> findByCaregiverIdAndStatus(Long caregiverId, BookingStatus status);

    Boolean existsBySlotIdAndStatusNot(Long slotId, BookingStatus status);
}
