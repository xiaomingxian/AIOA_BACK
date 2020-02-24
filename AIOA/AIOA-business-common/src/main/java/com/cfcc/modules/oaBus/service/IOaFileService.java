package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oabutton.entity.OaButton;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 附件表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface IOaFileService extends IService<OaFile> {

    Boolean savePicText(Integer fileType, String picText);

    String findPicText(Integer fileType);

    List<OaFile> getOaFileByType0(Integer fileType);

    Boolean checkDefault(Integer fileType ,Integer id,Integer checked);

    Boolean readNotPicture(Integer id,String resourceType,HttpServletResponse response);

    Boolean updatePicture(Integer fileType, Integer id);

    void deleteFileByTypeAndOrder(List<OaFile> oaFileList);

//    String getOneFileByType();
    Boolean readPictureSer(Integer fileType, String resourceType, HttpServletResponse response);

//    void saveOaFile(OaFile oaFile);

    //根据文件类型获取登录图片
    List<OaFile> getOaFileByType(Integer fileType);

    //根据文件类型删除登录图片信息
    void deleteOneFileByType(Integer id);

    List<OaFile> getOaFileContext(String DBvalue);

    IPage<OaFile> getPage(Integer pageNo, Integer pageSize, OaFile oaFile);

    OaFile queryById(Integer oaFileId);

    List<Map<String,Object>> getOaFile(String DBvalue);

    boolean updateDocNameById(Map<String,Object> map);

    public String getColumList(String sBusdataTable,Integer iId, String DBvalue);

    List<OaFile> getOaFileListSer(String tableName, String busDataId);

    List<OaFile> getOaFileList(String stable, String tableid);

    List<OaFile> getBanWenList(String tableName, String busDataId, String sFileType);

    List<OaFile> copyFiles(String param);

    int updateIorderById(Integer id);

    boolean sortFile(Map<String, Object> param);

    OaFile singleCopyFile(Map<String, Object> map);

    boolean isShowFileBtn(Map<String, List<OaButton>> map);

    Boolean updatePicText(Integer fileType, String picText);

    /**
     * 批量上传
     * @param files
     * @param sTable
     * @param iTableId
     * @param sFileType
     * @param request
     * @param response
     * @return
     */
    List<OaFile> batchUploads(MultipartFile files, String sTable, Integer iTableId, String sFileType, HttpServletRequest request, HttpServletResponse response);

    List<Map<String, Object>> getOaBusdata(String sBusdataTable, List<Map<String, Object>> oaBusdata, BusFunction busFunction, String DBvalue);

    List<Map<String, Object>> getOaFileByTableAndTableId(String id, String sBusdataTable,String DBvalue);
}
