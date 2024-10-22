package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BatchServiceImpl implements BatchService {
    private final BatchRepository batchRepository;

    private final UserService userService;

    public BatchServiceImpl(BatchRepository batchRepository, UserService userService) {
        this.batchRepository = batchRepository;

        this.userService = userService;
    }

    @Override
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    @Override
    public Batch getBatchById(int id) {
        Optional<Batch> batch = batchRepository.findById((long) id);
        return batch.orElse(null);
    }

    @Override
    public Batch createBatch() {
        Batch batch = new Batch();
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());
        batch.setImportDate(new Date());
        batch.setBatchStatus("OK");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);
        batchRepository.save(batch);
        return batch;
    }


    @Override
    public Batch updateBatch(Long batchId, BatchDto batchDto) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);

        return batchRepository.save(batch);
    }

    @Override
    public Batch getBatchByBatchCode(String code) {
        Optional<Batch> batch = Optional.ofNullable(batchRepository.findByBatchCode(code));
        return batch.orElse(null);
    }


    @Override
    public void deleteBatch(Long batchId) {
        Batch batch = getBatchById(Math.toIntExact(batchId));
        batchRepository.delete(batch);
    }
}