package com.fpt.sep490.service;

import com.fpt.sep490.dto.EmployeeSalaryDto;
import com.fpt.sep490.dto.ExpenseReportDto;
import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.repository.DayActiveRepository;
import com.fpt.sep490.repository.EmployeeRepository;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
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
    private final WarehouseReceiptRepository warehouseReceiptRepository;

    public ExpenseVoucherServiceImpl(ExpenseVoucherRepository expenseVoucherRepository, DayActiveRepository dayActiveRepository, EmployeeRepository employeeRepository, WarehouseReceiptRepository warehouseReceiptRepository) {
        this.expenseVoucherRepository = expenseVoucherRepository;
        this.dayActiveRepository = dayActiveRepository;
        this.employeeRepository = employeeRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
    }

    @Override
    public Page<ExpenseVoucherDto> getExpenseVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize, String expenseCode) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<ExpenseVoucher> specification = ExpenseVoucherSpecification.isExpenseDateBetween(startDate, endDate, expenseCode);

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
        if (expenseVoucherDto.getTotalAmount() <= 0) {
            throw new RuntimeException("Không thể xuất phiếu chi có giá trị bằng 0");
        }
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

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

        List<DayActive> filteredDayActives = employee.getDayActives().stream()
                .filter(dayActive -> {
                    LocalDate localDate = dayActive.getDayActive().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !dayActive.isSpend() && YearMonth.from(localDate).equals(currentMonth);
                })
                .toList();

        if (filteredDayActives.isEmpty()) {
            throw new RuntimeException("Nhân viên đã được thanh toán toàn bộ lương");
        }

        if (totalAmount <= 0) {
            throw new RuntimeException("Không thể xuất phiếu chi có giá trị bằng 0");
        }
        Optional<LocalDate> minDateOpt = filteredDayActives.stream()
                .map(dayActive -> dayActive.getDayActive().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .min(Comparator.naturalOrder());
        Optional<LocalDate> maxDateOpt = filteredDayActives.stream()
                .map(dayActive -> dayActive.getDayActive().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .max(Comparator.naturalOrder());

        ExpenseVoucher newExpenseVoucher = new ExpenseVoucher();
        newExpenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        newExpenseVoucher.setExpenseDate(new Date());
        newExpenseVoucher.setTotalAmount(totalAmount);
        if (minDateOpt.isPresent()) {
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

        ExpenseVoucher savedExpenseVoucher = expenseVoucherRepository.save(newExpenseVoucher);

        filteredDayActives.forEach(dayActive -> {
            dayActive.setSpend(true);
            dayActiveRepository.save(dayActive);
        });

        return savedExpenseVoucher;
    }

    @Override
    public ExpenseVoucher createSupplierExpense(Long id) {
        WarehouseReceipt warehouseReceipt = warehouseReceiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập kho"));

        if (warehouseReceipt.getBatch().getBatchProducts().stream()
                .anyMatch(batchProduct -> batchProduct.isAdded() == false)) {
            throw new RuntimeException("Vui lòng xác nhận toàn bộ");
        }

        if (Boolean.TRUE.equals(warehouseReceipt.getIsPay())){
            throw new RuntimeException("Lô hàng này đã được thanh toán trước đó rồi");
        }

        double totalValue = warehouseReceipt.getBatch().getBatchProducts().stream()
                .filter(batchProduct -> batchProduct.isAdded() == true)
                .mapToDouble(batchProduct ->
                        batchProduct.getQuantity() *
                                batchProduct.getWeightPerUnit() *
                                batchProduct.getPrice()
                ).sum();

        ExpenseVoucher expenseVoucher = new ExpenseVoucher();
        expenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        expenseVoucher.setExpenseDate(new Date());
        expenseVoucher.setDeleted(false);
        expenseVoucher.setTotalAmount(totalValue);
        expenseVoucher.setType("Thanh toán tiền nhập hàng");
        expenseVoucher.setNote("Thanh toán tiền nhập lô hàng " + warehouseReceipt.getBatch().getBatchCode());

        warehouseReceipt.setIsPay(true);
        warehouseReceiptRepository.save(warehouseReceipt);
        return expenseVoucherRepository.save(expenseVoucher);
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
            return expenseVoucherRepository.save(expenseVoucher);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật phiếu chi");
        }
    }

    @Override
    public ExpenseVoucher deleteExpense(Long id) {
        ExpenseVoucher expenseVoucher = expenseVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi"));

        if (expenseVoucher.isDeleted()) {
            throw new RuntimeException("Phiếu chi không tồn tại!");
        }

        Date currentDate = new Date();
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
        if (expenseVoucher.getExpenseDate().before(new Date(currentDate.getTime() - threeDaysInMillis))) {
            throw new RuntimeException("Đã quá hạn xóa phiếu chi!");
        }
        expenseVoucher.setDeleted(true);
        try {
            return expenseVoucherRepository.save(expenseVoucher);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa phiếu chi");
        }
    }

    @Override
    public List<ExpenseReportDto> getExpenseReport(Date date, String type) {
        List<Object[]> results = switch (type.toLowerCase()) {
            case "day" -> expenseVoucherRepository.findDailyExpenseByMonth(date);
            case "week" -> expenseVoucherRepository.findDailyExpenseByWeek(date);
            case "month" -> expenseVoucherRepository.findMonthlyExpenseByYear(date);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

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
