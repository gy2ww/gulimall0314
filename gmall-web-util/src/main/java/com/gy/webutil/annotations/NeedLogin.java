package com.gy.webutil.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gaoyong on 2020/4/21.
 * @Target(ElementType.METHOD)  此注解作用在哪里 是方法上还是类上
 @Retention(RetentionPolicy.RUNTIME)  什么时候起作用 运行时
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedLogin {

    boolean LoginSucess() default true;
}
