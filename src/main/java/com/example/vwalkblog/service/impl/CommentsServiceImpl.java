package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.pojo.Comments;
import com.example.vwalkblog.service.CommentsService;
import com.example.vwalkblog.mapper.CommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author 32580
* @description 针对表【comments(评论表)】的数据库操作Service实现
* @createDate 2023-03-09 15:17:39
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService{

}




