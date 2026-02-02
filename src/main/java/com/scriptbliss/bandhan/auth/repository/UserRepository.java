package com.scriptbliss.bandhan.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	List<User> findByRole(UserRole role);
}