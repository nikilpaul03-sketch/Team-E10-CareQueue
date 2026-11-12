package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {

    AuditLog logAction(String performedBy, String action, String entityName, String entityId, String details, String ipAddress);

    List<AuditLog> getLogsByPerformedBy(String performedBy);

    Page<AuditLog> getLogsByEntity(String entityName, Pageable pageable);

    Page<AuditLog> getAllLogsPaged(Pageable pageable);
}
