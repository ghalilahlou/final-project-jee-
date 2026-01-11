package com.ghali.ecommerce.customer.service;

import com.ghali.ecommerce.customer.model.Customer;
import com.ghali.ecommerce.customer.model.CustomerAddress;
import com.ghali.ecommerce.customer.repository.CustomerAddressRepository;
import com.ghali.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des clients acheteurs
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;

    @Cacheable(value = "customers", key = "#keycloakId")
    public Customer getCustomerByKeycloakId(String keycloakId) {
        log.info("👤 Fetching customer by Keycloak ID: {}", keycloakId);
        return customerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Customer not found with Keycloak ID: " + keycloakId));
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public List<Customer> searchCustomers(String keyword) {
        log.info("🔍 Searching customers with keyword: {}", keyword);
        return customerRepository.searchCustomers(keyword);
    }

    public List<Customer> getTopCustomers(int minOrders) {
        return customerRepository.findTopCustomers(minOrders);
    }

    @Transactional
    @CacheEvict(value = "customers", key = "#customer.keycloakId")
    public Customer createCustomer(Customer customer) {
        log.info("✨ Creating new customer: {}", customer.getEmail());
        
        // Vérifier si email existe
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("Customer already exists with email: " + customer.getEmail());
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("✅ Customer created: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Transactional
    @CacheEvict(value = "customers", key = "#keycloakId")
    public Customer updateCustomer(String keycloakId, Customer customerData) {
        log.info("🔄 Updating customer: {}", keycloakId);
        
        Customer existingCustomer = getCustomerByKeycloakId(keycloakId);
        
        if (customerData.getFirstName() != null) {
            existingCustomer.setFirstName(customerData.getFirstName());
        }
        if (customerData.getLastName() != null) {
            existingCustomer.setLastName(customerData.getLastName());
        }
        if (customerData.getPhone() != null) {
            existingCustomer.setPhone(customerData.getPhone());
        }
        if (customerData.getDateOfBirth() != null) {
            existingCustomer.setDateOfBirth(customerData.getDateOfBirth());
        }
        if (customerData.getGender() != null) {
            existingCustomer.setGender(customerData.getGender());
        }
        if (customerData.getProfilePictureUrl() != null) {
            existingCustomer.setProfilePictureUrl(customerData.getProfilePictureUrl());
        }
        if (customerData.getNewsletterSubscribed() != null) {
            existingCustomer.setNewsletterSubscribed(customerData.getNewsletterSubscribed());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("✅ Customer updated: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    @Transactional
    public void updateLastLogin(String keycloakId) {
        Customer customer = getCustomerByKeycloakId(keycloakId);
        customer.setLastLogin(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Transactional
    @CacheEvict(value = "customers", key = "#keycloakId")
    public void updateOrderStats(String keycloakId, BigDecimal orderAmount) {
        log.info("📊 Updating order stats for customer: {}", keycloakId);
        Customer customer = getCustomerByKeycloakId(keycloakId);
        customer.updateOrderStats(orderAmount);
        customerRepository.save(customer);
    }

    // === Gestion des Adresses ===

    public List<CustomerAddress> getCustomerAddresses(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    public CustomerAddress getDefaultAddress(Long customerId) {
        return addressRepository.findByCustomerIdAndIsDefaultTrue(customerId)
                .orElse(null);
    }

    @Transactional
    public CustomerAddress addAddress(Long customerId, CustomerAddress address) {
        log.info("📍 Adding address for customer: {}", customerId);
        Customer customer = getCustomerById(customerId);
        
        // Si c'est la première adresse ou marquée par défaut
        List<CustomerAddress> existingAddresses = addressRepository.findByCustomerId(customerId);
        if (existingAddresses.isEmpty() || address.getIsDefault()) {
            // Retirer le défaut des autres adresses
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
            address.setIsDefault(true);
        }

        customer.addAddress(address);
        CustomerAddress savedAddress = addressRepository.save(address);
        log.info("✅ Address added: {}", savedAddress.getId());
        return savedAddress;
    }

    @Transactional
    public void setDefaultAddress(Long customerId, Long addressId) {
        log.info("📍 Setting default address {} for customer {}", addressId, customerId);
        
        // Retirer le défaut de toutes les adresses
        List<CustomerAddress> addresses = addressRepository.findByCustomerId(customerId);
        addresses.forEach(addr -> {
            addr.setIsDefault(addr.getId().equals(addressId));
            addressRepository.save(addr);
        });
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        log.info("🗑️ Deleting address: {}", addressId);
        addressRepository.deleteById(addressId);
    }
}
