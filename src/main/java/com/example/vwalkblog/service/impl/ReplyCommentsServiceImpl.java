package com.example.vwalkblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.service.ReplyCommentsService;
import com.example.vwalkblog.pojo.ReplyComments;
import com.example.vwalkblog.mapper.ReplyCommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author 32580
* @description 针对表【reply_comments(回复评论表)】的数据库操作Service实现
* @createDate 2023-03-09 15:17:39
*/
@Service
public class ReplyCommentsServiceImpl extends ServiceImpl<ReplyCommentsMapper, ReplyComments>
    implements ReplyCommentsService {

}




