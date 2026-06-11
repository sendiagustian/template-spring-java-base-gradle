package com.sendistudio.base.data.responses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({ "status", "messages", "data" })
public class DataResponse<T> extends WebResponse {
    private T data;

    public DataResponse() {
    }

    public DataResponse(Boolean status, String messages, T data) {
        super(status, messages);
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        DataResponse<?> that = (DataResponse<?>) obj;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
