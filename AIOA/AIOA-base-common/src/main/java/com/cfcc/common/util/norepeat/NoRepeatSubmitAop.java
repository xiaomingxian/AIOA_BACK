package com.cfcc.common.util.norepeat;

import com.alibaba.fastjson.JSON;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.common.util.SpringContextUtils;
import com.cfcc.modules.shiro.vo.DefContants;
import lombok.extern.slf4j.Slf4j;
import org.apache.avalon.framework.service.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class NoRepeatSubmitAop {

    private static final String JWT_TOKEN_KEY = DefContants.X_ACCESS_TOKEN;

    @Pointcut("@annotation(com.cfcc.common.util.norepeat.NoRepeatSubmit)")
    public void serviceNoRepeat() {
    }

    @Around("serviceNoRepeat()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);


        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String jwtToken = request.getHeader(JWT_TOKEN_KEY);
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        //排除时间戳
        if (parameterMap.get("_t")!=null){
            parameterMap.remove("_t");
        }
        String key = jwtToken + "-" + request.getRequestURL().toString() + "-" + JSON.toJSONString(parameterMap);
        Object redisVal = redisUtil.get(key);
        if (redisVal== null) {
            log.info("----------------------->>>正常请求,没有重复");
            try {
                MethodSignature signature = (MethodSignature) pjp.getSignature();

                NoRepeatSubmit noRepeatSubmit = signature.getMethod().getAnnotation(NoRepeatSubmit.class);

                redisUtil.set(key,key);
                redisUtil.expire(key, noRepeatSubmit.time());
                Object o = pjp.proceed(pjp.getArgs());

                return o;
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e.getCause());
            }
        } else {
            log.error("------------->>>>>检测到重复提交行为,已被阻止");
            return Result.error("检测到重复提交行为,已被阻止");
//            throw new ServiceException("重复提交");
        }
    }

}
