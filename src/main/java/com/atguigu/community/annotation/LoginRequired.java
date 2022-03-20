package com.atguigu.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注此注解的方法需要登录才会执行
 */

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)

public @interface LoginRequired {
}
