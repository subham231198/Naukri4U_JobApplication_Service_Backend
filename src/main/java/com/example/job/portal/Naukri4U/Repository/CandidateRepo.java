package com.example.job.portal.Naukri4U.Repository;

import com.example.job.portal.Naukri4U.Entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.plaf.OptionPaneUI;
import java.util.Optional;

@Repository
public interface CandidateRepo extends JpaRepository<CandidateEntity, Long> {

    Optional<CandidateEntity> findByUsername(String username);
}
