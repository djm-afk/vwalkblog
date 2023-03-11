package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.pojo.Category;
import com.example.vwalkblog.service.CategoryService;
import com.example.vwalkblog.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 32580
* @description 针对表【category(blog分类)】的数据库操作Service实现
* @createDate 2023-03-09 15:17:39
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




