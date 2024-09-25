package com.fpt.sep490.controller;

import com.fpt.sep490.dto.UnitOfMeasureDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.service.UnitOfMeasureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/unitOfMeasures")
public class UnitOfMeasureController {
    private final UnitOfMeasureService unitOfMeasureService;

    public UnitOfMeasureController(UnitOfMeasureService unitOfMeasureService) {
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/")
    ResponseEntity<?> getAllUnitOfMeasures() {
        List<UnitOfMeasureDto> list = unitOfMeasureService.getAllUnitOfMeasure();
        if (!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getUnitOfMeasure(@PathVariable long id) {
        UnitOfMeasureDto unitOfMeasureDto = unitOfMeasureService.getUnitOfMeasureById(id);
        if (unitOfMeasureDto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(unitOfMeasureDto);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createUnitOfMeasures")
    public ResponseEntity<?> createUnitOfMeasure(@RequestBody UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasureDto unitOfMeasure = unitOfMeasureService.createUnitOfMeasure(unitOfMeasureDto);
        if (unitOfMeasure != null) {
            return ResponseEntity.status(HttpStatus.OK).body(unitOfMeasureDto);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("/updateUnitOfMeasures")
    public ResponseEntity<?> updateUnitOfMeasure(@RequestBody UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasureDto updatedUnitOfMeasure = unitOfMeasureService.updateUnitOfMeasure(unitOfMeasureDto);
        if (updatedUnitOfMeasure != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedUnitOfMeasure);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
