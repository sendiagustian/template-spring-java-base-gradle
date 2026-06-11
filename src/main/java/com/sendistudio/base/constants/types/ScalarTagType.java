package com.sendistudio.base.constants.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScalarTagType {
    private Integer order;
    private String name;
    private String description;
}
