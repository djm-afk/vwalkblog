package com.example.vwalkblog.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
*   自定义的元数据对象处理器
* */
@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    /*
    *   插入自动填充
    * */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充【insert】");
        log.info("【insert】当前线程--->"+Thread.currentThread().getId());
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContextThreadLocal.getCurrentId());
        log.info(BaseContextThreadLocal.getCurrentId().toString());
//        log.info(metaObject.toString());
    }
    /*
     *   更新自动填充
     * */
    @Override
    public void updateFill(MetaObject metaObject) {
//        log.info("公共字段填充【update】");
//        log.info("【update】当前线程--->"+Thread.currentThread().getId());
        metaObject.setValue("updateTime",LocalDateTime.now());
//        log.info(metaObject.toString());
    }
}
