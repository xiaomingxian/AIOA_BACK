package com.cfcc.common.util;

import java.lang.reflect.Field;

/**
 * 字符串工具类
 */
public class StringUtil {
    /**
     * 将一个对象中的所有空值属性设置为null
     * @param obj
     * @return
     */
    public Object changeToNull(Object obj){
        Class c = obj.getClass() ;
        try {
            Field[] fs = c.getDeclaredFields() ;
            for(Field f : fs){
                f.setAccessible(true);
                String st = f.get(obj) + "";
                String str = st.replace(" ","");
                if(str.equals("") || str == null || str.equals("null")){
                    f.set(obj,null);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj ;
    }
}
