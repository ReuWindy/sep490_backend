package com.fpt.sep490.service;

import com.fpt.sep490.dto.EmployeeSalaryDto;
import com.fpt.sep490.dto.ExpenseReportDto;
import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.repository.DayActiveRepository;
import com.fpt.sep490.repository.EmployeeRepository;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.utils.RandomExpenseCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseVoucherServiceImpl implements ExpenseVoucherService {

    private final ExpenseVoucherRepository expenseVoucherRepository;
    private final DayActiveRepository dayActiveRepository;
    private final EmployeeRepository employeeRepository;

    public ExpenseVoucherServiceImpl(ExpenseVoucherRepository expenseVoucherRepository, DayActiveRepository dayActiveRepository, EmployeeRepository employeeRepository) {
        this.expenseVoucherRepository = expenseVoucherRepository;
        this.dayActiveRepository = dayActiveRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Page<ExpenseVoucherDto> getExpenseVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<ExpenseVoucher> specification = ExpenseVoucherSpecification.isExpenseDateBetween(startDate, endDate);

            Page<ExpenseVoucher> expenseVoucherPage = expenseVoucherRepository.findAll(specification, pageable);

            List<ExpenseVoucherDto> dtos = expenseVoucherPage.getContent().stream()
                    .map(ExpenseVoucherDto::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, expenseVoucherPage.getTotalElements());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ExpenseVoucher createExpense(ExpenseVoucherDto expenseVoucherDto) {
        ExpenseVoucher newExpenseVoucher = new ExpenseVoucher();
        newExpenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        newExpenseVoucher.setExpenseDate(new Date());
        newExpenseVoucher.setTotalAmount(expenseVoucherDto.getTotalAmount());
        newExpenseVoucher.setNote(expenseVoucherDto.getNote());
        newExpenseVoucher.setType(expenseVoucherDto.getType());
        return expenseVoucherRepository.save(newExpenseVoucher);
    }

    @Override
    public ExpenseVoucher createEmployeeExpense(List<EmployeeSalaryDto> employeeSalaryDtos) {
        double totalAmount = 0;
        Employee employee = null;
        String payDate = "ngày ";
        List<String> days = new ArrayList<>();
        String month = "";

        for (int i = 0; i < employeeSalaryDtos.size(); i++) {
            EmployeeSalaryDto es = employeeSalaryDtos.get(i);
            DayActive dayActive = dayActiveRepository.findById(es.getDayActiveId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày làm việc"));
            employee = dayActive.getEmployee();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dayActive.getDayActive());

            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            days.add(day);

            if (i == employeeSalaryDtos.size() - 1) {
                month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            }

            if (dayActive.isSpend()) {
                throw new RuntimeException("Phiếu chi ngày " + day + " tháng " + month + " đã được thanh toán trước đó");
            }

            if (employee == null) {
                throw new RuntimeException("Không tìm thấy nhân viên");
            }
            dayActive.setAmountPerMass(es.getAmountByTon());
            totalAmount += (dayActive.getMass() * es.getAmountByTon());
            dayActive.setSpend(true);
            dayActiveRepository.save(dayActive);
        }
        payDate += String.join(", ", days) + " tháng " + month;
        if (totalAmount <= 0) {
            throw new RuntimeException("Tổng tiền không hợp lệ");
        }
        ExpenseVoucher newExpenseVoucher = new ExpenseVoucher();
        newExpenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        newExpenseVoucher.setExpenseDate(new Date());
        newExpenseVoucher.setTotalAmount(totalAmount);
        newExpenseVoucher.setNote("Thanh toán lương " + payDate + " cho nhân viên " + employee.getFullName());
        newExpenseVoucher.setType("Thanh toán lương nhân viên");
        return expenseVoucherRepository.save(newExpenseVoucher);
    }

    @Override
    public ExpenseVoucher createEmployeeExpense(Long employeeId) {
        YearMonth currentMonth = YearMonth.now();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        double totalAmount = employee.getDayActives().stream()
                .filter(dayActive -> {
                    LocalDate localDate = dayActive.getDayActive().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !dayActive.isSpend() &&
                            YearMonth.from(localDate).equals(currentMonth);
                })
                .mapToDouble(dayActive -> employee.getRole().getSalaryDetail().getDailyWage())
                .sum();

        List<LocalDate> filteredDates = employee.getDayActives().stream()
                .filter(dayActive -> {
                    LocalDate localDate = dayActive.getDayActive().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    return !dayActive.isSpend() && YearMonth.from(localDate).equals(currentMonth);
                })
                .map(dayActive -> dayActive.getDayActive().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .toList();

        Optional<LocalDate> minDateOpt = filteredDates.stream().min(Comparator.naturalOrder());
        Optional<LocalDate> maxDateOpt = filteredDates.stream().max(Comparator.naturalOrder());

        ExpenseVoucher newExpenseVoucher = new ExpenseVoucher();
        newExpenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        newExpenseVoucher.setExpenseDate(new Date());
        newExpenseVoucher.setTotalAmount(totalAmount);
        if (minDateOpt.isPresent() && maxDateOpt.isPresent()) {
            LocalDate minDate = minDateOpt.get();
            LocalDate maxDate = maxDateOpt.get();

            String note = String.format("Thanh toán lương từ ngày %d đến ngày %d tháng %d cho nhân viên %s",
                    minDate.getDayOfMonth(),
                    maxDate.getDayOfMonth(),
                    currentMonth.getMonthValue(),
                    employee.getFullName()
            );

            newExpenseVoucher.setNote(note);
        }
        newExpenseVoucher.setType("Thanh toán lương nhân viên");
        return expenseVoucherRepository.save(newExpenseVoucher);
    }

    @Override
    public ExpenseVoucher updateExpense(ExpenseVoucherDto expenseVoucherDto) {
        ExpenseVoucher expenseVoucher = expenseVoucherRepository.findById(expenseVoucherDto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi"));

        if (expenseVoucher.isDeleted()) {
            throw new RuntimeException("Phiếu chi không tồn tại!");
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date datePlusThreeDays = calendar.getTime();

        if (expenseVoucher.getExpenseDate().after(datePlusThreeDays)) {
            throw new RuntimeException("Đã quá hạn sửa phiếu chi!");
        }
        expenseVoucher.setTotalAmount(expenseVoucherDto.getTotalAmount());
        expenseVoucher.setNote(expenseVoucherDto.getNote());
        expenseVoucher.setType(expenseVoucherDto.getType());
        try {
            ExpenseVoucher savedExpense = expenseVoucherRepository.save(expenseVoucher);
            return savedExpense;
        }catch (Exception e){
            throw  new RuntimeException("Lỗi khi cập nhật phiếu chi");
        }
    }

    @Override
    public ExpenseVoucher deleteExpense(ExpenseVoucherDto expenseVoucherDto, Long id) {
        ExpenseVoucher expenseVoucher = expenseVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi"));

        if (expenseVoucher.isDeleted()) {
            throw new RuntimeException("Phiếu chi không tồn tại!");
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date datePlusThreeDays = calendar.getTime();

        if (expenseVoucher.getExpenseDate().after(datePlusThreeDays)) {
            throw new RuntimeException("Đã quá hạn xóa phiếu chi!");
        }
        expenseVoucher.setDeleted(true);
        try{
            ExpenseVoucher savedExpense = expenseVoucherRepository.save(expenseVoucher);
            return savedExpense;
        }catch (Exception e){
            throw new RuntimeException("Lỗi khi xóa phiếu chi");
        }
    }

    @Override
    public List<ExpenseReportDto> getExpenseReport(Date date, String type) {
        List<Object[]> results;

        switch (type.toLowerCase()) {
            case "day":
                results = expenseVoucherRepository.findDailyExpenseByMonth(date);
                break;
            case "week":
                results = expenseVoucherRepository.findDailyExpenseByWeek(date);
                break;
            case "month":
                results = expenseVoucherRepository.findMonthlyExpenseByYear(date);
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Chuyển đổi kết quả thành DTO
        List<ExpenseReportDto> report = new ArrayList<>();
        for (Object[] result : results) {
            ExpenseReportDto dto = new ExpenseReportDto();
            dto.setPeriod(((Number) result[0]).intValue());
            dto.setTotalAmount((Double) result[1]);
            report.add(dto);
        }

        return report;
    }
}
