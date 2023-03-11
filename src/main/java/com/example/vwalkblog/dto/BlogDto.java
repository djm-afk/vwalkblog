package com.example.vwalkblog.dto;

import com.example.vwalkblog.pojo.Blog;
import com.example.vwalkblog.pojo.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlogDto extends Blog {
    private List<Category> categories = new ArrayList<>();
//    private List<Comments> comments = new ArrayList<>();
}
