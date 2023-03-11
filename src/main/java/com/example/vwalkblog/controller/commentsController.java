package com.example.vwalkblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vwalkblog.pojo.Comments;
import com.example.vwalkblog.pojo.Users;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/comments")
@ResponseBody
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class commentsController {

    @Autowired
    private CommentsService cs;

    // 新增comment
    @PostMapping()
    public Result<String> addComment(@RequestBody Comments comment){
        boolean save = cs.save(comment);
        return save ? Result.success("评论成功") : Result.error("评论失败");
    }
    // 查看comments
    @GetMapping("/{blogId}")
    public Result<List<Comments>> getComments(@PathVariable Long blogId){
        LambdaQueryWrapper<Comments> lqwco = new LambdaQueryWrapper<>();
        lqwco.eq(Comments::getBlogId,blogId);
        List<Comments> comments = cs.list(lqwco);
        return Result.success(comments);
    }
    // 删除comment
    @DeleteMapping()
    public Result<String> deleteComment(HttpSession session, @RequestBody Comments comment){
        Long createUser = comment.getCreateUser();
        Users user = (Users) session.getAttribute("user");
        if (createUser.equals(user.getId())){
            boolean delete = cs.removeById(comment);
            return delete ? Result.success("删除成功") : Result.error("删除失败");
        }
        return Result.error("删除失败");
    }
}
