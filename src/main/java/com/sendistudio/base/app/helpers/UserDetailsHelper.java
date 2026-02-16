package com.sendistudio.base.app.helpers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sendistudio.base.data.models.UserModel;
import com.sendistudio.base.domain.sources.UserSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsHelper implements UserDetailsService {

    private final UserSource userSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userSource.getUserByEmail(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found"));

        return user;
    }
}
