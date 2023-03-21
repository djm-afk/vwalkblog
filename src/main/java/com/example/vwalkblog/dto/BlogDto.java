package com.example.vwalkblog.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.vwalkblog.pojo.Blog;
import com.example.vwalkblog.pojo.Category;
import com.example.vwalkblog.pojo.Users;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class BlogDto extends Blog{

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

    private List<Category> categories = new ArrayList<>();
    private Users createuserdetail;
    private String[] images;
}
