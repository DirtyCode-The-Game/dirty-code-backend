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
public class AvatarUpdateRequestDTO {
    private String name;
    private String picture;
    private Integer intelligence;
    private Integer charisma;
    private Integer strength;
    private Integer stealth;
}
