package net.kartikverma.childcare.repository;

import net.kartikverma.childcare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    Page<User> findAll(Pageable pageable);

    Boolean existsByEmail(String username);
}
