package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.Blog;
import com.example.vwalkblog.pojo.Category;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.BlogService;
import com.example.vwalkblog.mapper.BlogMapper;
import com.example.vwalkblog.service.CategoryService;
import com.example.vwalkblog.service.CommentsService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 32580
 * @description 针对表【blog(blog)】的数据库操作Service实现
 * @createDate 2023-03-09 15:17:39
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService{

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentsService commentsService;
    // 新增blog
    @Override
    @Transactional
    public boolean saveWithType(BlogDto blogDto) {
        boolean saveBlog = this.save(blogDto);
        Long blogDtoId = blogDto.getId();
        List<Category> categories = blogDto.getCategories();
        categories.stream().forEach(item -> {
            item.setBlogId(blogDtoId);
        });
        boolean saveCategoryBatch = categoryService.saveBatch(categories);
        return saveBlog && saveCategoryBatch;
    }

    // 根据blogId查询blog
    @Override
    public Result<BlogDto> getBlogById(Long blogId){
        Blog blog = this.getById(blogId);
        BlogDto blogDto = new BlogDto();
        BeanUtils.copyProperties(blog,blogDto);
        LambdaQueryWrapper<Category> lqwc = new LambdaQueryWrapper<>();
        lqwc.eq(Category::getBlogId,blogId);
        List<Category> categories = categoryService.list(lqwc);
        blogDto.setCategories(categories);
        return Result.success(blogDto);
    }

    // 查询blog列表
    @Override
    public Result<Page<BlogDto>> selectByPage(@PathParam("name") String name, @PathParam("page") Integer page, @PathParam("pageSize") Integer pageSize){
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper();
        lqw.select(Blog::getId,Blog::getCreateUser,Blog::getTitle,Blog::getCover,Blog::getCreateTime,Blog::getDescription);
        lqw.like(!Strings.isEmpty(name),Blog::getTitle,name);
        lqw.orderByDesc(Blog::getCreateTime);
        Page page_ = this.page(new Page(page, pageSize), lqw);
        long pages = page_.getPages();
        if (pages < page){
            page_ = this.page(new Page(pages, pageSize), lqw);
        }
        Page<BlogDto> blogDtoPage = new Page<>();
        BeanUtils.copyProperties(page_,blogDtoPage);
        // 处理records
        List<Blog> records = page_.getRecords();
        LambdaQueryWrapper<Category> lqw_c = new LambdaQueryWrapper();
        ArrayList<BlogDto> blogDtos = new ArrayList<>();
        records.stream().forEach(item -> {
            BlogDto blogDto = new BlogDto();
            BeanUtils.copyProperties(item,blogDto);
            Long blogId = item.getId();
            lqw_c.clear();
            lqw_c.eq(Category::getBlogId,blogId);
            List<Category> categories = categoryService.list(lqw_c);
            blogDto.setCategories(categories);
            blogDtos.add(blogDto);
        });
        blogDtoPage.setRecords(blogDtos);
        return Result.success(blogDtoPage);
    }

    // 根据userId查询blog列表
    @Override
    public Result<List<BlogDto>> getBlogByUserId(Long userId){
        LambdaQueryWrapper<Blog> lqwb = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Category> lqwca = new LambdaQueryWrapper<>();
        lqwb.select(Blog::getId,Blog::getCreateUser,Blog::getTitle,Blog::getCover,Blog::getCreateTime,Blog::getDescription);
        lqwb.eq(Blog::getCreateUser,userId);
        List<Blog> list = this.list(lqwb);
        ArrayList<BlogDto> blogDtoList = (ArrayList<BlogDto>) list.stream().map(blog -> {
            Long blogId = blog.getId();
            BlogDto blogDto = new BlogDto();
            BeanUtils.copyProperties(blog,blogDto);
            lqwca.clear();
            lqwca.eq(Category::getBlogId,blogId);
            List<Category> categories = categoryService.list(lqwca);
            blogDto.setCategories(categories);
            return blogDto;
        }).collect(Collectors.toList());
        return Result.success(blogDtoList);
    }
}




