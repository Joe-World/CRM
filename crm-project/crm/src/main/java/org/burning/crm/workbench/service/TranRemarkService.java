package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.TranRemark;

import java.util.List;

public interface TranRemarkService {
    int saveCreateTranRemarkByList(List<TranRemark> remarkList);

    int saveCreateTranRemark(TranRemark remark);

    int deleteTranRemarkById(String id);

    int saveEditTranRemark(TranRemark remark);

    List<TranRemark> queryTranRemarkForDetailByTranId(String id);
}
