package com.migestion.shared.security;

import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.JwtResponse.UserProfile;
import com.migestion.tenant.domain.Cliente;
import com.migestion.tenant.domain.ClienteRepository;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
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
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final TenantRepository tenantRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return superAdminRepository.findByEmailIgnoreCase(email)
                .map(this::toUserDetails)
                .or(() -> usuarioTenantRepository.findByEmailIgnoreCase(email).map(this::toUserDetails))
                .or(() -> clienteRepository.findFirstByEmailIgnoreCase(email).map(this::toUserDetails))
                .orElseThrow(() -> new UsernameNotFoundException("No user found for email: " + email));
    }

    private UserDetails toUserDetails(UsuarioTenant usuario) {
        Tenant tenant = tenantRepository.findById(usuario.getTenantId()).orElse(null);
        UserProfile profile = UserProfile.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .role(usuario.getRol().toUpperCase())
                .tenantId(usuario.getTenantId())
                .tenantSlug(tenant != null ? tenant.getSlug() : null)
                .tenantNombre(tenant != null ? tenant.getNombreNegocio() : null)
                .telefono(usuario.getTelefono())
                .isActive(usuario.isActive())
                .build();

        return AuthenticatedUserDetails.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .tenantId(usuario.getTenantId())
                .role(usuario.getRol().toUpperCase())
                .permissions(List.of())
                .userProfile(profile)
                .build();
    }

    private UserDetails toUserDetails(Cliente cliente) {
        Tenant tenant = tenantRepository.findById(cliente.getTenantId()).orElse(null);
        UserProfile profile = UserProfile.builder()
                .id(cliente.getId())
                .email(cliente.getEmail())
                .role(CLIENTE_ROLE)
                .tenantId(cliente.getTenantId())
                .tenantSlug(tenant != null ? tenant.getSlug() : null)
                .tenantNombre(tenant != null ? tenant.getNombreNegocio() : null)
                .isActive(true)
                .build();

        return AuthenticatedUserDetails.builder()
                .id(cliente.getId())
                .email(cliente.getEmail())
                .password(cliente.getPasswordHash())
                .tenantId(cliente.getTenantId())
                .role(CLIENTE_ROLE)
                .permissions(List.of())
                .userProfile(profile)
                .build();
    }

    private UserDetails toUserDetails(SuperAdmin superAdmin) {
        UserProfile profile = UserProfile.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getEmail())
                .nombre(superAdmin.getNombre())
                .apellido(superAdmin.getApellido())
                .role(SUPERADMIN_ROLE)
                .isActive(superAdmin.isActive())
                .build();

        return AuthenticatedUserDetails.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getEmail())
                .password(superAdmin.getPasswordHash())
                .tenantId(null)
                .role(SUPERADMIN_ROLE)
                .permissions(List.of())
                .userProfile(profile)
                .build();
    }
}