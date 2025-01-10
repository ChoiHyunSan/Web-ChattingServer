package com.ll.webchattingserver.global.security;

import com.ll.webchattingserver.core.domain.user.User;
import com.ll.webchattingserver.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return CustomUserDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .role(user.getRole().getValue())
                .build();
    }
}
