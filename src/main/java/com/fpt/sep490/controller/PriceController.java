package com.fpt.sep490.controller;

import com.fpt.sep490.dto.CustomerPriceDto;
import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.dto.ProductPriceRequestDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.Price;
import com.fpt.sep490.model.ProductPrice;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.PriceService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {
    private final PriceService priceService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public PriceController(PriceService priceService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.priceService = priceService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPrice() {
        List<Price> prices = priceService.findAllPrices();
        if (!prices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(prices);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());

    }

    @GetMapping("/admin/prices")
    public ResponseEntity<PagedModel<EntityModel<Price>>> getAdminPricePage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Price> pagedResourcesAssembler
    ) {
        Page<Price> contractPage = priceService.getPriceByFilter(name, pageNumber, pageSize);
        PagedModel<EntityModel<Price>> pagedModel = pagedResourcesAssembler.toModel(contractPage);
        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/admin/AddPrice")
    public ResponseEntity<?> AddPrice(HttpServletRequest rq, @RequestBody PriceRequestDto request) {
        try{
            Price createdPrice = priceService.AddPrice(request);
            String token = jwtTokenManager.resolveTokenFromCookie(rq);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ADD_PRICE", "Giá " + request.getName() + "đã được tạo bởi người dùng:" + username);
            messagingTemplate.convertAndSend("/topic/prices", "Giá " + request.getName() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPrice);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Add Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/UpdatePrice")
    public ResponseEntity<?> UpdatePrice(HttpServletRequest rq, @RequestBody PriceRequestDto request) {
        try{
            Price updatedPrice = priceService.UpdatePrice(request);
            String token = jwtTokenManager.resolveTokenFromCookie(rq);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_PRICE", "Cập nhật giá " + request.getName() + " bởi " + username);
            messagingTemplate.convertAndSend("/topic/prices", "Cập nhật giá " + request.getName() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedPrice);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/UpdateCustomerPrice")
    public ResponseEntity<?> UpdateCustomerPrice(HttpServletRequest request, @Valid @RequestBody CustomerPriceDto customerPriceDto) {
        try {
            List<Customer> updatedPriceCustomers = priceService.updateCustomerPrice(customerPriceDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_CUSTOMER_PRICE", "Cập nhật Bảng giá cho khách hàng" + " bởi " + username);
            messagingTemplate.convertAndSend("/topic/prices", "Bảng giá cho khách hàng" + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedPriceCustomers);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/UpdateProductPrice")
    public ResponseEntity<?> UpdateProductPrice(HttpServletRequest request, @Valid @RequestBody ProductPriceRequestDto productPriceDto) {
        try{
            List<ProductPrice> updatedProductPrice = priceService.updateProductPrice(productPriceDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_PRICE_ADMIN", "Cập nhật Bảng giá cho sản phẩm" + " bởi " + username);
            messagingTemplate.convertAndSend("/topic/prices", "Bảng giá cho sản phẩm " + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProductPrice);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/admin/DeletePrice/{priceId}")
    public ResponseEntity<?> DeletePrice(HttpServletRequest request, @PathVariable long priceId) {
        try {
            priceService.deletePrice(priceId);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DELETE_PRICE", "Xoá Bảng giá: " + priceId + " by " + username);
            messagingTemplate.convertAndSend("/topic/prices", "Bảng giá " + priceId + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body("Xóa giá thành công!");
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy giá phù hợp!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xảy ra lỗi trong quá trình xóa giá !");
        }
    }
}