package com.tbcpl.workforce.ttr.entity.enums;

public enum TtrStatus {

    S1_OPENED,
    S2_IN_PROGRESS,
    S3_COMPLETED,
    S4_CHANGES_REQUESTED,
    S5_CLOSED;

    public boolean canTransitionTo(TtrStatus next) {
        return switch (this) {
            case S1_OPENED            -> next == S2_IN_PROGRESS;
            case S2_IN_PROGRESS       -> next == S3_COMPLETED;
            case S3_COMPLETED         -> next == S4_CHANGES_REQUESTED || next == S5_CLOSED;
            case S4_CHANGES_REQUESTED -> next == S2_IN_PROGRESS;
            case S5_CLOSED            -> false;
        };
    }
}