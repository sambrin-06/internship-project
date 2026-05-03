package com.ict.internal_controls_testing.service;

import com.ict.internal_controls_testing.dto.ControlRequest;
import com.ict.internal_controls_testing.entity.Control;
import com.ict.internal_controls_testing.entity.User;
import com.ict.internal_controls_testing.exception.ResourceNotFoundException;
import com.ict.internal_controls_testing.repository.ControlRepository;
import com.ict.internal_controls_testing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ControlService {

    private final ControlRepository controlRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "controls", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Control> getAllControls(Pageable pageable) {
        return controlRepository.findAll(pageable);
    }

    @Cacheable(value = "control", key = "#id")
    public Control getControlById(Long id) {
        return controlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Control not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = {"controls", "control"}, allEntries = true)
    public Control createControl(ControlRequest request) {
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        Control control = Control.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .riskLevel(request.getRiskLevel())
                .deadline(request.getDeadline())
                .assignee(assignee)
                .build();

        return controlRepository.save(control);
    }

    @Transactional
    @CacheEvict(value = {"controls", "control"}, allEntries = true)
    public Control updateControl(Long id, ControlRequest request) {
        Control control = getControlById(id);

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        control.setTitle(request.getTitle());
        control.setDescription(request.getDescription());
        control.setStatus(request.getStatus());
        control.setRiskLevel(request.getRiskLevel());
        control.setDeadline(request.getDeadline());
        control.setAssignee(assignee);

        return controlRepository.save(control);
    }

    @Transactional
    @CacheEvict(value = {"controls", "control"}, allEntries = true)
    public void deleteControl(Long id) {
        Control control = getControlById(id);
        controlRepository.delete(control);
    }
}
