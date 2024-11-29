package com.fpt.sep490.service;

import com.fpt.sep490.model.EmployeeRole;
import com.fpt.sep490.repository.EmployeeRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeRoleServiceImpl implements EmployeeRoleService {

    private final EmployeeRoleRepository employeeRoleRepository;

    public EmployeeRoleServiceImpl(EmployeeRoleRepository employeeRoleRepository) {
        this.employeeRoleRepository = employeeRoleRepository;
    }


    @Override
    public List<EmployeeRole> getAllEmployeeRole() {
        return employeeRoleRepository.findAll();
    }

    @Override
    public EmployeeRole getEmployeeRoleById(int id) {
        Optional<EmployeeRole> employeeRole = employeeRoleRepository.findById((long) id);
        return employeeRole.orElse(null);
    }

    @Override
    public EmployeeRole createEmployeeRole(EmployeeRole employeeRole) {
        EmployeeRole createdEmployeeRole = new EmployeeRole();
        createdEmployeeRole.setRoleName(employeeRole.getRoleName());
        employeeRoleRepository.save(createdEmployeeRole);
        return createdEmployeeRole;
    }

    @Override
    public EmployeeRole updateEmployeeRole(EmployeeRole employeeRole) {
        EmployeeRole existingEmployeeRole = employeeRoleRepository.findById(employeeRole.getId()).orElse(null);
        if (existingEmployeeRole != null) {
            existingEmployeeRole.setRoleName(employeeRole.getRoleName());
            employeeRoleRepository.save(existingEmployeeRole);
            return existingEmployeeRole;
        }
        return null;
    }

    @Override
    public EmployeeRole deleteEmployeeRole(int id) {
        return null;
    }
}
