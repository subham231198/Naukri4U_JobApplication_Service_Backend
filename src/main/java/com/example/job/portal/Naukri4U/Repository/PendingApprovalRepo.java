package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.PendingAccountApprovalQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingApprovalRepo extends JpaRepository<PendingAccountApprovalQueue, Long> {
    Optional<PendingAccountApprovalQueue> findByUsername(String username);
    Optional<PendingAccountApprovalQueue> findByEmail(String email);
    Optional<PendingAccountApprovalQueue> findByCompanyName(String companyName);
    Optional<PendingAccountApprovalQueue> deleteByUsername(String username);
}
