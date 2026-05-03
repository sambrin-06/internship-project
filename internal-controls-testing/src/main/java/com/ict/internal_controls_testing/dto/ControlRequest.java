package com.ict.internal_controls_testing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ControlRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Risk level is required")
    private String riskLevel;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    private Long assigneeId;
}
