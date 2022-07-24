package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.ClueRemark;
import org.burning.crm.workbench.mapper.ClueRemarkMapper;
import org.burning.crm.workbench.service.ClueRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("clueRemarkService")
public class ClueRemarkServiceImpl implements ClueRemarkService {
    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Override
    public int deleteClueRemarkByClueId(String id) {
        return clueRemarkMapper.deleteClueRemarkByClueId(id);
    }

    @Override
    public int saveCreateClueRemark(ClueRemark clueRemark) {
        return clueRemarkMapper.insertClueRemark(clueRemark);
    }

    @Override
    public int saveEditClueRemark(ClueRemark clueRemark) {
        return clueRemarkMapper.updateClueRemark(clueRemark);
    }

    @Override
    public List<ClueRemark> queryClueRemarkForDetailByClueId(String id) {
        return clueRemarkMapper.selectClueRemarkForDetailByClueId(id);
    }

    @Override
    public int deleteClueRemarkById(String id) {
        return clueRemarkMapper.deleteClueRemarkById(id);
    }
}
