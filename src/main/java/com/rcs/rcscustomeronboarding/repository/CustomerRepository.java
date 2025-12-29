package com.rcs.rcscustomeronboarding.repository;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends
        RevisionRepository<Customer, Long, Integer>,
        JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer>
        {
    List<Customer> findByStatus(SubmissionStatus submissionStatus);

}
