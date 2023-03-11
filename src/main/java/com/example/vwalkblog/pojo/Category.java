package com.example.vwalkblog.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * blog分类
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * blog_id
     */
    private Long blogId;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}