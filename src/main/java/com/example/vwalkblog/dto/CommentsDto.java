package com.example.vwalkblog.dto;

import com.example.vwalkblog.pojo.Comments;
import com.example.vwalkblog.pojo.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentsDto extends Comments {
    private Users user;
}
