package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.LunarDate.LunarDate;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.DateUtils;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IoaCalendarService;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.pojo.TaskInfoJsonAble;
import com.cfcc.modules.workflow.service.TaskCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 日程管理表
 * @Author: jeecg-boot
 * @Date: 2019-11-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "日程管理表")
@RestController
@RequestMapping("/oaBus/Calendar/oaCalendar")
public class oaCalendarController implements Job {

    @Autowired
    private IoaCalendarService oaCalendarService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private IOaBusdataService oaBusdataService;;


    /**
     * 时间阴历
     *
     * @param
     * @param
     * @return
     */
    @AutoLog(value = "阴历时间表")
    @ApiOperation(value = "阴历时间表", notes = "阴历时间表")
    @GetMapping(value = "/findLunarDate")
    public Result findLunarDate(@RequestParam(name = "year") Integer year,
                                @RequestParam(name = "month") Integer month,
                                @RequestParam(name = "day") Integer day) {
        Result result = new Result();
        //LunarDate lunarDate = new LunarDate();
        String data = LunarDate.oneDay(year, month, day);

        result.setSuccess(true);
        result.setResult(data);
        return result;
    }

    /**
     * 我的日程
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @param
     * @return
     */
    @AutoLog(value = "日程管理表-分页列表查询")
    @ApiOperation(value = "日程管理表-分页列表查询", notes = "日程管理表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<oaCalendar>> queryPageList(oaCalendar oaCalendar,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest request) {
        Result<IPage<oaCalendar>> result = new Result<IPage<oaCalendar>>();
        /*SysUser currentUser = sysUserService.getCurrentUser(request);
        if(currentUser == null){
            result.error500("未找到对应实体");
        }else{
            String username = currentUser.getUsername();
            oaCalendar.setSCreateBy(username);
        }*/
        IPage<oaCalendar> pageList = oaCalendarService.findPage(pageNo, pageSize, oaCalendar);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /***
     * 查出领导的日程
     *
     */
    @AutoLog(value = "日程管理表-领导的日程")
    @ApiOperation(value = "日程管理表-领导的日程", notes = "日程管理表-领导的日程")
    @GetMapping(value = "/findByLeader")
    public Result<IPage<oaCalendar>> findByLeader(oaCalendar oaCalendar, HttpServletRequest request,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        Result<IPage<oaCalendar>> result = new Result<IPage<oaCalendar>>();
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        List<oaCalendar> oaCalendarList = new ArrayList<>();
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        SysUser currentUser = sysUserService.getCurrentUser(request);
        if(currentUser == null){
            result.error500("未找到对应实体");
        }
        String id = currentUser.getId();
        Integer departId = currentUser.getDepartId();
        List<String> manageIdList = oaCalendarService.getUserId(id);
        IPage<oaCalendar> byLeader = oaCalendarService.findByLeader(pageNo, pageSize, oaCalendar);  //查出领导的所有日程
        List<oaCalendar> oaCalendars = byLeader.getRecords();
        if (oaCalendars == null) {
            result.error500("未找到对应实体");
        } else {
            for (oaCalendar Leader : oaCalendars) {
                Integer iOpenType = Leader.getIOpenType();//公开类型
                String userName = Leader.getSCreateBy();//创建人的名字
                String userNameId = sysUserService.getUserByName(userName).getId();//查出创建人的id
                List<String> departIdList = oaCalendarService.getDepartIdList(userNameId);//查出创建人的部门
                List<String> manageIdList2 = oaCalendarService.getUserId(userNameId);

                if (iOpenType == 1) { //公开类型是全行
                    oaCalendarList.add(Leader);
                } else if (iOpenType == 2) { //公开类型是分管
                    for (int i = 0; i < manageIdList.size(); i++) {
                        for (int j = 0; j < manageIdList2.size(); j++) {
                            if (manageIdList.get(i).equals(manageIdList2.get(j))) {
                                oaCalendarList.add(Leader);
                            }
                        }
                    }
                } else { //公开类型是部门
                    for (int i = 0; i < departIdList.size(); i++)
                        if (departId != null && departIdList.get(i).equals(departId)) {
                            oaCalendarList.add(Leader);
                        }

                }
            }
        }
        pageList.setRecords(oaCalendarList);
        pageList.setTotal(oaCalendarList.size());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /***
     * 查出共享的日程
     *
     */
    @AutoLog(value = "日程管理表-共享日程")
    @ApiOperation(value = "日程管理表-分页列表查询", notes = "日程管理表-分页列表查询")
    @GetMapping(value = "/queryPageList")
    public Result<IPage<oaCalendar>> queryPage(oaCalendar oaCalendar, HttpServletRequest request,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        Result<IPage<oaCalendar>> result = new Result<IPage<oaCalendar>>();
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        List<oaCalendar> oaCalendarList = new ArrayList<>();
        IPage<oaCalendar> queryPageList = oaCalendarService.queryPageList(pageNo, pageSize, oaCalendar);//查出全部的共享日程
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        SysUser user = sysUserService.getUserByName(username);
        if(user == null){
            result.error500("未找到对应实体");
        }
        String id = user.getId();//查出当前用户的id
        String departId = oaCalendarService.getDepartId(user.getId()); //查出当前登陆用户的部门id
        List<String> manageIdList = oaCalendarService.getUserId(id);
        List<oaCalendar> oaCalendars = queryPageList.getRecords();
        if (oaCalendars == null) {
            result.error500("未找到对应实体");
        } else {
            for (oaCalendar share : oaCalendars) {
                Integer iOpenType = share.getIOpenType();//公开类型
                String userName = share.getSCreateBy();//创建人的名字
                String userNameId = sysUserService.getUserByName(userName).getId();//查出创建人的id
                List<String> departIdList = oaCalendarService.getDepartIdList(userNameId);//查出创建人的部门
                List<String> manageIdList2 = oaCalendarService.getUserId(id);


                if (iOpenType == 1) { //公开类型是全行
                    oaCalendarList.add(share);
                } else if (iOpenType == 2) { //公开类型是分管
                    for (int i = 0; i < manageIdList.size(); i++) {
                        for (int j = 0; j < manageIdList2.size(); j++) {
                            if (manageIdList.get(i).equals(manageIdList2.get(j))) {
                                oaCalendarList.add(share);
                            }
                        }
                    }
                } else if (iOpenType == 3) { //公开类型是部门
                    for (int i = 0; i < departIdList.size(); i++)
                        if (departId != null && departIdList.get(i).equals(departId)) {
                            oaCalendarList.add(share);
                        }

                }
            }
        }
        pageList.setRecords(oaCalendarList);
        pageList.setTotal(oaCalendarList.size());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /***
     * 查出代办的日程
     *
     */
    @AutoLog(value = "日程管理表-分页列表查询")
    @ApiOperation(value = "日程管理表-分页列表查询", notes = "日程管理表-分页列表查询")
    @PostMapping(value = "/findwait")
    public Result<IPage<oaCalendar>> findwait(oaCalendar oaCalendar,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        Result<IPage<oaCalendar>> result = new Result<IPage<oaCalendar>>();
        IPage<oaCalendar> pageList = oaCalendarService.findWait(pageNo, pageSize, oaCalendar);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /***
     * 查出常用链接
     *
     */
    @ApiOperation("查询图片数据")
    @GetMapping(value = "/MostUserLink")
    public boolean readPicture(HttpServletResponse response, HttpServletRequest request, @RequestParam(name = "id", required = false) String id,
                               @RequestParam("resourceType") String resourceType) {

        try {
            oaCalendarService.MostUserLink(response, request, id, resourceType);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /***
     * 查出常用链接
     *
     */
    @AutoLog(value = "常用链接")
    @ApiOperation(value = "日程管理表-分页列表查询", notes = "日程管理表-分页列表查询")
    @PostMapping(value = "LinkList")
    public List<Map<String, Object>> LinkList(HttpServletRequest request) {
        List<Map<String, Object>> oaList1 = new ArrayList<>();
        StringBuffer strBuf = new StringBuffer("") ;
        //查询当前用户，作为assignee
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String realname = loginInfo.getRealname();
        String username = loginInfo.getUsername();

        /*{"modelId":"1","condition":{"function_id":"","i_is_state":"","selType":1,"s_create_name":"","d_create_time":""}}*/
        strBuf.append("{\"modelId\":");
        strBuf.append(49) ;
        strBuf.append(",\"pageSize\":");
        strBuf.append(10);
        strBuf.append(",\"pageNo\":");
        strBuf.append(1);
        strBuf.append(",\"condition\":{") ;
        strBuf.append("\"function_id\":") ;
        strBuf.append(107) ;
        strBuf.append("}} ") ;
        Result<IPage<Map<String, Object>>> byModelId = oaBusdataService.getByModelId(strBuf.toString(), realname, username);
//        log.info(byModelId.toString());
        if (byModelId!=null && byModelId.getResult()!=null) {
            oaList1 = byModelId.getResult().getRecords() ;

        }
        return oaList1;
    }

    /**
     * 添加
     *
     * @param oaCalendar
     * @return
     */
    @AutoLog(value = "日程管理表-添加")
    @ApiOperation(value = "日程管理表-添加", notes = "日程管理表-添加")
    @PutMapping(value = "/add")
    public Result<oaCalendar> add(@RequestBody oaCalendar oaCalendar, HttpServletRequest request) {
        Result<oaCalendar> result = new Result<oaCalendar>();
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        try {
            Integer busDataId = oaCalendar.getIFunDataId();
            oaCalendar o = oaCalendarService.findBybusDataId(busDataId);
            if(o == null){
                // 给定截取条件分割字符串为字符数组
                String[] split = oaCalendar.getSUserNames().split(",");
                List<SysUser> listUsr = new ArrayList<>();
                //接收排序好的字符串
                String str = "";
                for (int i = 0; i < split.length; i++) {
                    SysUser user = sysUserService.getUserByName(split[i]); //根据username查出user
                    if (user != null) {
                        listUsr.add(user);
                    }
                }
                List<SysUser> listUserOrder = listUsr.stream().sorted(Comparator.comparing(SysUser::getShowOrder)).collect(Collectors.toList());
                //遍历show_order   根据show_order 找到对应的id

                for (SysUser sysUser : listUserOrder) {
                    str = str + sysUser.getUsername() + ",";
                }
                oaCalendar.setSUserNames(str);
                oaCalendar.setSCreateBy(username);
                if (oaCalendar.getIIsTop() == null) {
                    oaCalendar.setIIsTop(0);
                }
                if (oaCalendar.getIIsLeader() == null) {
                    oaCalendar.setIIsLeader(0);
                }
                if (oaCalendar.getIOpenType() == null) {
                    oaCalendar.setIOpenType(0);
                }
                if (oaCalendar.getIRemindType() == null) {
                    oaCalendar.setIOpenType(0);
                }
                oaCalendar.setState("0");
                oaCalendar.setIFunDataId(1);
                String s = UUID.randomUUID().toString().replace("-", "");
                oaCalendar.setTaskUserId(s);

                oaCalendarService.saveCalendar(oaCalendar);
                result.success("添加成功！");
            }else{
                String sUserNames = oaCalendar.getSUserNames();
                String sUserNames1 = o.getSUserNames();
                String s = sUserNames + sUserNames1 + ",";
                oaCalendar.setSUserNames(s);
                oaCalendar.setIId(o.getIId());
                oaCalendarService.updateByIid(oaCalendar);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 未办存到日程
     *
     * @param oaCalendar
     * @return
     */
    @AutoLog(value = "日程管理表-添加")
    @ApiOperation(value = "日程管理表-添加", notes = "日程管理表-添加")
    @PutMapping(value = "/DataAdd")
    public Result<oaCalendar> DataAdd(@RequestBody oaCalendar oaCalendar, HttpServletRequest request) {
        Result<oaCalendar> result = new Result<oaCalendar>();
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        try {
            Integer busDataId = oaCalendar.getIFunDataId();
            oaCalendar o = oaCalendarService.findBybusDataId(busDataId);
            if(o == null){
                // 给定截取条件分割字符串为字符数组
                String[] split = oaCalendar.getSUserNames().split(",");
                List<SysUser> listUsr = new ArrayList<>();
                //接收排序好的字符串
                String str = "";
                for (int i = 0; i < split.length; i++) {
                    SysUser user = sysUserService.getUserByName(split[i]); //根据username查出user
                    if (user != null) {
                        listUsr.add(user);
                    }
                }
                List<SysUser> listUserOrder = listUsr.stream().sorted(Comparator.comparing(SysUser::getShowOrder)).collect(Collectors.toList());
                //遍历show_order   根据show_order 找到对应的id

                for (SysUser sysUser : listUserOrder) {
                    str = str + sysUser.getUsername() + ",";
                }
                oaCalendar.setSUserNames(str);
                if(oaCalendar.getSCreateBy().length()!=0){
                    oaCalendar.setSCreateBy(oaCalendar.getSCreateBy());
                }else{
                    oaCalendar.setSCreateBy(username);
                }
                if(oaCalendar.getIFunDataId()==0){
                    oaCalendar.setIFunDataId(1);
                }
                if(oaCalendar.getState().length()==0){
                    oaCalendar.setState("0");
                }
                if (oaCalendar.getIIsTop() == null) {
                    oaCalendar.setIIsTop(0);
                }
                if (oaCalendar.getIIsLeader() == null) {
                    oaCalendar.setIIsLeader(0);
                }
                if (oaCalendar.getIOpenType() == null) {
                    oaCalendar.setIOpenType(0);
                }
                if (oaCalendar.getIRemindType() == null) {
                    oaCalendar.setIOpenType(0);
                }

                String s = UUID.randomUUID().toString().replace("-", "");
                oaCalendar.setTaskUserId(s);

                oaCalendarService.saveCalendar(oaCalendar);
                result.success("添加成功！");
            }else{
                String sUserNames = oaCalendar.getSUserNames();
                String sUserNames1 = o.getSUserNames();
                String s = sUserNames + sUserNames1 + ",";
                oaCalendar.setSUserNames(s);
                oaCalendar.setIId(o.getIId());
                oaCalendarService.updateByIid(oaCalendar);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param oaCalendar
     * @return
     */
    @AutoLog(value = "日程管理表-编辑")
    @ApiOperation(value = "日程管理表-编辑", notes = "日程管理表-编辑")
    @PutMapping(value = "/edit")
    public Result<oaCalendar> edit(@RequestBody oaCalendar oaCalendar) {
        Result<oaCalendar> result = new Result<oaCalendar>();
        oaCalendar oaCalendarEntity = oaCalendarService.findById(oaCalendar.getIId());
        if (oaCalendarEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = oaCalendarService.updateByIid(oaCalendar);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "日程管理表-通过id删除")
    @ApiOperation(value = "日程管理表-通过id删除", notes = "日程管理表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(HttpServletRequest request, @RequestParam(name = "id", required = false) String id, @RequestParam(name = "sCreateBy", required = false) String sCreateBy) {
        try {
            //查询当前用户，作为assignee
            String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
            String username = JwtUtil.getUsername(token);
            if (sCreateBy.equals(username)) {
                oaCalendarService.deleteByIid(id);

            } else {
                return Result.error("您没有权限删除其他人的日程");
            }
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "日程管理表-批量删除")
    @ApiOperation(value = "日程管理表-批量删除", notes = "日程管理表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<oaCalendar> deleteBatch(HttpServletRequest request, @RequestParam(name = "ids", required = false) String ids) {
        Result<oaCalendar> result = new Result<oaCalendar>();
        if (ids == null || "".equals(ids.trim())) {

            result.error500("参数不识别！");
        } else {

            List<String> list = Arrays.asList(ids.split(","));
            for (int j = 0; j < list.size(); j++) {
                this.oaCalendarService.deleteByIid(list.get(j));
                result.success("删除成功!");
            }
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "日程管理表-通过id查询")
    @ApiOperation(value = "日程管理表-通过id查询", notes = "日程管理表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<oaCalendar> queryById(@RequestParam(name = "id", required = false) String id) {
        Result<oaCalendar> result = new Result<oaCalendar>();
        oaCalendar oaCalendar = oaCalendarService.getByIid(id);
        if (oaCalendar == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(oaCalendar);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<oaCalendar> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                oaCalendar oaCalendar = JSON.parseObject(deString, oaCalendar.class);
                queryWrapper = QueryGenerator.initQueryWrapper(oaCalendar, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<oaCalendar> pageList = oaCalendarService.list(queryWrapper);
        for (int i = 0; i < pageList.size(); i++) {
            Integer iIsLeader = pageList.get(i).getIIsLeader();//是否是领导
            if (iIsLeader == 1) {  //是
                pageList.get(i).setLeader("是");
            } else {
                pageList.get(i).setLeader("否");
            }
            Integer iIsTop = pageList.get(i).getIIsTop();//是否置顶
            if (iIsTop == 1) {  //是
                pageList.get(i).setTop("是");
            } else {
                pageList.get(i).setTop("否");
            }
            Integer iOpenType = pageList.get(i).getIOpenType();//公开类型
            if (iOpenType == 1) {  //全行
                pageList.get(i).setOpen("全行");
            } else if (iOpenType == 2) {//分管
                pageList.get(i).setOpen("分管");
            } else if (iOpenType == 3) {//部门内
                pageList.get(i).setOpen("部门内");
            }
            Integer remindType = pageList.get(i).getIRemindType();//消息提示类型
            if (remindType == 1) {  //全行:1.10分钟前 2.30分钟前;3.1小时前;4.2小时前
                pageList.get(i).setMessage("10分钟前");
            } else if (remindType == 2) {//分管
                pageList.get(i).setMessage("30分钟前");
            } else if (remindType == 3) {//部门内
                pageList.get(i).setMessage("1小时前");
            } else if (remindType == 4) {
                pageList.get(i).setMessage("2小时前");
            }
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "日程管理表列表");
        mv.addObject(NormalExcelConstants.CLASS, oaCalendar.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("日程管理表列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<oaCalendar> listoaCalendars = ExcelImportUtil.importExcel(file.getInputStream(), oaCalendar.class, params);
                oaCalendarService.saveBatch(listoaCalendars);
                return Result.ok("文件导入成功！数据行数:" + listoaCalendars.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //传入时间限制参数
        List<TaskInfoJsonAble> taskInfoJsonAbles = taskCommonService.allUndoltLimitTime();

        //所有用户id
        List<String> allUserIds = new ArrayList<>();
        taskInfoJsonAbles.stream().forEach(u -> {
            String assignee = u.getAssignee();
            if (StringUtils.isNoneBlank(assignee) && !allUserIds.contains(assignee)) allUserIds.add(assignee);
        });

        Map<String, SysUser> allUserMsg = null;
        if (allUserIds.size() > 0) {
            allUserMsg = sysUserService.selectUsersByUids(allUserIds);
        }

        List<oaCalendar> oaCalendars = new ArrayList<>();

        for (TaskInfoJsonAble taskInfo : taskInfoJsonAbles) {
//            boolean b = taskInfoJsonAbles.stream().anyMatch(u -> u.getId().equals(taskInfo.getId()));
//            if (!b) {
//            SysUser sysUser = sysUserService.getById(assignee);

            oaCalendar oaCalendarInstance = new oaCalendar();

            String assignee = taskInfo.getAssignee();
            SysUser sysUser = allUserMsg.get(assignee);
            Integer FunDataId = Integer.valueOf(taskInfo.getTableId());

            oaCalendarInstance.setSUserNames(sysUser.getUsername()); //用户名字
            oaCalendarInstance.setSTitle(taskInfo.getTitle()); //标题
            oaCalendarInstance.setDStartTime(taskInfo.getCreateTime());//开始时间
            oaCalendarInstance.setDEndTime(null);//结束时间 taskInfo.getEndTime()
            oaCalendarInstance.setIFunDataId(FunDataId); //实例数据id
            //SysUser sysUser = sysUserService.getById(taskInfo.getDrafterId());
            oaCalendarInstance.setSCreateBy(sysUser.getUsername());
            oaCalendarInstance.setIBusFunctionId(Integer.valueOf(taskInfo.getFunctionId()));//业务功能
            oaCalendarInstance.setIBusModelId(Integer.valueOf(taskInfo.getModelId()));//业务模块
            oaCalendarInstance.setDCreateTime(DateUtils.getDate());
			String taskUserId=taskInfo.getId()+assignee;
            oaCalendarInstance.setTaskUserId(taskUserId);
            oaCalendarInstance.setTaskId(taskInfo.getId());
            oaCalendarInstance.setState("1");
			oaCalendar oaCalendar1 = oaCalendarService.findByTaskUserId(taskUserId);
			if(oaCalendar1 == null){ //数据库没有该条数据
				oaCalendars.add(oaCalendarInstance);
			}else{//数据库已经存在该条数据
//				oaCalendarService.updateByIid(oaCalendarInstance);
			}

//            }
        }
        if (oaCalendars.size()>0){
            oaCalendarService.saveBatch(oaCalendars);//批量写入
        }
//        oaCalendarService.saveCalendar(oaCalendars);
    }

    /**
     * 查询功能模块
     *
     * @return
     */
    @AutoLog(value = "权限设置-查询功能模块")
    @ApiOperation(value = "权限设置-查询功能模块", notes = "权限设置-查询功能模块")
    @GetMapping(value = "/getFunctionName")
    public Result<List<BusFunction>> getFunctionName(HttpServletRequest request) {
        Result<List<BusFunction>> result = new Result<>();
        //查询当前用户，作为assignee
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        SysDepart depart = loginInfo.getDepart();
        List<BusFunction> busModelList = oaCalendarService.busFunctionList();
        //权限过滤，有些fun只能特定的部门能看到
        busModelList = oaBusdataService.getFunListByFunUnit(busModelList, depart);
        if (busModelList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busModelList);
            result.setSuccess(true);
        }
        return result;
    }
}
