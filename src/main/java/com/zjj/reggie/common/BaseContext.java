package com.zjj.reggie.common;

/**
 * @create: 2023-04-04 19:49
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 封装 ThreadLocal 的工具类
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentID(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentID() {
        return threadLocal.get();
    }
}
