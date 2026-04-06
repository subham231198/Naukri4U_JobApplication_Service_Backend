package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<CompanyEntity, Long> {
    CompanyEntity findByCompanyName(String companyName);
    CompanyEntity findByDomain(String domain);
}
