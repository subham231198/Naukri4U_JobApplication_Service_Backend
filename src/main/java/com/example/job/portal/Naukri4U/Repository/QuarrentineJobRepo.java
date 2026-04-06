package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.QuarrentineJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuarrentineJobRepo extends JpaRepository<QuarrentineJobEntity, Long>
{
    Optional<QuarrentineJobEntity> findByJobReqId(String jobReqId);
    Optional<QuarrentineJobEntity> findByAddedBy(String addedBy);
    Boolean existsByJobReqId(String jobReqId);
}
