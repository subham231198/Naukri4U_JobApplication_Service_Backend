package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepo extends JpaRepository<JobEntity, Long> {

    List<JobEntity> findByJobTitle(String jobTitle);
    List<JobEntity> findByCompanyName(String companyName);
    List<JobEntity> findByLocation(String location);
    Optional<JobEntity> findByJobReqId(String job_req_Id);
    List<JobEntity> findByMinSalaryLessThanEqualAndMaxSalaryGreaterThanEqual(
            Integer expectedMax,
            Integer expectedMin
    );
    List<JobEntity> findByAddedBy(String addedBy);
    List<JobEntity> findByMinExperienceLessThanEqualAndMaxExperienceGreaterThanEqual(
            Integer expectedMax,
            Integer expectedMin
    );
}
