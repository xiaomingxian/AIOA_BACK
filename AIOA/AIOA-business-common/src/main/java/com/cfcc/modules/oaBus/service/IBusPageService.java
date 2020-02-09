package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Description: 业务页面表
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
public interface IBusPageService extends IService<BusPage> {

    IPage<BusPage> getPage(Integer pageNo, Integer pageSize, BusPage busPage);

    int saveBusPage(BusPage busPage);

    /**
     * 根据Id查询一条数据
     * @param iId
     * @return
     */
    BusPage getBusPageById(Integer iId);

    boolean updateBusPageById(BusPage busPage);

    void removeBusPageById(String id);

    String queryActShowByPageRef(String pageRef);

    /**
     * 初始化字典数据
     * @return
     */
    Map<String, Object> getInitDictSer();

    void readPictureSer(int pageId, String resourceType, HttpServletResponse response);

    String getPicNameSer(int fileId);
}
