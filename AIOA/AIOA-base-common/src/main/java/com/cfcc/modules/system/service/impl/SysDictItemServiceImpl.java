package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.system.entity.SysDict;
import com.cfcc.modules.system.service.ISysDictItemService;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.mapper.SysDictItemMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public void saveDictAndDeparts(SysDictItem sysDictItem, List<String> departs) {

        //先清一遍数据
        sysDictItemMapper.deleteDepartAbout(sysDictItem.getId());
        sysDictItemMapper.insert(sysDictItem);
        String id = sysDictItem.getId();

        sysDictItemMapper.saveDictAndDeparts(id, departs);
    }

    @Override
    public void queryDeparts(IPage<SysDictItem> pageList) {

        List<SysDictItem> records = pageList.getRecords();
        //1 查询出dictId对应的部门信息(一个字典对应多个部门)
        if (records.size() == 0) return;
        List<Map<String, Object>> maps = sysDictItemMapper.queryDeparts(records);
        //2 一个字典对应多个部门结果
        Map<String, List<Map<String, Object>>> res = new HashMap<>();
        Map<String, List<String>> ids = new HashMap<>();
        maps.stream().forEach(m -> {
            String id = m.get("itemId") + "";
            String departId = m.get("departId") + "";
            //ids.add(departId);
            List<Map<String, Object>> data = res.get(id);
            List<String> depIds = ids.get(id);
            if (data == null) {
                List<Map<String, Object>> l = new ArrayList<>();
                l.add(m);
                res.put(id, l);

            } else {
                data.add(m);
            }
            if (depIds != null && depIds.size() > 0) {
                depIds.add(departId);
            } else {
                List<String> depIdsFirst = new ArrayList<>();
                depIdsFirst.add(departId);
                ids.put(id, depIdsFirst);
            }


        });
        //3 为实体赋值
        pageList.getRecords().stream().forEach(r -> {
            r.setDepartsMsg(res.get(r.getId()));
            r.setDeparts(ids.get(r.getId()));
        });
    }

    @Override
    public void deleteDepartAbout(String id) {
        sysDictItemMapper.deleteDepartAbout(id);
    }

    @Override
    public boolean updateByIdAndDeparts(SysDictItem sysDictItem) {

        try {
            sysDictItemMapper.updateById(sysDictItem);
            //删除关联表中的相关数据
            String id = sysDictItem.getId();
            sysDictItemMapper.deleteDepartAbout(id);
            //插入新的数据
            List<String> departs = sysDictItem.getDeparts();
            if (departs != null && departs.size() > 0) {
                sysDictItemMapper.saveDictAndDeparts(id, departs);
            }
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
        return true;

    }

    @Override
    public String getDictItemText(String dictKey, String itemValue) {


        return sysDictItemMapper.getDictItemText(dictKey, itemValue);
    }

    @Override
    public Boolean deleteDictItemByDictID(String id) {
        return sysDictItemMapper.deleteDictItemByDictID(id);
    }
}
