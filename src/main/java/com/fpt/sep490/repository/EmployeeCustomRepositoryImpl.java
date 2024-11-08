package com.fpt.sep490.repository;

import com.fpt.sep490.Enum.EmployeeRole;
import com.fpt.sep490.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {
    private final EntityManager entityManager;

    @Autowired
    public EmployeeCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Employee> getEmployees(int month, int year, String role) {
        TypedQuery<Employee> query = entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "JOIN e.dayActives d " +
                        "WHERE e.employeeRole = :role " +
                        "AND FUNCTION('MONTH',d.dayActive) = :month " +
                        "AND FUNCTION('YEAR',d.dayActive) = :year"
                        , Employee.class);
        query.setParameter("role", EmployeeRole.valueOf(role));
        query.setParameter("month", month);
        query.setParameter("year", year);
        return query.getResultList();
    }
}
