package com.devos.backend.team.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferTeamLeadRequest {

    @NotNull(message = "User ID is required")
    private Long userId;
}