package com.cfcc.common.mycat;

import com.cfcc.common.constant.CommonConstant;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.common.util.SpringContextUtils;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.LoginInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理mycat的schema
 *
 * @Author zhw
 * @Date 2020-01-18
 */
public class MycatSchema {
    public static String ANNOTATION = "/*!mycat:schema=";
    public static String ANNOTATION_END = "*/ ";

    public static String getSchema() {
        String orgSchema = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                orgSchema = (String) request.getAttribute("orgSchema");
                if (StringUtils.isBlank(orgSchema)) {
                    String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
                    RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
                    LoginInfo loginInfo = (LoginInfo) redisUtil.get(CommonConstant.LOGIN_INFO + token);
                    if (loginInfo != null) {
                        orgSchema = loginInfo.getOrgSchema();
                    }
                }
            }
        }
        return orgSchema == null ? "" : orgSchema;
    }

    public static void setSchema(String schema) {
    }

    public static String getMycatAnnot() {
        String orgSchema = MycatSchema.getSchema();
        if (StringUtils.isNotBlank(orgSchema)) {
            return ANNOTATION + orgSchema + ANNOTATION_END;
        } else {
            return "";
        }
    }
}