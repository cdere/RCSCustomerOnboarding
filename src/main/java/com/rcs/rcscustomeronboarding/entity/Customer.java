package com.rcs.rcscustomeronboarding.entity;

import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Audited
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    private String remarks;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @Column(updatable = false)
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Integer version; // For optimistic locking and simple version tracking

    @NotBlank(message = "RCS Provider is mandatory")
    private String rcsProvider;

    @Min(value = 1000, message = "Expected monthly volume must be at least 1000")
    private Integer expectedMonthlyVolume;

    public void transitionTo(SubmissionStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    "Invalid workflow transition: Cannot move from " + this.status + " to " + nextStatus
            );
        }
        this.status = nextStatus;
    }

    /*private String businessEIN;
    private String organizationType;

    private String brandName;
    private String brandDescription;

    private String agentName;
    private String agentPurpose; // OTP/Transactional/Promotional/Multi-use
    private String agentServiceCode;
    private String agentPhoneNumber;
    private String agentAddress;

    private String contactName;
    private String contactEmail;
    private int contactPhoneNumber;
    private String feedback;

    private String messageWebhookURL;*/

}
