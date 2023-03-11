package com.example.vwalkblog.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * blog
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * blog标题
     */
    private String title;

    /**
     * blog正文
     */
    @TableField(value = "text_part")
    private String textPart;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 封面图片
     */
    private String cover;

    /**
     * 图片
     */
    private String image;

    /**
     * 创建时间
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
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}