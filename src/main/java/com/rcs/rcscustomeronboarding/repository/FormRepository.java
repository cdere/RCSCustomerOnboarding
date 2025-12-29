package com.rcs.rcscustomeronboarding.repository;

import com.rcs.rcscustomeronboarding.entity.FormVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<FormVersion, Long>{
}
