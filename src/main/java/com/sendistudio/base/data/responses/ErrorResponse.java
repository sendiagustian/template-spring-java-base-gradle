package com.sendistudio.base.data.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse extends WebResponse {
    private String messages;

    public ErrorResponse() {
    }

    public ErrorResponse(Boolean status, String messages) {
        super(status);
        this.messages = messages;
    }
}