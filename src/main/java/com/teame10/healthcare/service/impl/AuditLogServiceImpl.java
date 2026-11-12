package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.AuditLog;
import com.teame10.healthcare.repository.AuditLogRepository;
import com.teame10.healthcare.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public AuditLog logAction(String performedBy, String action, String entityName, String entityId, String details, String ipAddress) {
        AuditLog log = new AuditLog(performedBy, action, entityName, entityId, details, ipAddress);
        return auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> getLogsByPerformedBy(String performedBy) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(performedBy);
    }

    @Override
    public Page<AuditLog> getLogsByEntity(String entityName, Pageable pageable) {
        return auditLogRepository.findByEntityName(entityName, pageable);
    }

    @Override
    public Page<AuditLog> getAllLogsPaged(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
