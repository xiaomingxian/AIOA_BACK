package com.cfcc.modules.papertitle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.impl.OaFileServiceImpl;
import com.cfcc.modules.papertitle.entity.OaTemplate;
import com.cfcc.modules.papertitle.mapper.OaTemplateMapper;
import com.cfcc.modules.papertitle.service.IOaTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date: 2019-10-15
 * @Version: V1.0
 */
@Service
public class OaTemplateServiceImpl extends ServiceImpl<OaTemplateMapper, OaTemplate> implements IOaTemplateService {

    @Resource
    private OaTemplateMapper oaTemplateMapper;

    @Autowired
    @Lazy
    private OaFileServiceImpl oaFileService;


    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public OaTemplate queryById(Integer id) {
        return oaTemplateMapper.selectTemplateById(id);
    }

    @Override
    public boolean updateTemplateById(OaTemplate oaTemplate) {
        return oaTemplateMapper.updateTemplateById(oaTemplate);
    }

    @Override
    public boolean deleteTemplateTitleByIID(String id) {
        return oaTemplateMapper.deleteTemplateByIID(id);
    }

    @Override
    public IPage<OaTemplate> queryTemplateList(OaTemplate oaTemplate, Integer pageNo, Integer pageSize) {
        int total = oaTemplateMapper.selectTemplateTotal(oaTemplate);
        List<OaTemplate> templateList = oaTemplateMapper.selectTemplateList(oaTemplate, (pageNo - 1) * pageSize, pageSize);
        IPage<OaTemplate> pageList = new Page<OaTemplate>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(templateList);
        return pageList;
    }

    @Override
    public List<OaTemplate> templateList(Integer type) {
        return oaTemplateMapper.templateList(type);
    }

    @Override
    public OaFile getFileNameById(String iFileId) {
        return oaTemplateMapper.getFileNameById(iFileId);
    }

    @Override
    public List<OaFile> batchUploads(MultipartFile[] files, String sTable, Integer iTableId, String sFileType, HttpServletRequest request, HttpServletResponse response) {
        List<OaFile> fileIds = new ArrayList<>();
        try {
            //获取用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            String ctxPath = uploadpath;
            String fileName = null;
            Calendar calendar = Calendar.getInstance();
            String path = ctxPath.replace("//", "/" +
                    "") + "/" + calendar.get(Calendar.YEAR) +
                    "/" + (calendar.get(Calendar.MONTH) + 1) +
                    "/" + calendar.get(Calendar.DATE) + "/";
            if (files.length > 0) {
                File parent = new File(path);
                if (!parent.exists()) {
                    parent.mkdirs();// 创建文件根目录
                }
                for (MultipartFile file : files) {
                    String orgName = file.getOriginalFilename();// 获取文件名
                    fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
                    String savePath = parent.getPath() + File.separator + fileName;
                    File savefile = new File(savePath);
                    FileCopyUtils.copy(file.getBytes(), savefile);
                    OaFile oaFile = new OaFile();
                    oaFile.setSTable(sTable);
                    oaFile.setITableId(iTableId);
                    oaFile.setSFileType(sFileType);
                    oaFile.setSFileName(orgName);        //设置附件名字
                    oaFile.setSFilePath(savePath);        //设置文件路径
                    oaFile.setSCreateBy(username);
                    oaFile.setDCreateTime(new Date());
                    oaFileService.save(oaFile);
                    QueryWrapper<OaFile> c = new QueryWrapper<>();
                    c.setEntity(oaFile);
                    OaFile ad = oaFileService.getOne(c);
                    fileIds.add(ad);
                    oaFileService.updateIorderById(oaFile.getIId());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return fileIds;
    }
}
