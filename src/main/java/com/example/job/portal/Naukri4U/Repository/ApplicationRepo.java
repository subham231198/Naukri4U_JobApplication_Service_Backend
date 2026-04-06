package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepo extends JpaRepository<ApplicationEntity, Long>
{
    List<ApplicationEntity> findByUsername(String username);
    List<ApplicationEntity> findByJobReqId(String jobReqId);
    Boolean existsByUsernameAndJobReqId(String username, String jobReqId);
}
