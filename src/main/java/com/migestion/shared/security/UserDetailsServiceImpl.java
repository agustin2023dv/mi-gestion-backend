package com.migestion.shared.security;

import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.tenant.domain.Cliente;
import com.migestion.tenant.domain.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String CLIENTE_ROLE = "CLIENTE";
    private static final String SUPERADMIN_ROLE = "SUPERADMIN";

    private final ClienteRepository clienteRepository;
    private final SuperAdminRepository superAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return superAdminRepository.findByEmailIgnoreCase(email)
                .map(this::toUserDetails)
                .or(() -> clienteRepository.findFirstByEmailIgnoreCase(email).map(this::toUserDetails))
                .orElseThrow(() -> new UsernameNotFoundException("No user found for email: " + email));
    }

    private UserDetails toUserDetails(Cliente cliente) {
        return AuthenticatedUserDetails.builder()
                .id(cliente.getId())
                .email(cliente.getEmail())
                .password(cliente.getPasswordHash())
                .tenantId(cliente.getTenantId())
                .role(CLIENTE_ROLE)
                .permissions(List.of())
                .build();
    }

    private UserDetails toUserDetails(SuperAdmin superAdmin) {
        return AuthenticatedUserDetails.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getEmail())
                .password(superAdmin.getPasswordHash())
                .tenantId(null)
                .role(SUPERADMIN_ROLE)
                .permissions(List.of())
                .build();
    }
}