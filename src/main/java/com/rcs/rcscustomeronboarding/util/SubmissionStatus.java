package com.rcs.rcscustomeronboarding.util;

public enum SubmissionStatus {
    DRAFT,
    SUBMITTED,
    IN_REVIEW,
    APPROVED,
    REJECTED;

    /**
     * Logic to prevent illegal state jumps (e.g., DRAFT directly to APPROVED)
     */
    public boolean canTransitionTo(SubmissionStatus nextStatus) {
        return switch (this) {
            case DRAFT -> nextStatus == SUBMITTED;
            case SUBMITTED -> nextStatus == IN_REVIEW || nextStatus == REJECTED;
            case IN_REVIEW -> nextStatus == APPROVED || nextStatus == REJECTED;
            case APPROVED, REJECTED -> false;
        };
    }
}
