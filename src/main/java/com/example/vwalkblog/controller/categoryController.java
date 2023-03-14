package com.example.vwalkblog.controller;

import com.example.vwalkblog.controller.exceptionC.ex.CategoryDelEx;
import com.example.vwalkblog.pojo.Category;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/category")
@ResponseBody
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class categoryController {
    @Autowired
    private CategoryService cs;

    @PostMapping()
    public Result<String> addCategory(@RequestBody Category category){
        System.out.println(category);
        boolean save = cs.save(category);
        return save ? Result.success("新增分类成功") : Result.error("新增分类失败");
    }

    @GetMapping()
    public Result<List<Category>> getCategory(){
        List<Category> list = cs.list();
        return Result.success(list);
    }

    @DeleteMapping("/{categoryId}")
    public Result<String> deleteCategory(@PathVariable Long categoryId){
        if (Objects.isNull(cs.getById(categoryId))){
            throw new CategoryDelEx("此分类不存在,无法删除");
        }
        boolean remove = cs.removeById(categoryId);
        return remove ? Result.success("删除分类成功") : Result.error("删除分类失败");
    }
}
