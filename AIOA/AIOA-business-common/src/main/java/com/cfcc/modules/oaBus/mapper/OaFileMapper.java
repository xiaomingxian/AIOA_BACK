package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.OaFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 附件表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
public interface OaFileMapper extends BaseMapper<OaFile> {

    Boolean savePicText(@Param("fileType") Integer fileType,@Param("picText") String picText);

    String findPicText(@Param("fileType") Integer fileType);

    void updateFileOrder0(@Param("fileType")int fileType);

    void updateFileOrder(@Param("id")Integer id);

    void updateTableIdById(@Param("fileType")Integer fileType,@Param("checked")int checked);

    List<OaFile> getOaFileByTypeAndOrderAndChecked(@Param("fileType")int fileType);

    OaFile getOaFileById(@Param("id") int id);

    void deleteOneFileByType(@Param("id") int id);

    void deleteFileByTypeAndOrder(@Param("list") List<OaFile> oaFileList);

    //    @Insert("insert into oa_file (s_file_type , s_file_name , s_file_path, s_create_by , d_create_time) values (#{sFileType},#{sFileName},#{sFilePath}, #{sCreateBy}, #{dCreateTime})")
    void saveOaFile(OaFile oaFile);

//    @Select("select * from oa_file where s_file_type = 9")
    List<OaFile> getOaFileByType(@Param("fileType")int fileType);

//    @Delete("delete from oa_file where s_file_type = 9")
//    void deleteOneFileByType();

    List<OaFile> getOaFileByIidAndTable(@Param("sBusdataTable") String sBusdataTable, @Param("oaBusdataIid") List<Integer> oaBusdataIid);

    int queryOaFileCount(OaFile oaFile);

    List<OaFile> queryOaFileList(@Param(value = "pageNo") int pageNo, @Param(value = "pageSize") Integer pageSize, @Param(value = "oaFile") OaFile oaFile);

    OaFile queryById(Integer oaFileId);

    boolean updateDocNameById(OaFile oaFile);

    List<OaFile> getOaFileListDao(@Param("tableName") String tableName, @Param("busDataId") String busDataId);

    @Select(" select * from oa_file c where c.s_table = #{tableName} and c.i_table_id = #{busDataId} ")
    List<OaFile> getOaFileList(@Param("tableName") String tableName, @Param("busDataId") String busDataId);

    List<OaFile> getBanWenList(@Param("tableName") String tableName, @Param("busDataId") String busDataId, @Param("sFileType") String sFileType);

    List<OaFile> queryFileListByType(@Param("tableName") String tableName, @Param("busDataId") String busDataId, @Param("sFileType") String sFileType);

    int updateIorderById(@Param("id")Integer id);

    int changeIorderById(@Param("id") Integer id,@Param("iOrder") Integer iOrder);

    List<Integer> queryFileButton();

    void updateFileOrder2(@Param("id") Integer id);

    Integer updatePicText(@Param("fileType") Integer fileType,@Param("picText") String picText);

    List<Map<String, Object>> getOaFileByTableAndTableId(@Param("id") String id,@Param("sBusdataTable") String sBusdataTable,@Param("DBvalue") String DBvalue);

    List<Map<String,Object>> getOaFileByIdAndTable(@Param("tableId") String tableId,@Param("table") String table);
}


