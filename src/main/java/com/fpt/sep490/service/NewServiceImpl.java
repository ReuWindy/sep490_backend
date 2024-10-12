package com.fpt.sep490.service;

import com.fpt.sep490.dto.NewDto;
import com.fpt.sep490.model.News;
import com.fpt.sep490.repository.NewRepository;
import org.springframework.data.domain.Page;
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
    public News updateNew(News news) {
        return null;
    }

    @Override
    public List<News> getAllNews() {
        return newRepository.findAll();
    }

    @Override
    public Page<News> getNewsByFilter() {
        return null;
    }
}
