package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.dto.CommentsDto;
import com.example.vwalkblog.pojo.Comments;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.CommentsService;
import com.example.vwalkblog.mapper.CommentsMapper;
import com.example.vwalkblog.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 32580
* @description 针对表【comments(评论表)】的数据库操作Service实现
* @createDate 2023-03-09 15:17:39
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService{

    @Autowired
    private UserService userService;
    @Override
    public Result<List<CommentsDto>> getListByBlogId(Long blogId) {
        LambdaQueryWrapper<Comments> lqwco = new LambdaQueryWrapper<>();
        lqwco.eq(Comments::getBlogId,blogId);

        List<Comments> comments = this.list(lqwco);
        List<CommentsDto> collect = comments.stream().map(item -> {
            CommentsDto commentsDto = new CommentsDto();
            BeanUtils.copyProperties(item, commentsDto);
            Long userId = item.getCreateUser();
            commentsDto.setUser(userService.getById(userId));
            return commentsDto;
        }).collect(Collectors.toList());

        return Result.success(collect);
    }
}




