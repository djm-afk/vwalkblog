package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.pojo.BlogCategory;
import com.example.vwalkblog.service.BlogCategoryService;
import com.example.vwalkblog.mapper.BlogCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 32580
* @description 针对表【blog_category(blog对应的分类)】的数据库操作Service实现
* @createDate 2023-03-14 09:08:17
*/
@Service
public class BlogCategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory>
    implements BlogCategoryService{

}




