package net.kartikverma.childcare.repository;

import net.kartikverma.childcare.model.CaregiverProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaregiverRepository extends JpaRepository<CaregiverProfile, Long> {

    Optional<CaregiverProfile> findByUserId(Long userId);

    // Keep the old method for internal use (e.g. cache eviction logic elsewhere)
    List<CaregiverProfile> findByIsVerifiedTrue();

    // New paginated version
    Page<CaregiverProfile> findByIsVerifiedTrue(Pageable pageable);

    List<CaregiverProfile> findByIsVerifiedFalse();

    List<CaregiverProfile> findByCityAndIsVerifiedTrue(String city);
}