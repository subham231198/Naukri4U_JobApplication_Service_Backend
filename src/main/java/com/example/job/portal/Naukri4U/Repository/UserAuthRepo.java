package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.UserAuthEntity;
import com.example.job.portal.Naukri4U.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepo extends JpaRepository<UserAuthEntity, Long>
{
    Optional<UserAuthEntity> findByUsername(String username);
}
