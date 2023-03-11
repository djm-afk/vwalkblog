package com.example.vwalkblog.common;

/*
*   基于ThreadLocal（线程隔离）封装工具类，用户保存和获取当前登录用户的id
*   在LoginInterceptor --> employeeController --> MyMetaObjecthandler中使用，
*   一次请求一个线程，一次请求执行以上三个操作时是同一个线程，可通过 此类 来传递消息
* */

public class BaseContextThreadLocal {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
