package com.cfcc.modules.oabutton.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oaBus.service.impl.BusProcSetServiceImpl;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.service.IOaButtonService;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.oabutton.service.IOaProcButtonService;
import io.swagger.annotations.Api;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Api(tags="按钮显示管理")
@RestController
@RequestMapping("/buttonPermit/ButtonPermit")
public class ButtonPermitShow {

    @Autowired
    private IBusProcSetService iBusProcSetService;

    @Autowired
    private IOaButtonService iOaButtonServicep;

    @Autowired
    private IOaButtonSetService iOaButtonSetService;

    /**
     * 按钮查询
     */
    @GetMapping(value = "/ButtonPermit")
    public Result<OaButton> ButtonPermit(HttpServletRequest request,Integer iBusFunctionId,Integer iIsCreater,Integer iIsReader,Integer iIsLastsender) {
        Result result = new Result();

        String schema = MycatSchema.getSchema();
        RedisUtil redisUtil = new RedisUtil();
        List<BusProcSet> busProcSetList = iBusProcSetService.findList(schema);
        List<Integer> list=new ArrayList();
        if (busProcSetList == null) {
            result.error500("未找到对应实体");

        } else {
            for (BusProcSet busProc : busProcSetList) { //遍历业务和流程关联配置表
                Integer iProcButtonId = busProc.getIProcButtonId();
                if(iBusFunctionId == iProcButtonId){   //判断页面传过来的iBusFunctionId 和 iProcButtonId是不是相等
                    List<OaButtonSet> oaButtonSetList = iOaButtonSetService.findList(schema);
                    for (OaButtonSet buttonSet : oaButtonSetList) {//遍历按钮权限表
                        Integer iProcButtonId1 = buttonSet.getIProcButtonId();
                          if(iProcButtonId1 == iProcButtonId){  //判断iProcButtonId是不是相等
                             if(buttonSet.getIPermitType()==0){ //如果不控制表示直接显示 把ibuttonId放到集合里
                                 Integer iButtonId = buttonSet.getIButtonId();
                                 list.add(iButtonId);
                             }else{ //如果控制  判断读者、用户、处理人 那个是能显示的   放到list集合里面
                                 if(iIsCreater == 1){
                                     Integer iButtonId = buttonSet.getIButtonId();
                                     list.add(iButtonId);
                                 }else if(iIsReader == 1){
                                     Integer iButtonId = buttonSet.getIButtonId();
                                     list.add(iButtonId);
                                 }else if(iIsLastsender == 1){
                                     Integer iButtonId = buttonSet.getIButtonId();
                                     list.add(iButtonId);
                                 }
                             }

                          }
                    }
                }
            }
            List<OaButton> oaButtonList = iOaButtonServicep.findList();
            for (int i = 0; i <list.size() ; i++) {  //把存进去的按钮id都遍历
                for (OaButton button : oaButtonList) {
                    Integer buttonIId = button.getIId();
                    if(buttonIId == list.get(i)){  //判断button表里面有没有  返回给前端
                        result.setResult(oaButtonList);
                    }
                }

            }
        }

        return result;


    }
}
