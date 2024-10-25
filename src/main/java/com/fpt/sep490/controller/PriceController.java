package com.fpt.sep490.controller;

import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.Price;
import com.fpt.sep490.service.PriceService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {
        private final PriceService priceService;

        public PriceController(PriceService priceService){
            this.priceService = priceService;
        }

        @GetMapping("/all")
        public ResponseEntity<?> getAllPrice(){
            List<Price> prices = priceService.findAllPrices();
            if(!prices.isEmpty()){
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
        ){
            Page<Price> contractPage = priceService.getPriceByFilter(name, pageNumber, pageSize);
            PagedModel<EntityModel<Price>> pagedModel = pagedResourcesAssembler.toModel(contractPage);
            return ResponseEntity.ok(pagedModel);
        }

        @PostMapping("/admin/AddPrice")
        public ResponseEntity<?> AddPrice(@RequestBody PriceRequestDto request){
            Price createdPrice = priceService.AddPrice(request);
            if (createdPrice != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createdPrice);
            }
            final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
}   
