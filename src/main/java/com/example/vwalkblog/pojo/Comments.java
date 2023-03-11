package com.example.vwalkblog.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName comments
 */
@TableName(value ="comments")
@Data
public class Comments implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 所属blog
     */
    private Long blogId;

    /**
     * 评论正文
     */
    private String comment;

    /**
     * 创建时间/评论时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_user",fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 是否删除
     */
    @TableField(select = false)
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}