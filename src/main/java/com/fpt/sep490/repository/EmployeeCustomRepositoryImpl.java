package com.fpt.sep490.repository;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.exceptions.ApiRequestException;
import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {
    private final EntityManager entityManager;

    @Autowired
    public EmployeeCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Employee getEmployeeById(long id) {
        Employee employee = entityManager.find(Employee.class, id);
        if (employee == null) {
            throw new ApiRequestException("Employee not found");
        }
        return employee;
    }

    @Override
    public List<Employee> getEmployees(String role) {
        TypedQuery<Employee> query = entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "JOIN e.role r " +
                        "JOIN r.salaryDetail s " +
                        "WHERE s.salaryType = :role "
                        , Employee.class);
        query.setParameter("role", SalaryType.valueOf(role));
        return query.getResultList();
    }

    @Override
    public void createActiveDate(long id, Date date, int mass, String note) {
        Employee employee = entityManager.find(Employee.class, id);
        if (employee == null) {
            throw new ApiRequestException("Employee not found");
        }
        DayActive dayActive = DayActive.builder()
                .dayActive(date)
                .mass(mass)
                .note(note)
                .employee(employee)
                .build();
        employee.getDayActives().add(dayActive);
        entityManager.persist(employee);
    }

    @Override
    public void deleteActiveDate(long id, Date date) {
        DayActive dayActive = getDayActive(id, date);
        entityManager.remove(dayActive);
    }

    @Override
    public Employee updateActiveDate(long id, Date date, int mass, String note) {
        DayActive dayActive = getDayActive(id, date);
        dayActive.setMass(mass);
        dayActive.setNote(note);
        entityManager.persist(dayActive);
        return entityManager.find(Employee.class, id);
    }

    @Override
    public List<DayActive> getDayActiveByEmployeeId(long id, int month, int year) {
        TypedQuery<DayActive> query = entityManager.createQuery("SELECT d FROM DayActive d " +
                "WHERE d.employee.id = :id and " +
                "MONTH(d.dayActive) = :month and " +
                "YEAR(d.dayActive) = :year", DayActive.class);
        query.setParameter("id", id);
        query.setParameter("month", month);
        query.setParameter("year", year);
        List<DayActive> dayActives = query.getResultList();
        if (dayActives.isEmpty()) {
            return List.of();
        }
        return dayActives;
    }

    @Override
    public List<Employee> getEmployeesByRole(String role) {
        TypedQuery<Employee> query = entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.role.salaryDetail.salaryType = :role", Employee.class);
        query.setParameter("role", SalaryType.valueOf(role));
        List<Employee> employees = query.getResultList();
        if (employees.isEmpty()) {
            return List.of();
        }
        return employees;
    }

    private DayActive getDayActive(long id, Date date) {
        try {
            TypedQuery<DayActive> query = entityManager.createQuery("SELECT d FROM DayActive d " +
                            "JOIN d.employee e " +
                            "WHERE d.dayActive = :date " +
                            "AND e.id = :employeeId",
                    DayActive.class);
            query.setParameter("employeeId", id);
            query.setParameter("date", date);
            DayActive dayActive = query.getSingleResult();
            if (dayActive == null) {
                throw new ApiRequestException("Day active not found");
            }
            return dayActive;
        } catch (NoResultException e) {
            throw new RuntimeException("Day active not found");
        }
    }
}
