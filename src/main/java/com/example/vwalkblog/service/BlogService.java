package com.example.vwalkblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vwalkblog.respR.Result;
import org.apache.ibatis.annotations.Select;

import javax.websocket.server.PathParam;
import java.util.List;

/**
* @author 32580
* @description 针对表【blog(blog)】的数据库操作Service
* @createDate 2023-03-09 15:17:39
*/
public interface BlogService extends IService<Blog> {
    boolean saveWithType(BlogDto dishDto);

    Result<BlogDto> getBlogById(Long blogId);

    Result<Page<BlogDto>> selectByPage(String name, Integer page, Integer pageSize);

    Result<List<BlogDto>> getBlogByUserId(Long blogId);
}
