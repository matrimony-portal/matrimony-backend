package com.scriptbliss.bandhan.shared.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		return new CustomUserPrincipal(user);
	}

	public static class CustomUserPrincipal implements UserDetails {
		private static final long serialVersionUID = 1L;

		private final Long userId;
		private final String email;
		private final String password;
		private final String role;
		private final boolean active;

		public CustomUserPrincipal(User user) {
			this.userId = user.getId();
			this.email = user.getEmail();
			this.password = user.getPassword();
			this.role = user.getRole().name();
			this.active = user.getStatus() == AccountStatus.ACTIVE;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
		}

		@Override
		public String getPassword() {
			return password;
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
			return active;
		}

		public Long getUserId() {
			return userId;
		}

		public String getEmail() {
			return email;
		}

		public String getRole() {
			return role;
		}
	}
}