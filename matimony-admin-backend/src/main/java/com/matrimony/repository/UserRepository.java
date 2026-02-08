package com.matrimony.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.matrimony.entites.Role;
import com.matrimony.entites.Status;
import com.matrimony.entites.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    Optional<User> findByIdAndRole(Long id, Role role);
    Optional<User> findByEmailAndRole(String email, Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(Status status);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND u.role = :role")
    Long countByStatusAndRole(Status status, Role role);
    
    List<User> findByStatus(Status status);
    List<User> findByStatusOrderByCreatedAtDesc(Status status);
    
    List<User> findByRoleOrderByCreatedAtDesc(Role role);
    List<User> findAllByOrderByCreatedAtDesc();
    
    Optional<User> findByEmail(String email);
}
