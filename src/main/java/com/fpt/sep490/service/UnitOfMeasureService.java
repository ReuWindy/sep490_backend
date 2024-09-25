package com.fpt.sep490.service;

import com.fpt.sep490.dto.UnitOfMeasureDto;
import com.fpt.sep490.model.UnitOfMeasure;

import java.util.List;

public interface UnitOfMeasureService {
    List<UnitOfMeasureDto> getAllUnitOfMeasure();
    UnitOfMeasureDto createUnitOfMeasure(UnitOfMeasureDto unitOfMeasureDto);
    UnitOfMeasureDto getUnitOfMeasureById(long id);
    UnitOfMeasureDto updateUnitOfMeasure(UnitOfMeasureDto unitOfMeasureDto);
    void deleteUnitOfMeasure(long id);
}
