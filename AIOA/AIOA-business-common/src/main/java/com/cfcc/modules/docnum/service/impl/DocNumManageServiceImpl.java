package com.cfcc.modules.docnum.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.docnum.mapper.DocNumManageMapper;
import com.cfcc.modules.docnum.service.IDocNumManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 文号管理表
 * @Author: jeecg-boot
 * @Date: 2019-11-18
 * @Version: V1.0
 */
@Service
public class DocNumManageServiceImpl extends ServiceImpl<DocNumManageMapper, DocNumManage> implements IDocNumManageService {
    @Resource
    private DocNumManageMapper docNumManageMapper;

    @Override
    public IPage<DocNumManage> getNumList(DocNumManage docNumManage, Integer pageNo, Integer pageSize) {
        int total = docNumManageMapper.getNumListTotal(docNumManage);
        List<DocNumManage> list = docNumManageMapper.getNumList(docNumManage, (pageNo - 1) * pageSize, pageSize);
        IPage<DocNumManage> pageList = new Page<DocNumManage>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public boolean updateDocNumStatus(DocNumManage docNumManage) {
        List<DocNumManage> busdataDocNum = docNumManageMapper.getBusdataDocNum(docNumManage);
        if (busdataDocNum.size() > 0 && busdataDocNum.get(0) != null) {
            busdataDocNum.get(0).setIBusdataId(0);
            docNumManageMapper.updateDocNumStatus(busdataDocNum.get(0));
        }
        return docNumManageMapper.updateDocNumStatus(docNumManage);
    }

    @Override
    public int addDocNum(DocNumManage docNumManage) {
        List<DocNumManage> busdataDocNum = docNumManageMapper.getBusdataDocNum(docNumManage);
        if (busdataDocNum.size() > 0 && busdataDocNum.get(0) != null) {
            busdataDocNum.get(0).setIBusdataId(0);
            docNumManageMapper.updateDocNumStatus(busdataDocNum.get(0));
            if (docNumManage.getStatus() != null) {
                /**
                 * 修改原文号状态为0
                 * 修改现文号为业务id
                 */
                DocNumManage docNumUpdata = new DocNumManage();
                docNumUpdata.setIId(docNumManage.getStatus());
                docNumUpdata.setIBusdataId(docNumManage.getIBusdataId());
                docNumManageMapper.updateDocNumStatus(docNumUpdata);
            } else {
                docNumManageMapper.addDocNum(docNumManage);
            }
        } else {
            docNumManageMapper.addDocNum(docNumManage);
        }
        DocNumManage maxDocNum = docNumManageMapper.queryMaxDocNum(docNumManage);
        docNumManageMapper.updateMaxNum(maxDocNum);
        return maxDocNum.getIDocNum();
    }

    @Override
    public List<DocNumManage> checkDocNum(DocNumManage docNumManage) {
        List<DocNumManage> docNumManages = docNumManageMapper.checkDocNum(docNumManage);
        return docNumManages;
    }
}
