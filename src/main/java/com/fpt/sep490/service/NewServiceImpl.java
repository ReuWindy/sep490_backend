package com.fpt.sep490.service;

import com.fpt.sep490.dto.NewDto;
import com.fpt.sep490.model.News;
import com.fpt.sep490.repository.NewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NewServiceImpl implements NewService {
    private final NewRepository newRepository;

    public NewServiceImpl(NewRepository newRepository) {
        this.newRepository = newRepository;
    }

    @Override
    public News createNew(NewDto newDto) {
        News newNew = new News();
        newNew.setName(newDto.getName());
        newNew.setDescription(newDto.getDescription());
        newNew.setCreateAt(new Date());
        newNew.setImage(newDto.getImage());
        newNew.setType(newDto.getType());
        newNew.setContent(newDto.getContent());
        newNew.setStatus(true);
        newRepository.save(newNew);
        return newNew;
    }

    @Override
    public News updateNew(int id, NewDto newDto) {
        News existedNew = getNewById(id);
        existedNew.setName(newDto.getName());
        existedNew.setDescription(newDto.getDescription());
        existedNew.setImage(newDto.getImage());
        existedNew.setType(newDto.getType());
        existedNew.setContent(newDto.getContent());
        existedNew.setStatus(true);
        existedNew.setUpdateAt(new Date());
        return newRepository.save(existedNew);
    }

    @Override
    public News getNewById(int id) {
        return newRepository.findById(id).orElse(null);
    }

    @Override
    public List<News> getAllNews() {
        return newRepository.findAll();
    }

    @Override
    public Page<News> getNewsByFilter(String name, String type, String username, int pageNumber, int pageSize ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
            Specification<News> specification = NewSpecification.hasNameOrTypeOrCreatedBy(name, type, username);
            return newRepository.findAll(specification, pageable);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public void disableNews(int id) {
        News newToDisable = getNewById(id);
        newToDisable.setStatus(false);
        newToDisable.setUpdateAt(new Date());
        newRepository.save(newToDisable);
    }

    @Override
    public void enableNews(int id) {
        News newToDisable = getNewById(id);
        newToDisable.setStatus(true);
        newToDisable.setUpdateAt(new Date());
        newRepository.save(newToDisable);
    }
}
