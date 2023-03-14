package com.example.vwalkblog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * blog对应的分类
 * @TableName blog_category
 */
@TableName(value ="blog_category")
@Data
public class BlogCategory implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * blog_id
     */
    private Long blogId;

    /**
     * category_id
     */
    private Long categoryId;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}