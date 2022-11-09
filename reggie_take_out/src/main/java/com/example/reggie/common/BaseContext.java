package com.example.reggie.common;

//每个线程都独享一个此类中的ThreadLocal对象

/**
 * 基于 ThreadLocal封装工具类，用于保存和获取当前用户的 Id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setId(Long id) {
        threadLocal.set(id);
    }

    public static Long getId() {
        return threadLocal.get();
    }
}
