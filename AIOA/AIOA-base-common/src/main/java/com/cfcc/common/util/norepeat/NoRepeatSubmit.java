package com.cfcc.common.util.norepeat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    /**
     * 重复统计时长
     *
     * @return
     */
    int time() default 3;
}
