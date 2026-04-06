package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.RecruiterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepo extends JpaRepository<RecruiterEntity, Long> {
    Optional<RecruiterEntity> findByUsername(String username);
}
