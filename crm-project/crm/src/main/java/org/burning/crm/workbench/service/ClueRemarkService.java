package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkService {
    int deleteClueRemarkByClueId(String id);

    int saveCreateClueRemark(ClueRemark clueRemark);

    int saveEditClueRemark(ClueRemark clueRemark);

    List<ClueRemark> queryClueRemarkForDetailByClueId(String id);

    int deleteClueRemarkById(String id);
}
