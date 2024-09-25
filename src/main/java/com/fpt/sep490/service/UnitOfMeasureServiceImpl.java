package com.fpt.sep490.service;

import com.fpt.sep490.dto.UnitOfMeasureDto;
import com.fpt.sep490.model.UnitOfMeasure;
import com.fpt.sep490.repository.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public List<UnitOfMeasureDto> getAllUnitOfMeasure() {
        return unitOfMeasureRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public UnitOfMeasureDto createUnitOfMeasure(UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();

        unitOfMeasure.setUnitName(unitOfMeasureDto.getUnitName());
        unitOfMeasure.setConversionFactor(unitOfMeasureDto.getConversionFactor());

        unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);

        unitOfMeasureDto.setId(unitOfMeasure.getId());

        return unitOfMeasureDto;
    }

    @Override
    public UnitOfMeasureDto getUnitOfMeasureById(long id) {
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit of Measure not found"));
        return toDto(unitOfMeasure);
    }

    @Override
    public UnitOfMeasureDto updateUnitOfMeasure(UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(unitOfMeasureDto.getId())
                .orElseThrow(() -> new NotFoundException("Unit of Measure not found"));

        unitOfMeasure.setUnitName(unitOfMeasureDto.getUnitName());
        unitOfMeasure.setConversionFactor(unitOfMeasureDto.getConversionFactor());

        unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);

        return  toDto(unitOfMeasure);
    }

    @Override
    public void deleteUnitOfMeasure(long id) {
        if (!unitOfMeasureRepository.existsById(id)) {
            throw new NotFoundException("Unit of Measure not found");
        }
        unitOfMeasureRepository.deleteById(id);
    }

    private UnitOfMeasure toModel(UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId(unitOfMeasureDto.getId());
        unitOfMeasure.setUnitName(unitOfMeasureDto.getUnitName());
        unitOfMeasure.setConversionFactor(unitOfMeasureDto.getConversionFactor());
        return unitOfMeasure;
    }

    private UnitOfMeasureDto toDto(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto();
        unitOfMeasureDto.setId(unitOfMeasure.getId());
        unitOfMeasureDto.setUnitName(unitOfMeasure.getUnitName());
        unitOfMeasureDto.setConversionFactor(unitOfMeasure.getConversionFactor());
        return unitOfMeasureDto;
    }
}
