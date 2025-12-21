package com.sendistudio.base.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingModel {
    private Integer size;
    private Integer totalData;
    private Integer currentPage;
    private Integer totalPage;
}
