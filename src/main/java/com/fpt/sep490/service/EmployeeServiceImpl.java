package com.fpt.sep490.service;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.dto.DailyEmployeePayrollResponseDTO;
import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.dto.EmployeeWithDayActiveDTO;
import com.fpt.sep490.dto.MonthlyEmployeePayrollResponseDTO;
import com.fpt.sep490.exceptions.ApiRequestException;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeCustomRepository employeeCustomRepository;
    private final UserRepository userRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeRoleRepository employeeRoleRepository, EmployeeCustomRepository employeeCustomRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.employeeCustomRepository = employeeCustomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        Optional<Employee> employee = employeeRepository.findById((long) id);
        return employee.orElse(null);
    }

    @Override
    public Page<Employee> getEmployeeByFilter(String employeeCode, String fullName, String phoneNumber, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Employee> specification = EmployeeSpecification.hasEmployeeCodeOrFullNameOrPhoneNumber(employeeCode, fullName, phoneNumber);
            return employeeRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Employee createEmployee(EmployeeDTO employeeDTO) {
        return null;
    }

    @Override
    public Employee updateEmployee(EmployeeDTO employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        if (existingEmployee == null) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }

        User existingPhone = userRepository.findUserByPhone(employee.getPhone());
        User existingEmail = userRepository.findUserByEmail(employee.getEmail());

        EmployeeRole newEmployeeRole = employeeRoleRepository.findById(employee.getEmployeeRoleId()).orElse(null);
        if (existingEmployee == null) {
            throw new RuntimeException("Không tìm thấy chức vụ");
        }
        if (existingEmail.getId() != employee.getId()){
            throw new RuntimeException("Đã có tài khoản được đăng ký bằng địa chỉ email này");
        }
        if (existingPhone.getId() != employee.getId()){
            throw new RuntimeException("Đã có tài khoản được đăng ký bằng số điện thoại này");
        }
        if (employee.getFullName().isBlank()) {
            throw new RuntimeException("Tên nhân viên không được bỏ trống");
        }
        if (employee.getEmail().isBlank()) {
            throw new RuntimeException("Địa chỉ email không được bỏ trống");
        }
        String email = employee.getEmail();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new RuntimeException("Địa chỉ email không hợp lệ");
        }
        if (employee.getPhone().isBlank()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }
        String phoneNumber = employee.getPhone();
        String phoneNumberRegex = "^(\\+84|0)[3-9]{1}[0-9]{8}$";

        Pattern phonePattern = Pattern.compile(phoneNumberRegex);
        if (!phonePattern.matcher(phoneNumber).matches()) {
            throw new RuntimeException("Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10 hoặc 11 chữ số");
        }
        existingEmployee.setFullName(employee.getFullName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setBankName(employee.getBankName());
        existingEmployee.setBankNumber(employee.getBankNumber());
        existingEmployee.setDob(employee.getDob());
        existingEmployee.setGender(employee.isGender());
        existingEmployee.setImage(employee.getImage());
        existingEmployee.setUpdateAt(new Date());
        Role role = existingEmployee.getRole();
        if (role != null) {
            role.setEmployeeRole(newEmployeeRole);
            SalaryDetail salaryDetail = role.getSalaryDetail();
            if (salaryDetail != null) {
                if (role.getEmployeeRole().getRoleName().equalsIgnoreCase("PORTER_EMPLOYEE")) {
                    salaryDetail.setDailyWage(0);
                } else {
                    salaryDetail.setDailyWage(employee.getDailyWage());
                }
            }
            role.setSalaryDetail(salaryDetail);
        }
        existingEmployee.setRole(role);
        employeeRepository.save(existingEmployee);
        return existingEmployee;
    }

    @Override
    public User deleteEmployee(Long id) {
        User employeeToDisable = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        employeeToDisable.setActive(false);
        userRepository.save(employeeToDisable);
        return employeeToDisable;
    }

    @Override
    public User enableEmployee(Long id) {
        User employeeToDisable = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        employeeToDisable.setActive(true);
        userRepository.save(employeeToDisable);
        return employeeToDisable;
    }

    @Override
    public List<EmployeeWithDayActiveDTO> getEmployees(String role) {
        if (!Objects.equals(role, SalaryType.DAILY.toString())
                && !Objects.equals(role, SalaryType.MONTHLY.toString())) {
            throw new ApiRequestException("Invalid role");
        }
        List<Employee> employees = employeeCustomRepository.getEmployees(role);
        return convertEmployeeToEmployeeWithDayActiveDTO(employees);
    }

    @Transactional
    @Override
    public Employee createDayActive(long id, String date, int mass, String note) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            employeeCustomRepository.createActiveDate(id, formattedDate, mass, note);
            return employeeCustomRepository.getEmployeeById(id);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Transactional
    @Override
    public void deleteDayActive(long id, String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            employeeCustomRepository.deleteActiveDate(id, formattedDate);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Transactional
    @Override
    public Employee updateDayActive(long id, String date, int mass, String note) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            return employeeCustomRepository.updateActiveDate(id, formattedDate, mass, note);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Override
    public List<DayActive> getDayActiveByEmployeeId(long id, int month, int year) {
        return employeeCustomRepository.getDayActiveByEmployeeId(id, month, year);
    }

    @Override
    public List<EmployeeWithDayActiveDTO> getEmployeesByRole(String role) {
        List<Employee> employees = employeeCustomRepository.getEmployeesByRole(role);
        return convertEmployeeToEmployeeWithDayActiveDTO(employees);
    }

    @Override
    public List<DailyEmployeePayrollResponseDTO> getDailyEmployeePayroll(int month, int year) {
        List<Employee> employees = employeeCustomRepository.getEmployeesByRole(SalaryType.DAILY.toString());
        return employees.stream().map(
                employee -> {
                    List<DayActive> dayActives = employeeCustomRepository.getDayActiveByEmployeeId(employee.getId(), month, year);
                    int dayWorked = dayActives.size();
                    double totalMass = dayActives.stream().mapToDouble(DayActive::getMass).sum();
                    return new DailyEmployeePayrollResponseDTO(
                            employee.getId(), employee.getPhone(), employee.getEmail(), employee.getAddress(),
                            employee.getFullName(), employee.getBankName(), employee.getBankNumber(), employee.getDob(),
                            employee.isGender(), employee.getImage(), employee.getRole().getEmployeeRole().getRoleName(), dayWorked, totalMass
                    );
                }
        ).toList();
    }

    @Override
    public List<MonthlyEmployeePayrollResponseDTO> getMonthlyEmployeePayroll(int month, int year) {
        List<Employee> employees = employeeCustomRepository.getEmployeesByRole(SalaryType.MONTHLY.toString());
        return employees.stream().map(
                employee -> {
                    List<DayActive> dayActives = employeeCustomRepository.getDayActiveByEmployeeId(employee.getId(), month, year);
                    int dayWorked = dayActives.size();
                    double totalSalary = employee.getRole().getSalaryDetail().getDailyWage() * dayWorked;
                    return new MonthlyEmployeePayrollResponseDTO(
                            employee.getId(), employee.getPhone(), employee.getEmail(), employee.getAddress(),
                            employee.getFullName(), employee.getBankName(), employee.getBankNumber(), employee.getDob(),
                            employee.isGender(), employee.getImage(), employee.getRole().getEmployeeRole().getRoleName(),
                            employee.getRole().getSalaryDetail().getDailyWage(), dayWorked, totalSalary
                    );
                }
        ).toList();
    }

    public List<EmployeeWithDayActiveDTO> convertEmployeeToEmployeeWithDayActiveDTO(List<Employee> employees) {
        if (employees.isEmpty()) {
            return List.of();
        }
        return employees.stream().map(
                employee -> new EmployeeWithDayActiveDTO(employee.getId(), employee.getPhone(), employee.getEmail(), employee.getAddress(),
                        employee.getFullName(), employee.getBankName(), employee.getBankNumber(), employee.getDob(),
                        employee.isGender(), employee.getImage(), employee.getRole().getEmployeeRole().getRoleName(), employee.getRole().getSalaryDetail().getSalaryType().toString(),
                        employee.getRole().getSalaryDetail().getDailyWage())
        ).toList();
    }
}
