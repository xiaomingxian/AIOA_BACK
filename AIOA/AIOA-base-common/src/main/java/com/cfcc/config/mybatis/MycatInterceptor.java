package com.cfcc.config.mybatis;

import com.cfcc.common.mycat.MycatSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * mycat拦截器，修改schema
 *
 * @Author zhw
 * @Date 2020-01-18
 */
@Slf4j
@Component
@Intercepts(value = {@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MycatInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        while (metaObject.hasGetter("h")) {
            metaObject = SystemMetaObject.forObject(metaObject.getValue("h"));
        }
        String sql = MycatSchema.getMycatAnnot() + metaObject.getValue("delegate.boundSql.sql");
//        System.out.println("sql is " + sql);
        metaObject.setValue("delegate.boundSql.sql", sql);
        Object result = invocation.proceed();
//        System.out.println("invocation.proceed()");
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String prop1 = properties.getProperty("prop1");
        String prop2 = properties.getProperty("prop2");
        System.out.println(prop1 + "-----------" + prop2);
    }
}