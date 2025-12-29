package com.rcs.rcscustomeronboarding.entity;

import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@Audited
@Entity
public class FormVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String oldRemarks;
    private String newRemarks;
    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus newStatus;
}
