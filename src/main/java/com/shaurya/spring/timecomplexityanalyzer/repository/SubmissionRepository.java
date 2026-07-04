package com.shaurya.spring.timecomplexityanalyzer.repository;

import com.shaurya.spring.timecomplexityanalyzer.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long> {
}
