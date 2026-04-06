package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<OTPEntity, Long> {

    Optional<OTPEntity> findByUsername(String username);

    Optional<OTPEntity> findByPhone(String phone);

    Optional<OTPEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    Boolean existsByUsername(String username);
}
