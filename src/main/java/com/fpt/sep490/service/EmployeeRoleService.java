package com.fpt.sep490.service;

import com.fpt.sep490.model.EmployeeRole;

import java.util.List;

public interface EmployeeRoleService {

    List<EmployeeRole> getAllEmployeeRole();
    EmployeeRole getEmployeeRoleById(int id);
    EmployeeRole createEmployeeRole(EmployeeRole employeeRole);
    EmployeeRole updateEmployeeRole(EmployeeRole employeeRole);
    EmployeeRole deleteEmployeeRole(int id);

}
