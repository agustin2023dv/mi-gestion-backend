package com.migestion.shared.security;

import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AuthenticatedUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Long tenantId;
    private final String role;
    private final List<String> permissions;
    private final List<GrantedAuthority> authorities;

    @Builder
    private AuthenticatedUserDetails(
            Long id,
            String email,
            String password,
            Long tenantId,
            String role,
            List<String> permissions) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
        this.role = role;
        this.permissions = permissions == null ? List.of() : List.copyOf(permissions);
        this.authorities = buildAuthorities(this.role, this.permissions);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private static List<GrantedAuthority> buildAuthorities(String role, List<String> permissions) {
        List<GrantedAuthority> grantedAuthorities = new java.util.ArrayList<>();
        if (role != null && !role.isBlank()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        permissions.stream()
                .filter(permission -> permission != null && !permission.isBlank())
                .map(SimpleGrantedAuthority::new)
                .forEach(grantedAuthorities::add);

        return List.copyOf(grantedAuthorities);
    }
}