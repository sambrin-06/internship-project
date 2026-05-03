package com.ict.internal_controls_testing.service;

import com.ict.internal_controls_testing.dto.ControlRequest;
import com.ict.internal_controls_testing.entity.Control;
import com.ict.internal_controls_testing.entity.User;
import com.ict.internal_controls_testing.exception.ResourceNotFoundException;
import com.ict.internal_controls_testing.repository.ControlRepository;
import com.ict.internal_controls_testing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControlServiceTest {

    @Mock
    private ControlRepository controlRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ControlService controlService;

    private Control control;
    private ControlRequest controlRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testuser").build();

        control = Control.builder()
                .id(1L)
                .title("Test Control")
                .description("Test Description")
                .status("PENDING")
                .riskLevel("HIGH")
                .deadline(LocalDateTime.now().plusDays(1))
                .assignee(user)
                .build();

        controlRequest = new ControlRequest();
        controlRequest.setTitle("Test Control");
        controlRequest.setDescription("Test Description");
        controlRequest.setStatus("PENDING");
        controlRequest.setRiskLevel("HIGH");
        controlRequest.setDeadline(LocalDateTime.now().plusDays(1));
        controlRequest.setAssigneeId(1L);
    }

    @Test
    void getAllControls_ShouldReturnPageOfControls() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Control> controlPage = new PageImpl<>(Collections.singletonList(control));
        when(controlRepository.findAll(pageable)).thenReturn(controlPage);

        Page<Control> result = controlService.getAllControls(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(controlRepository, times(1)).findAll(pageable);
    }

    @Test
    void getControlById_WhenExists_ShouldReturnControl() {
        when(controlRepository.findById(1L)).thenReturn(Optional.of(control));

        Control result = controlService.getControlById(1L);

        assertNotNull(result);
        assertEquals("Test Control", result.getTitle());
    }

    @Test
    void getControlById_WhenDoesNotExist_ShouldThrowException() {
        when(controlRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controlService.getControlById(1L));
    }

    @Test
    void createControl_WithValidAssignee_ShouldSaveAndReturnControl() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(controlRepository.save(any(Control.class))).thenReturn(control);

        Control result = controlService.createControl(controlRequest);

        assertNotNull(result);
        assertEquals("Test Control", result.getTitle());
        verify(controlRepository, times(1)).save(any(Control.class));
    }

    @Test
    void createControl_WithInvalidAssignee_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controlService.createControl(controlRequest));
        verify(controlRepository, never()).save(any(Control.class));
    }

    @Test
    void createControl_WithoutAssignee_ShouldSaveAndReturnControl() {
        controlRequest.setAssigneeId(null);
        when(controlRepository.save(any(Control.class))).thenReturn(control);

        Control result = controlService.createControl(controlRequest);

        assertNotNull(result);
        verify(controlRepository, times(1)).save(any(Control.class));
    }

    @Test
    void updateControl_WhenControlExists_ShouldUpdateAndReturnControl() {
        when(controlRepository.findById(1L)).thenReturn(Optional.of(control));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(controlRepository.save(any(Control.class))).thenReturn(control);

        controlRequest.setTitle("Updated Title");
        Control result = controlService.updateControl(1L, controlRequest);

        assertNotNull(result);
        verify(controlRepository, times(1)).save(any(Control.class));
    }

    @Test
    void updateControl_WhenControlDoesNotExist_ShouldThrowException() {
        when(controlRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controlService.updateControl(1L, controlRequest));
    }

    @Test
    void deleteControl_WhenExists_ShouldDelete() {
        when(controlRepository.findById(1L)).thenReturn(Optional.of(control));

        controlService.deleteControl(1L);

        verify(controlRepository, times(1)).delete(control);
    }

    @Test
    void deleteControl_WhenDoesNotExist_ShouldThrowException() {
        when(controlRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controlService.deleteControl(1L));
        verify(controlRepository, never()).delete(any());
    }
}
