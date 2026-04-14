package com.migestion.shared.security;

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

    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return clienteRepository.findFirstByEmailIgnoreCase(email)
                .map(this::toUserDetails)
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
}