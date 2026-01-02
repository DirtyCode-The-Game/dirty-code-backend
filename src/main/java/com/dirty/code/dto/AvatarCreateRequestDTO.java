package com.dirty.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarCreateRequestDTO {
    private String name;
    private Integer stamina;
    private Integer str;
    private Integer karma;
    private Integer intelligence;
}
