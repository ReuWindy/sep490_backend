package com.fpt.sep490.service;

import com.fpt.sep490.dto.NewDto;
import com.fpt.sep490.model.News;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewService {
    News createNew(NewDto newDto);

    News updateNew(int id, NewDto newDto);

    News getNewById(int id);

    List<News> getAllNews();

    Page<News> getNewsByFilter(String name, String type, String username, Boolean status, int pageNumber, int pageSize);

    News disableNews(int id);

    News enableNews(int id);
}
