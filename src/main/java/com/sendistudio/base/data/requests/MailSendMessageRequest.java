package com.sendistudio.base.data.requests;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class MailSendMessageRequest {

    @NotBlank
    private String to;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
