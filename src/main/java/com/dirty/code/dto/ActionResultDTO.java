package com.dirty.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionResultDTO {
    private boolean success;
    private AvatarResponseDTO avatar;
    private Integer timesExecuted;
    private Map<String, Object> variations;
}
