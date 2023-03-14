package com.example.vwalkblog.controller.exceptionC;

import com.example.vwalkblog.controller.exceptionC.ex.BlogDelEx;
import com.example.vwalkblog.controller.exceptionC.ex.CategoryDelEx;
import com.example.vwalkblog.respR.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/*
 *   处理 RestController.class 的异常
 * */
@RestControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class RestExceptionController {

    @ExceptionHandler(SQLException.class)
    public Result SqlExceptionHandler(SQLException ex){
        log.error(ex.getMessage());
        return Result.error("数据库异常");
    }

    // category删除业务异常
    @ExceptionHandler(BlogDelEx.class)
    public Result BlogDelExHandler(RuntimeException ex){
        return Result.error(ex.getMessage());
    }


    // category删除业务异常
    @ExceptionHandler(CategoryDelEx.class)
    public Result CategoryDelExHandler(RuntimeException ex){
        return Result.error(ex.getMessage());
    }

    //    @ExceptionHandler(Exception.class)
    public Result GlobalExceptionHandler(Exception ex){
        log.error(ex.getMessage());
        return Result.error("其他异常，请联系运维人员");
    }



}
