package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.UserAuthEntity;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
    Optional<UserEntity> findByEmailAndPassword(String email, String password);
    Boolean existsByUsername(String username);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);
}
