package com.sendistudio.base.data.responses;

import com.sendistudio.base.data.models.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends UserModel {
    private String accessToken;
    private String refreshToken;
}
