package com.sendistudio.base.data.responses;

import com.sendistudio.base.data.models.PagingModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataPaginationResponse<T> extends WebResponse {
    private T data;
    private PagingModel paging;

    public DataPaginationResponse() {
    }

    public DataPaginationResponse(Boolean status, T data, PagingModel paging) {
        super(status);
        this.data = data;
        this.paging = paging;
    }
}
