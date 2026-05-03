package com.ict.internal_controls_testing.controller;

import com.ict.internal_controls_testing.dto.ControlRequest;
import com.ict.internal_controls_testing.entity.Control;
import com.ict.internal_controls_testing.service.ControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controls")
@RequiredArgsConstructor
public class ControlController {

    private final ControlService controlService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Control>> getAllControls(Pageable pageable) {
        return ResponseEntity.ok(controlService.getAllControls(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Control> getControlById(@PathVariable Long id) {
        return ResponseEntity.ok(controlService.getControlById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Control> createControl(@Valid @RequestBody ControlRequest request) {
        Control createdControl = controlService.createControl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdControl);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Control> updateControl(@PathVariable Long id, @Valid @RequestBody ControlRequest request) {
        Control updatedControl = controlService.updateControl(id, request);
        return ResponseEntity.ok(updatedControl);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteControl(@PathVariable Long id) {
        controlService.deleteControl(id);
        return ResponseEntity.noContent().build();
    }
}
