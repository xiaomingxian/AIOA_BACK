package com.cfcc.modules.oaBus.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.elasticsearch.utils.FileUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.mapper.*;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.mapper.SysDepartMapper;
import com.cfcc.modules.system.mapper.SysDictItemMapper;
import com.cfcc.modules.system.mapper.SysDictMapper;
import com.cfcc.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Description: 附件表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Service
public class OaFileServiceImpl extends ServiceImpl<OaFileMapper, OaFile> implements IOaFileService {
    @Autowired
    private OaFileMapper oaFileMapper;

    @Autowired
    private BusFunctionMapper busFunctionMapper;

    @Autowired
    private BusModelMapper busModelMapper;

    @Autowired
    private OaBusdataMapper oaBusdataMapper;

    @Autowired
    private BusPageDetailMapper busPageDetailMapper;

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysDepartMapper sysDepartMapper;

    @Autowired
    @Lazy
    private IOaFileService oaFileService;

    @Autowired
    @Lazy
    private ISysUserService sysUserService;

    //附件上传地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    //附件临时文件地址
    @Value(value = "${jeecg.path.tempFilePath}")
    private String tempPath;

    //模板地址
    @Value(value = "${jeecg.path.templateFilePath}")
    private String templateFilePath;


    @Override
    public Boolean savePicText(Integer fileType, String picText) {
        Boolean a = oaFileMapper.savePicText(fileType, picText);
        return a;
    }

    @Override
    public String findPicText(Integer fileType) {
        String picText = oaFileMapper.findPicText(fileType);
        return picText;
    }

    @Override
    public List<OaFile> getOaFileByType0(Integer fileType) {
        return oaFileMapper.getOaFileByType(fileType);
    }

    @Override
    public Boolean checkDefault(Integer fileType, Integer id, Integer checked) {
        try {
            oaFileMapper.updateTableIdById(fileType, checked);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean readNotPicture(Integer id, String resourceType, HttpServletResponse response) {
        try {
            OaFile oaFile = oaFileMapper.getOaFileById(id);
            if (oaFile != null) {
                if (oaFile.getSFilePath() != null) {
                    String path = oaFile.getSFilePath();
                    File file = new File(path);
                    FileInputStream stream = new FileInputStream(file);
                    byte[] b = new byte[1024];
                    int len = -1;
                    while ((len = stream.read(b, 0, 1024)) != -1) {
                        response.getOutputStream().write(b, 0, len);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean updatePicture(Integer fileType, Integer id) {
        try {
            oaFileMapper.updateFileOrder0(fileType);
            oaFileMapper.updateFileOrder(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteFileByTypeAndOrder(List<OaFile> oaFileList) {
        oaFileMapper.deleteFileByTypeAndOrder(oaFileList);
    }

    @Override
    public Boolean readPictureSer(Integer fileType, String resourceType, HttpServletResponse response) {
        try {
            List<OaFile> oaFileList = oaFileMapper.getOaFileByTypeAndOrderAndChecked(fileType);
            if (oaFileList.size() == 1) {
                File file = new File(oaFileList.get(0).getSFilePath());
                FileInputStream stream = new FileInputStream(file);
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = stream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<OaFile> getOaFileByType(Integer fileType) {
        return oaFileMapper.getOaFileByTypeAndOrderAndChecked(fileType);
    }

    @Override
    public void deleteOneFileByType(Integer id) {
        oaFileMapper.deleteOneFileByType(id);
    }

    @Override
    public IPage<OaFile> getPage(Integer pageNo, Integer pageSize, OaFile oaFile) {
        int total = oaFileMapper.queryOaFileCount(oaFile);
        List<OaFile> modelList = oaFileMapper.queryOaFileList((pageNo - 1) * pageSize, pageSize, oaFile);
        IPage<OaFile> pageList = new Page<OaFile>();
        pageList.setRecords(modelList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public List<Map<String, Object>> getOaFileByTableAndTableId(String id, String sBusdataTable, String DBvalue) {
        List<Map<String, Object>> oaFileList = oaFileMapper.getOaFileByTableAndTableId(id, sBusdataTable, DBvalue);
        Iterator<Map<String, Object>> iterator = oaFileList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            String sFileName = map.get("s_file_name") + "";
            String str = sFileName.substring(sFileName.lastIndexOf(".") + 1);
            if (!str.equalsIgnoreCase("doc") && !str.equalsIgnoreCase("docx")
                    && !str.equalsIgnoreCase("txt") && !str.equalsIgnoreCase("pdf")
                    && !str.equalsIgnoreCase("pdf") && !str.equalsIgnoreCase("xls")
                    && !str.equalsIgnoreCase("xlsx")) {
                iterator.remove();
                continue;
            }
            String sFilePath = map.get("s_file_path") + "";
            File file = new File(sFilePath);
            String sContent = null;
            if (file.exists()) { //文件存在返回true
                switch (str) {
                    case "doc":
                        sContent = FileUtils.readDocFile(sFilePath);
                        break;
                    case "docx":
                        sContent = FileUtils.readDocxFile(sFilePath);
                        break;
                    case "txt":
                        sContent = FileUtils.ReadTxtFile(sFilePath);
                        break;
                    case "pdf":
                        sContent = FileUtils.readPdfFile(sFilePath);
                        break;
                    case "xls":
                        sContent = FileUtils.readXLSFile(sFilePath);
                        break;
                    case "xlsx":
                        sContent = FileUtils.readxlsxFile(sFilePath);
                        break;
                    default:
                        break;
                }
                String sTitle = oaBusdataMapper.getBusdataByIdAndTableName(id, sBusdataTable, DBvalue);
                map.put("s_file_path", sContent);
                map.put("sTitle", sTitle);
            }
        }
        return oaFileList;
    }

    @Override
    public List<Map<String, Object>> getOaFileByIdAndTable(String tableId, String table) {
        List<Map<String, Object>> oaFileList = oaFileMapper.getOaFileByIdAndTable(tableId, table);
        Iterator<Map<String, Object>> iterator = oaFileList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            String sFileName = map.get("s_file_name") + "";
            String str = sFileName.substring(sFileName.lastIndexOf(".") + 1);
            if (!str.equalsIgnoreCase("doc") && !str.equalsIgnoreCase("docx")
                    && !str.equalsIgnoreCase("txt") && !str.equalsIgnoreCase("pdf")
                    && !str.equalsIgnoreCase("pdf") && !str.equalsIgnoreCase("xls")
                    && !str.equalsIgnoreCase("xlsx")) {
                iterator.remove();
                continue;
            }
        }
        return oaFileList;
    }

    /**
     * 附件检索
     *
     * @return
     */
    @Override
    public List<OaFile> getOaFileContext(String DBvalue) {
        List<BusModel> busModels = findBusModels(DBvalue);
        List<OaFile> oaFileLists = new ArrayList<>();
        for (BusModel busModel : busModels) {
            String sBusdataTable = busModel.getSBusdataTable();
            Integer iId = busModel.getIId();
            List<Integer> oaBusdataListIid = oaBusdataMapper.getBusdataIidByTable(sBusdataTable, iId);
            if (oaBusdataListIid.size() <= 0) {
                continue;
            }
            //获取需要存储的file
            List<OaFile> oaFileList = oaFileMapper.getOaFileByIidAndTable(sBusdataTable, oaBusdataListIid);
            //System.out.println("---------oaFileList :----------------" + oaFileList);
            //判断文件类型并获取其内容
            String sContent = null;
            List<OaFile> oaFileReMoveList = new ArrayList<>();
            for (OaFile oaFile : oaFileList) {
                String sFilePath = oaFile.getSFilePath();
                File file = new File(sFilePath);
                String sFileName = oaFile.getSFileName();
                String str = sFileName.substring(sFileName.lastIndexOf(".") + 1);
                if (!str.equalsIgnoreCase("doc") && !str.equalsIgnoreCase("docx")
                        && !str.equalsIgnoreCase("txt") && !str.equalsIgnoreCase("pdf")
                        && !str.equalsIgnoreCase("pdf") && !str.equalsIgnoreCase("xls")
                        && !str.equalsIgnoreCase("xlsx")) {
//
                    oaFileReMoveList.add(oaFile);
                    continue;
                }
                if (file.exists()) { //文件存在返回true
                    switch (str) {
                        case "doc":
                            sContent = FileUtils.readDocFile(sFilePath);
                            break;
                        case "docx":
                            sContent = FileUtils.readDocxFile(sFilePath);
                            break;
                        case "txt":
                            sContent = FileUtils.ReadTxtFile(sFilePath);
                            break;
                        case "pdf":
                            sContent = FileUtils.readPdfFile(sFilePath);
                            break;
                        case "xls":
                            sContent = FileUtils.readXLSFile(sFilePath);
                            break;
                        case "xlsx":
                            sContent = FileUtils.readxlsxFile(sFilePath);
                            break;
                        default:
//                            System.out.println(sFilePath + "文件内容不可读取！！！！");
                            break;
                    }
                }
                oaFile.setSContent(sContent);
                String sTitle = oaBusdataMapper.getBusdataByOaFile(oaFile);
                oaFile.setSTitle(sTitle);
            }
            for (OaFile oaFile : oaFileReMoveList) {
                oaFileList.remove(oaFile);
            }
            oaFileLists.addAll(oaFileList);
        }
        //System.out.println("------开始:---------" + oaFileLists.toString());
        return oaFileLists;
    }


    @Override
    public OaFile queryById(Integer oaFileId) {
        OaFile oaFile = new OaFile();
        oaFile = oaFileMapper.queryById(oaFileId);
        return oaFile;
    }

    @Override
    public boolean updateDocNameById(Map<String, Object> map, HttpServletRequest request) {
        String sfilepath = "";
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String orgSchema = loginInfo.getOrgSchema();
//        if ( orgSchema != null &&!orgSchema.equals("")) {
//            sfilepath = uploadpath + File.separator + orgSchema + File.separator + map.get("s_file_path") + "";
//        } else {
//            sfilepath = uploadpath+ File.separator + map.get("s_file_path") + "";
//        }
        String s_file_name = map.get("s_file_name") + "";
        String iid = map.get("i_id") + "";
        OaFile initFile = oaFileService.queryById(Integer.valueOf(iid));
        String oldName = initFile.getSFileName().substring(0, initFile.getSFileName().lastIndexOf("."));
        String newName = initFile.getSFileName().replace(oldName, s_file_name);
        initFile.setSFileName(newName);
//        initFile.setSFilePath(sfilepath);
        boolean ok = false;
        ok =  oaFileMapper.updateDocNameById(initFile);
//        File file = new File(sfilepath);
//        if (file.exists()) {
//            String reNamePath = file.getParent() + File.separator + initFile.getSFileName();
//            File newFile = new File(reNamePath);
//            if (file.renameTo(newFile)) {
//                OaFile oaFile = new OaFile();
//                oaFile.setIId(Integer.valueOf(iid));
//                oaFile.setSFileName(newFile.getName());
//                oaFile.setSFilePath(reNamePath);
//                ok = oaFileMapper.updateDocNameById(oaFile);
//            }
//        }
        return ok;
    }

    @Override
    public List<OaFile> getOaFileListSer(String tableName, String busDataId) {
        return oaFileMapper.getOaFileListDao(tableName, busDataId);

    }

    @Override
    public List<OaFile> getOaFileList(String stable, String tableid) {
        return oaFileMapper.getOaFileList(stable, tableid);
    }

    @Override
    public List<OaFile> getBanWenList(String tableName, String busDataId, String sFileType) {
        return oaFileMapper.getBanWenList(tableName, busDataId, sFileType);
    }

    @Override
    public List<OaFile> copyFiles(String param, HttpServletRequest request) {
        //根据orgSchema生成文件地址
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String ctxPath = uploadpath;
        Calendar calendar = Calendar.getInstance();
        String calendarPath = calendar.get(Calendar.YEAR) +
                File.separator + (calendar.get(Calendar.MONTH) + 1) +
                File.separator + calendar.get(Calendar.DATE);
        String upPath = ""; //生成文件地址
        String suffexPath = "";  //查询文件前缀
        if (loginInfo.getOrgSchema() != null && !loginInfo.getOrgSchema().equals("")) {
            upPath = ctxPath.replace("//", "/" +
                    "") + File.separator + loginInfo.getOrgSchema() + File.separator + calendarPath;
            suffexPath = ctxPath.replace("//", "/" +
                    "") + File.separator + loginInfo.getOrgSchema();
        } else {
            upPath = ctxPath.replace("//", "/" +
                    "") + File.separator + calendarPath;
            suffexPath = ctxPath.replace("//", "/" + "");
        }

        JSONObject obj = JSONObject.parseObject(param);
        File file = new File(upPath);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
        List<OaFile> returnList = new ArrayList<>();
        OaFile saveFile = new OaFile();
        saveFile.setSTable(obj.get("sTable") + "");
        saveFile.setITableId((Integer) obj.get("iTableId"));
        saveFile.setSFileType(obj.get("sFileType") + "");
        List<OaFile> fileList = oaFileMapper.queryFileListByType(obj.get("sTable") + "", obj.get("iTableId") + "", obj.get("sFileType") + "");
        for (OaFile oafile : fileList) {
            File lastPath = new File(suffexPath + File.separator + oafile.getSFilePath());
            String filename = System.currentTimeMillis() + com.cfcc.common.util.FileUtils.generatePassword(5);
            String newFileName = oafile.getSFileName().replace(oafile.getSFileName().substring(0, oafile.getSFileName().lastIndexOf(".")), filename);
//            StringBuilder str = new StringBuilder(oafile.getSFileName());
//            StringBuilder filename = str.insert(oafile.getSFileName().indexOf("."), "_" + System.currentTimeMillis());
            File uploadPath = new File(upPath + File.separator + newFileName);
            try {
                uploadPath.createNewFile();
                FileInputStream oldfile = new FileInputStream(lastPath);
                FileOutputStream newfile = new FileOutputStream(uploadPath);
                byte[] bytes = new byte[1024];
                int i = 0;
                while ((i = oldfile.read(bytes)) > 0) {
                    newfile.write(bytes);
                }
                oldfile.close();
                newfile.close();
                saveFile.setSFileType(oafile.getSFileType());
                saveFile.setSTable(obj.get("receiveTable") + "");
                saveFile.setITableId((Integer) obj.get("receiveId"));
                saveFile.setSFileName(oafile.getSFileName());
                saveFile.setSFilePath(calendarPath + File.separator + newFileName);
                saveFile.setDCreateTime(new Date());
                oaFileService.save(saveFile);
//                QueryWrapper<OaFile> c = new QueryWrapper<>();
//                c.setEntity(saveFile);
//                OaFile ad = oaFileService.getOne(c);
                oaFileService.updateIorderById(saveFile.getIId());
                returnList.add(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }

    @Override
    public int updateIorderById(Integer id) {
        int i = oaFileMapper.updateIorderById(id);
        return id;
    }

    @Override
    public boolean sortFile(Map<String, Object> param) {
        boolean flag = false;
        Integer id1 = (Integer) param.get("id1");
        Integer iorder1 = (Integer) param.get("iorder1");
        Integer id2 = (Integer) param.get("id2");
        Integer iorder2 = (Integer) param.get("iorder2");
        oaFileMapper.changeIorderById(id1, iorder2);
        oaFileMapper.changeIorderById(id2, iorder1);
        flag = true;
        return flag;
    }

    @Override
    public OaFile singleCopyFile(Map<String, Object> map, HttpServletRequest request) {
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String initFile = "";
        if (loginInfo.getOrgSchema() != null && !loginInfo.getOrgSchema().equals("")) {
            initFile = uploadpath + File.separator + loginInfo.getOrgSchema() + File.separator + map.get("sFilePath") + "";
        } else {
            initFile = uploadpath + File.separator + map.get("sFilePath") + "";
        }

        OaFile file = new OaFile();
        String fileName = initFile.substring(initFile.lastIndexOf(File.separator) + 1, initFile.length());
        try {
            String tempPaths = "";
            if (map.get("sFilePath") != null) {
                if ((map.get("sFileType") != null) && (map.get("sFileType")+"").equals("7")) {
                    tempPaths = uploadpath + File.separator + "templateFiles";
                } else {
                    tempPaths = uploadpath + File.separator + "temporaryFiles";
                }
            }
            File temp = new File(tempPaths);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            FileInputStream oldfile = new FileInputStream(initFile);
            FileOutputStream newfile = new FileOutputStream(temp + File.separator + fileName);
            byte[] bytes = new byte[1024];
            int i = 0;
            while ((i = oldfile.read(bytes)) > 0) {
                newfile.write(bytes);
            }
            file.setSFilePath(temp + File.separator + fileName);
            file.setSFileName(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public boolean isShowFileBtn(Map<String, List<OaButton>> map) {
        boolean flag = false;
        List<OaButton> isDefend = map.get("isDefend");
        List<OaButton> isNotDefend = map.get("isNotDefend");

        //查询附件关联按钮权限
        List<Integer> buttonIds = oaFileMapper.queryFileButton();
        for (OaButton btn : isDefend) {
            if (buttonIds.contains(btn.getIId())) {
                flag = true;
                break;
            }
        }
        for (OaButton btn2 : isNotDefend) {
            if (buttonIds.contains(btn2.getIId())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public Boolean updatePicText(Integer fileType, String picText) {
        Integer count = oaFileMapper.updatePicText(fileType, picText);
        if (count == 1) {
            return true;
        }
        return false;
    }

    @Override
    public String getColumList(String sBusdataTable, Integer iId, String DBvalue) {
        List<String> isEsColumnLists = busPageDetailMapper.getCloumnNameByTableAndEsquery(sBusdataTable, iId, DBvalue);
        String columnLists = "";
        if (isEsColumnLists != null) {
            String columnList = isEsColumnLists.toString().replace("[", "");
            columnLists = columnList.toString().replace("]", "");
        }
        return columnLists;
    }

    /**
     * 获取BusPageDetail中存入es的数据
     *
     * @param sBusdataTable
     * @param isEsColumnLists
     * @return
     */
    public List<BusPageDetail> getDetailColums(String sBusdataTable, List<String> isEsColumnLists) {
        //获取
        List<BusPageDetail> clumnNameLists = busPageDetailMapper.getColumsNameByTableAndColumn(sBusdataTable, isEsColumnLists);
        return clumnNameLists;
    }

    @Override
    public List<Map<String, Object>> getOaBusdata(String sBusdataTable, List<Map<String, Object>> oaBusdata, BusFunction busFunction, String DBvalue) {
        Iterator<Map<String, Object>> iterator = oaBusdata.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> oaBusdatum = iterator.next();
            Integer functionId = null;
            if (oaBusdatum.get("i_bus_function_id") == null && oaBusdatum.get("i_bus_function_id") != "") {
                continue;
            } else {
                functionId = Integer.parseInt(oaBusdatum.get("i_bus_function_id") + "");
            }
            if (functionId == null) {
                continue;
            }
            Set<String> set = oaBusdatum.keySet();
            for (String key : set) {
                Object value = oaBusdatum.get(key);
                if (key.equals("s_title")) {
                    oaBusdatum.put(key, "【" + busFunction.getSName() + "】" + value);
                }
                List<String> sDictIdlist = busPageDetailMapper.getSDictIdByKey(functionId, sBusdataTable, key, DBvalue);
                System.out.println("................." + sDictIdlist + ".................");
                String aa = null;
                if (sDictIdlist.size() == 0) {
                    continue;
                }
                boolean flag = false;
                for (int i = 0; i < sDictIdlist.size(); i++) {
                    aa = sDictIdlist.get(i);
                    System.out.println(aa);
                    if (aa == null) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    continue;
                }
                if (sDictIdlist.size() == 1) {  //如果有返回值，则对其赋值
                    if (sDictIdlist.get(0) != null && sDictIdlist.get(0).trim().length() > 0) {
                        String sysDictId = sysDictMapper.getDictIdByDictCode(sDictIdlist.get(0), DBvalue);
                        String itemValue = sysDictItemMapper.getItemTextById(sysDictId, value, DBvalue);
                        if (itemValue == null) {
                            System.out.println("key:" + key + ",  value:" + value + "该字段查不到其含义！！！！！");
                        } else {
                            oaBusdatum.put(key, itemValue);
                        }
                    }
                }

            }
            oaBusdatum.put("table_name", sBusdataTable);
        }


        return oaBusdata;
    }

    /**
     * 根据i_is_es判断是否纳入全文检索
     * 当i_is_es为1时为纳入全文检索
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getOaFile(String DBvalue) {
        List<BusFunction> busFunctionList = findBusFunction(DBvalue);
        List<Map<String, Object>> oaBusdataList = new ArrayList<>();
        for (BusFunction busFunction : busFunctionList) {
            //表名
            String sBusdataTable = busFunction.getSBusdataTable();
            //要检索字段变为字符串格式
            String columnLists = getColumList(sBusdataTable, busFunction.getIId(), DBvalue);
            if (columnLists.equals("")) {
                continue;
            }
            //根据表名和业务模块id查询数据
            List<Map<String, Object>> oaBusdata = oaBusdataMapper.getBusdataByTable(columnLists, busFunction, DBvalue);
            if (oaBusdata.size() == 0) {  //执行下一循环
                //System.out.println("-----------无数据存入ES库！！！！！-----------");
                continue;
            }
            Iterator<Map<String, Object>> iterator = oaBusdata.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> oaBusdatum = iterator.next();
                Integer functionId = (Integer) oaBusdatum.get("i_bus_function_id");
                if (functionId == null) {
                    continue;
                }
                Set<String> set = oaBusdatum.keySet();
                for (String key : set) {
                    Object value = oaBusdatum.get(key);
//                    System.out.println("key:"+key+",  sBusdataTable:"+sBusdataTable+"， functionId" + functionId);
                    List<String> sDictIdlist = busPageDetailMapper.getSDictIdByKey(functionId, sBusdataTable, key, DBvalue);
                    //System.out.println("................." + sDictIdlist + ".................");
                    String aa = null;
                    if (sDictIdlist.size() == 0) {
                        continue;
                    }
                    boolean flag = false;
                    for (int i = 0; i < sDictIdlist.size(); i++) {
                        aa = sDictIdlist.get(i);
                        //System.out.println(aa);
                        if (aa == null) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                    if (sDictIdlist.size() == 1) {  //如果有返回值，则对其赋值
                        if (sDictIdlist.get(0) != null && sDictIdlist.get(0).trim().length() > 0) {
                            String sysDictId = sysDictMapper.getDictIdByDictCode(sDictIdlist.get(0), DBvalue);
                            String itemValue = sysDictItemMapper.getItemTextById(sysDictId, value, DBvalue);
                            if (itemValue == null) {
                                //System.out.println("key:" + key + ",  value:" + value + "该字段查不到其含义！！！！！");
                            } else {
                                oaBusdatum.put(key, itemValue);
                            }
                        }
                    }
                    if (key.equals("s_title")) {
                        oaBusdatum.put(key, "【" + busFunction.getSName() + "】" + value);
                    }
                }
                oaBusdatum.put("table_name", sBusdataTable);
            }
            if (oaBusdata == null) {
                continue;
            }
            oaBusdataList.addAll(oaBusdata);
        }
        return oaBusdataList;
    }

    public List<BusFunction> findBusFunction(String DBvalue) {
        List<BusFunction> busFunctionList = busFunctionMapper.getFunByIsEs(DBvalue);
        List<BusModel> busModels = new ArrayList<>();
        for (BusFunction busFunction : busFunctionList) {
            BusModel busModel = busModelMapper.getBusModelById(busFunction.getIBusModelId(), DBvalue);
            if (busModel.getIId() == busFunction.getIBusModelId()) {
                busFunction.setSBusdataTable(busModel.getSBusdataTable());
                continue;
            }
        }
        return busFunctionList;
    }


    //-----------------------------------------------

    /**
     * /**
     * 根据i_is_es判断是否纳入全文检索
     * 当i_is_es为1时为纳入全文检索
     *
     * @return
     */
   /* @Override
    public List<Map<String,Object>> getOaFile() {
        List<BusModel> busModels = findBusModels();
        List<Map<String,Object>> oaBusdataList = new ArrayList<>();
        for (BusModel busModel : busModels) {
            //该表名
            String sBusdataTable = busModel.getSBusdataTable();
            List<String> isEsColumnLists = busPageDetailMapper.getCloumnNameByTableAndEsquery(sBusdataTable);
            String columnLists = getColumList(sBusdataTable);
            //获取查询条件
            List<BusPageDetail> clumnNameLists = getDetailColums(sBusdataTable,isEsColumnLists);

            System.out.println("---------clumnNameLists: "+clumnNameLists.toString());
            //根据表名和业务模块id查询数据
            List<Map<String,Object>> oaBusdata = oaBusdataMapper.getBusdataByTable(columnLists,busModel);
            if (oaBusdata.size() <= 0){  //执行下一循环
                System.out.println("-----------无数据存入ES库！！！！！-----------");
                continue;
            }
//            System.out.println(oaBusdata.toString());
            //遍历map集合，将特定字符的数据赋予其含义
            for (Map<String, Object> oaBusdatum : oaBusdata) {
                Integer functionId = (Integer) oaBusdatum.get("i_bus_function_id");
//                Long i_id = (Long) oaBusdatum.get("i_id");
                Set<String> set = oaBusdatum.keySet();
                for (String key : set) {
                    Object value = oaBusdatum.get(key);
                    String sDictId = busPageDetailMapper.getSDictIdByKey(key,sBusdataTable,functionId );
                    System.out.println("................." + sDictId +".................");
                    if (sDictId != null){  //如果有返回值，则对其赋值
                        System.out.println("-------------sDictId"+sDictId+",value:"+value+"-------------");
                        String itemValue = sysDictItemMapper.getItemTextById(sDictId,value);
                        if (itemValue == null){
                            System.out.println("key:" + key + ",  value:"+ value +"该字段查不到其含义！！！！！");
                        }else {
                            oaBusdatum.put(key, itemValue);
                        }
                    }
                }
                //往map里存放表名
                oaBusdatum.put("table_name", sBusdataTable);
//                System.out.println(oaBusdatum.toString());
                *//*for (BusPageDetail clumnNameList : clumnNameLists) {
                    oaBusdatum.put("sColumnName" , clumnNameList.getSColumnName());
                }*//*
            }

            if (oaBusdata == null){
                continue;
            }
            oaBusdataList.addAll(oaBusdata);
        }

        System.out.println("oaBusdataList:   "+oaBusdataList.toString());
        return oaBusdataList;
    }
*/
    public List<BusModel> findBusModels(String DBvalue) {
        List<BusFunction> busFunctionList = busFunctionMapper.getFunByIsEs(DBvalue);
        List<BusModel> busModelList = new ArrayList<>();
        for (BusFunction busFunction : busFunctionList) {
            BusModel busModel = busModelMapper.getBusModelById(busFunction.getIBusModelId(), DBvalue);
            busModelList.add(busModel);
        }
        return busModelList;
    }

    @Override
    public List<OaFile> batchUploads(MultipartFile file,String filename, String sTable, Integer iTableId, String sFileType, HttpServletRequest request, HttpServletResponse response) {
        List<OaFile> fileIds = new ArrayList<>();
        try {
            String ctxPath = uploadpath;
            String fileName = null;
            Calendar calendar = Calendar.getInstance();
            String calendarPath = calendar.get(Calendar.YEAR) +
                    File.separator + (calendar.get(Calendar.MONTH) + 1) +
                    File.separator + calendar.get(Calendar.DATE);
            String path = "";
            String orgSchema = request.getAttribute("orgSchema") + "";
            if (orgSchema != null && !orgSchema.equals("null")) {
                path = ctxPath.replace("//", "/" +
                        "") + File.separator + orgSchema + File.separator + calendarPath;
            } else {
                path = ctxPath.replace("//", "/" +
                        "") + File.separator + calendarPath;
            }
            if (file != null) {
                File parent = new File(path);
                if (!parent.exists()) {
                    parent.mkdirs();// 创建文件根目录
                }
                String orgName = filename;// 获取文件名
                fileName = System.currentTimeMillis()+ com.cfcc.common.util.FileUtils.generatePassword(5)+orgName.substring(orgName.indexOf("."));
//                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
                String savePath = parent.getPath() + File.separator + fileName;
                File savefile = new File(savePath);
                FileCopyUtils.copy(file.getBytes(), savefile);
                OaFile oaFile = new OaFile();
                oaFile.setSTable(sTable);
                oaFile.setITableId(iTableId);
                oaFile.setSFileType(sFileType);
                oaFile.setSFileName(orgName);        //设置附件名字
                oaFile.setSFilePath(calendarPath + File.separator + fileName);        //设置文件路径
//                oaFile.setSCreateBy(username);
                oaFile.setDCreateTime(new Date());
                oaFileService.save(oaFile);
                oaFileService.updateIorderById(oaFile.getIId());
                fileIds.add(oaFile);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return fileIds;
    }


}
