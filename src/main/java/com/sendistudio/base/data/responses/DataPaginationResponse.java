package com.sendistudio.base.data.responses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sendistudio.base.data.models.PagingModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({ "status", "messages", "data", "pageInfo" })
public class DataPaginationResponse<T> extends WebResponse {
    private T data;
    private PagingModel pageInfo;

    public DataPaginationResponse() {
    }

    public DataPaginationResponse(Boolean status, T data, PagingModel pageInfo) {
        super(status, null);
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public DataPaginationResponse(Boolean status, String messages, T data, PagingModel pageInfo) {
        super(status, messages);
        this.data = data;
        this.pageInfo = pageInfo;
    }
}
