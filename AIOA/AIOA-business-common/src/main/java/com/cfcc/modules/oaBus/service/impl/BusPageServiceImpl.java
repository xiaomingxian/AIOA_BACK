package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.mapper.BusPageMapper;
import com.cfcc.modules.oaBus.service.IBusPageService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.system.entity.SysDict;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysDictService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务页面表
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
@Service
public class BusPageServiceImpl extends ServiceImpl<BusPageMapper, BusPage> implements IBusPageService {

    @Autowired
    BusPageMapper busPageMapper;
    @Autowired
    IOaFileService iOaFileService;
    @Autowired
    ISysDictService iSysDictService;

    @Override
    public IPage<BusPage> getPage(Integer pageNo, Integer pageSize, BusPage busPage) {
        int total = busPageMapper.countBusPage(busPage);
        List<BusPage> modelList =  busPageMapper.queryBusPage((pageNo-1)*pageSize,pageSize,busPage);
        IPage<BusPage> pageList = new Page<BusPage>();
        pageList.setRecords(modelList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    /**
     * 插入一条数据
     * @param busPage
     * @return
     */

    @Override
    public int saveBusPage(BusPage busPage) {
        busPage.setDCreateTime(DateUtils.getDate());
        int res = busPageMapper.addBusPage(busPage);
        return 0;
    }

    @Override
    public BusPage getBusPageById(Integer iId) {
        return busPageMapper.getBusPageById(iId);
    }

    @Override
    public boolean updateBusPageById(BusPage busPage) {

        boolean res = false ;
        busPage.setDCreateTime(DateUtils.getDate());
        int updateRes = busPageMapper.updateBusPageById(busPage);
        if(updateRes > 0) {
            res = true;
        }
        return res;
    }

    @Override
    public void removeBusPageById(String id) {
        busPageMapper.deleteBusPageById(id);
    }

    @Override
    public String queryActShowByPageRef(String pageRef) {
        return busPageMapper.queryActShowByPageRef(pageRef);
    }

    /**
     * 初始化字典数据
     * @return
     */
    @Override
    public Map<String, Object> getInitDictSer() {
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<SysDict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.select("dict_name","dict_code") ;

        List<SysDict> dictList = iSysDictService.list(dictQueryWrapper);
        List<DictModel> dictByCodeList = iSysDictService.getDictByCode("sql");
        List<DictModel> regulars = iSysDictService.getDictByCode("regular_expressions");
        map.put("dicList",dictList);
        map.put("dictByCodeList",dictByCodeList);
        map.put("regulars",regulars);
        return map;
    }

    /**
     * 读取图片预览
     * @param pageId
     * @param resourceType
     * @param response
     */
    @Override
    public void readPictureSer(int pageId, String resourceType, HttpServletResponse response) {
        try {
            BusPage busPage = getById(pageId);
            String path = iOaFileService.getById(busPage.getIFileId()).getSFilePath() ;
            File file = new File(path);
            FileInputStream stream = new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = stream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询fileName
     * @param fileId
     * @return
     */
    @Override
    public String getPicNameSer(int fileId) {
        OaFile oaFile = iOaFileService.getById(fileId);
        String picName = oaFile.getSFileName() ;
        if(picName == null || picName == ""){
            picName = "" ;
        }
        return picName;
    }
}
