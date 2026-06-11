package com.sendistudio.base.app.helpers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sendistudio.base.data.models.UserModel;


@Service
public class UserDetailsHelper implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Dummy — JWT auth builds the principal from token claims in JwtAuthFilter.
        // Replace this with a real UserSource lookup once the user domain is implemented.
        return UserModel.builder().username(username).build();
    }
}
