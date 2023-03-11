package com.example.vwalkblog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.Blog;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.BlogService;
import com.example.vwalkblog.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
class VWalkBlogApplicationTests {

    @Test
    void contextLoads() {
        BlogDto blogDto = new BlogDto();
        blogDto.setTitle("123");
        blogDto.setTextPart("123");
        blogDto.setDescription("123");
        blogDto.setTitle("123");
    }

    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BlogCategoryService blogCategoryService;

    @Test
    public Result<BlogDto> getBlogById(Long blogId){
        Blog blog = blogService.getById(blogId);
        BlogDto blogDto = new BlogDto();
        BeanUtils.copyProperties(blog,blogDto);
        LambdaQueryWrapper<BlogCategory> lqwbc = new LambdaQueryWrapper<>();
        lqwbc.select(BlogCategory::getCategoryId);
        lqwbc.eq(BlogCategory::getBlogId,blogId);
        Map<String, Object> map = blogCategoryService.getMap(lqwbc);
        System.out.println(map);
//        List<BlogCategory> blogCategories = blogCategoryService.list(lqwbc);
//
//        blogDto.setCategories(blogCategories);
//        blogDto.setCategoryName();
        return Result.success(blogDto);
    }

    @Transactional
    public boolean saveWithType(BlogDto blogDto) {
        boolean saveBlog = blogService.save(blogDto);
        Long blogDtoId = blogDto.getId();
        List<BlogCategory> categories = blogDto.getCategories();
        categories.stream().forEach(item -> {
            item.setBlogId(blogDtoId);
        });
        boolean saveCategoryBatch = blogCategoryService.saveBatch(categories);
        return saveBlog && saveCategoryBatch;
    }

}
