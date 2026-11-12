package com.teame10.healthcare.entity;

/**
 * Team E10 - Digital Queue Token Status Lifecycle
 */
public enum TokenStatus {
    GENERATED,
    CALLING,
    SERVING,
    COMPLETED,
    SKIPPED,
    CANCELLED
}
