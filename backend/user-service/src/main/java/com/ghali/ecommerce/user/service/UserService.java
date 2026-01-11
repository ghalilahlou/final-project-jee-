package com.ghali.ecommerce.user.service;

import com.ghali.ecommerce.user.model.User;
import com.ghali.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des utilisateurs (Admin, Vendor)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByKeycloakId(String keycloakId) {
        log.info("👤 Fetching user by Keycloak ID: {}", keycloakId);
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found with Keycloak ID: " + keycloakId));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsers(String keyword) {
        log.info("🔍 Searching users with keyword: {}", keyword);
        return userRepository.searchUsers(keyword);
    }

    @Transactional
    public User createUser(User user) {
        log.info("✨ Creating new user: {}", user.getUsername());
        
        // Vérifications
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists with username: " + user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }

        User savedUser = userRepository.save(user);
        log.info("✅ User created: {} with roles: {}", savedUser.getUsername(), savedUser.getRoles());
        return savedUser;
    }

    @Transactional
    public User updateUser(String keycloakId, User userData) {
        log.info("🔄 Updating user: {}", keycloakId);
        
        User existingUser = getUserByKeycloakId(keycloakId);
        
        if (userData.getFirstName() != null) {
            existingUser.setFirstName(userData.getFirstName());
        }
        if (userData.getLastName() != null) {
            existingUser.setLastName(userData.getLastName());
        }
        if (userData.getPhone() != null) {
            existingUser.setPhone(userData.getPhone());
        }
        if (userData.getCompanyName() != null) {
            existingUser.setCompanyName(userData.getCompanyName());
        }
        if (userData.getCompanyAddress() != null) {
            existingUser.setCompanyAddress(userData.getCompanyAddress());
        }
        if (userData.getTaxId() != null) {
            existingUser.setTaxId(userData.getTaxId());
        }
        if (userData.getCommissionRate() != null) {
            existingUser.setCommissionRate(userData.getCommissionRate());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("✅ User updated: {}", updatedUser.getUsername());
        return updatedUser;
    }

    @Transactional
    public User addRole(Long userId, User.UserRole role) {
        log.info("➕ Adding role {} to user {}", role, userId);
        User user = getUserById(userId);
        user.addRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRole(Long userId, User.UserRole role) {
        log.info("➖ Removing role {} from user {}", role, userId);
        User user = getUserById(userId);
        user.removeRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {
        log.info("✅ Activating user: {}", userId);
        User user = getUserById(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        log.info("❌ Deactivating user: {}", userId);
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String keycloakId) {
        User user = getUserByKeycloakId(keycloakId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
