package com.example.vwalkblog.service;

import com.example.vwalkblog.dto.CommentsDto;
import com.example.vwalkblog.pojo.Comments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vwalkblog.respR.Result;

import java.util.List;

/**
* @author 32580
* @description 针对表【comments(评论表)】的数据库操作Service
* @createDate 2023-03-09 15:17:39
*/
public interface CommentsService extends IService<Comments> {

    Result<List<CommentsDto>> getListByBlogId(Long blogId);
}
