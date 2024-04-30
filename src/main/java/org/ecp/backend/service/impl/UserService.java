package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.entity.Client;
import org.ecp.backend.entity.Employee;
import org.ecp.backend.entity.User;
import org.ecp.backend.repository.AdminRepository;
import org.ecp.backend.repository.ClientRepository;
import org.ecp.backend.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AdminRepository adminRepos;
    private final ClientRepository clientRepo;
    private final EmployeeRepository employeeRepo;

    public User loadUserByUsername(String username) {
        Admin admin = adminRepos.findByUsername(username).orElse(null);
        Employee employee = employeeRepo.findByUsername(username).orElse(null);
        Client client = clientRepo.findByUsername(username).orElse(null);
        User user;
        if (admin != null) {
            user = User.builder()
                    .id(admin.getId())
                    .username(admin.getUsername())
                    .password(admin.getPassword())
                    .email(admin.getEmail())
                    .role(admin.getRole())
                    .fullName(admin.getFullName())
                    .address(admin.getAddress())
                    .avatar(admin.getAvatar())
                    .phone(admin.getPhone())
                    .build();
        } else if (employee != null) {
            user = User.builder()
                    .id(employee.getId())
                    .username(employee.getUsername())
                    .password(employee.getPassword())
                    .email(employee.getEmail())
                    .role(employee.getRole())
                    .fullName(employee.getFullName())
                    .address(employee.getAddress())
                    .avatar(employee.getAvatar())
                    .phone(employee.getPhone())
                    .build();
        } else if (client != null) {
            user = User.builder()
                    .id(client.getId())
                    .username(client.getUsername())
                    .password(client.getPassword())
                    .email(client.getEmail())
                    .role(client.getRole())
                    .fullName(client.getFullName())
                    .address(client.getAddress())
                    .avatar(client.getAvatar())
                    .phone(client.getPhone())
                    .build();
        } else {
            return null;
        }
        return user;
    }
}
