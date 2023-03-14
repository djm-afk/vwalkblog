package com.example.vwalkblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.BlogService;
import com.example.vwalkblog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/blog")
@ResponseBody
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class blogController {

    @Autowired
    private BlogService bs;
    @Autowired
    private CategoryService cs;

    // 查询blog列表
    @GetMapping("/page")
    public Result<Page<BlogDto>> selectByPage(@PathParam("name") String name, @PathParam("page") Integer page, @PathParam("pageSize") Integer pageSize){
        return bs.selectByPage(name, page, pageSize);
    }

    // 新增blog
    @PostMapping()
    @CacheEvict(value = {"blogCache","userBlogCache"},allEntries = true)
    public Result<String> save(@RequestBody BlogDto blogDto){
        boolean save = bs.saveWithType(blogDto);
        return save ? Result.success("保存成功！") : Result.error("保存失败！");
    }

    // blog_id查询blog
    @GetMapping("/{blogId}")
    @Cacheable(value = "blogCache",key = "#blogId")
    public Result<BlogDto> blogById(@PathVariable Long blogId){
        return bs.getBlogById(blogId);
    }

    // 删除blog
    @DeleteMapping()
    @CacheEvict(value = {"blogCache","userBlogCache"},allEntries = true)
    public Result<String> save(@PathParam("ids") Long[] ids){
        boolean delete = bs.removeBlog(ids);
        return delete ? Result.success("删除成功！") : Result.error("删除失败！");
    }

    // 根据userId查询blog
    @GetMapping("/byUserId/{userId}")
    @Cacheable(value = "userBlogCache",key = "#userId")
    public Result<List<BlogDto>> getBlogByUserId(@PathVariable Long userId){
        return bs.getBlogByUserId(userId);
    }
}
