package com.sendistudio.base.data.requests.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String tenantId;
    private String email;
    private String passwordHash;
    private String globalRole;
    private String status;
}
