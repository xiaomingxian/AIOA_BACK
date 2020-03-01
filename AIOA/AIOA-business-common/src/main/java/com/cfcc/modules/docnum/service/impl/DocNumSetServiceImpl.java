package com.cfcc.modules.docnum.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.docnum.mapper.DocNumManageMapper;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.docnum.entity.DocNumSet;
import com.cfcc.modules.docnum.mapper.DocNumSetMapper;
import com.cfcc.modules.docnum.service.IDocNumSetService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.model.DepartIdModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 文号配置
 * @Author: jeecg-boot
 * @Date: 2019-10-16
 * @Version: V1.0
 */
@Service
public class DocNumSetServiceImpl extends ServiceImpl<DocNumSetMapper, DocNumSet> implements IDocNumSetService {

    @Resource
    private DocNumSetMapper docNumSetMapper;

    @Resource
    private DocNumManageMapper docNumManageMapper;

    /**
     * 根据年、文号id查询最大docnum
     * @param id
     * @param sYear 选填
     * @return
     */
    @Override
    public DocNumSet queryById(Integer id,String sYear) {
        DocNumSet docNumSet = docNumSetMapper.selectDocNumById(id);
        DocNumManage docNumManage = new DocNumManage();
        docNumManage.setIDocnumId(docNumSet.getIId());
        docNumManage.setSYear(sYear);
        DocNumManage maxDocNum= docNumManageMapper.queryMaxDocNum(docNumManage);
        docNumSet.setIDocNum(maxDocNum.getIDocNum());
        return docNumSet;
    }

    @Override
    public DocNumSet queryById(Integer id) {
        return docNumSetMapper.queryById(id);
    }

    @Override
    public boolean updateDocNumById(DocNumSet docNumSet) {
        String[] newDepart = docNumSet.getSelecteddeparts().split(",");
        if (docNumSet.getIId() != null && docNumSet.getIId() != 0) {
            boolean ok = docNumSetMapper.deleteDocNumDeptByIdocId(docNumSet.getIId());
            if (ok) {
                for (int i = 0; i < newDepart.length; i++) {
                    boolean b = docNumSetMapper.insertDepart(docNumSet.getIId(), newDepart[i]);
                }
            }
        }
        return docNumSetMapper.updateDocNumById(docNumSet);
    }

    @Override
    public boolean deleteDocNumTitleByIID(String id) {
        return docNumSetMapper.deleteDocNumByIID(id);
    }

    @Override
    public IPage<DocNumSet> queryDocNumList(DocNumSet docNumSet, Integer pageNo, Integer pageSize) {
        int total = docNumSetMapper.selectDocNumTotal(docNumSet);
        List<DocNumSet> paperList = docNumSetMapper.selectDocNumList(docNumSet, (pageNo - 1) * pageSize, pageSize);
        IPage<DocNumSet> pageList = new Page<DocNumSet>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(paperList);
        return pageList;
    }

    @Override
    public List<BusModel> busModelList() {
        return docNumSetMapper.busModelList();

    }

    @Override
    public List<BusFunction> busFunctionList(Integer ibusModelId) {
        return docNumSetMapper.busFunctionList(ibusModelId);
    }


    @Override
    public int insertDocNum(DocNumSet docNumSet) {
        return docNumSetMapper.insertDocNum(docNumSet);
    }

    /**
     * 新增文号部门关联
     *
     * @param id
     * @param departId
     * @return
     */
    @Override
    public boolean insertDepart(int id, String departId) {
        return docNumSetMapper.insertDepart(id, departId);
    }

    /**
     * 根据文号id查询相关部门信息
     *
     * @param docId
     * @return
     */
    @Override
    public List<DepartIdModel> queryDepartIdsOfDocNum(String docId) {
        try {
            String depIdList = "";
            List<DepartIdModel> depIdModelList = new ArrayList<>();
            String departIds = docNumSetMapper.selectDepartIds(docId);//查询部门ids
            if (departIds == null || ",".equals(departIds)) {
                return null;
            } else {
                String[] userDepList = departIds.split(",");
                if (userDepList.length > 0) {
                    for (int i = 0; i < userDepList.length; i++) {
                        if (i < userDepList.length - 1) {
                            depIdList += ('"' + userDepList[i] + '"' + ',');
                        } else {
                            depIdList += ('"' + userDepList[i] + '"');
                        }
                    }
                    List<SysDepart> depList = docNumSetMapper.selectDepartByIds(depIdList);
                    if (depList != null || depList.size() > 0) {
                        for (SysDepart depart : depList) {
                            depIdModelList.add(new DepartIdModel().convertByUserDepart(depart));
                        }
                    }
                    return depIdModelList;
                }
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    @Override
    public Integer queryByIdAndSendObj(DocNumManage docNumManage) {
        Integer oaFileId;
        DocNumSet docNumSet = docNumSetMapper.queryById(docNumManage.getIDocnumId());
        if (docNumManage.getISendObj() == 2) {
            oaFileId = docNumSet.getIDtemplateId();
        } else {
            oaFileId = docNumSet.getIUtemplateId();
        }
        return oaFileId;
    }

    @Override
    public List<DocNumSet> getDocNumNameListByBf(Integer iBusFunctionId, String sDeptId) {
        return docNumSetMapper.getDocNumNameList(iBusFunctionId, sDeptId);
    }


    @Override
    public Integer queryByTemplateId(DocNumManage docNumManage) {
        DocNumSet docNumSet = docNumSetMapper.queryById(docNumManage.getIDocnumId());
        Integer oaFileId = docNumSet.getIAtemplateId();
        return oaFileId;
    }

}
