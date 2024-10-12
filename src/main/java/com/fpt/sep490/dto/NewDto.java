package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;
@Data
public class NewDto {
    private String name;
    private String image;
    private String type;
    private String description;
    private String content;
    private int userId;
}
