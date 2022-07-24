package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.FunnelVO;
import org.burning.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranService {
    void saveCreateTran(Map<String, Object> map);

    Tran queryTranForDetailById(String id);

    List<FunnelVO> queryCountOfTranGroupByStage();

    List<Tran> queryTranByCustomerId(String id);

    List<Tran> queryTranByContactsId(String id);

    int deleteTranById(String id);

    List<Tran> queryTranByConditionForPage(Map<String, Object> map);

    int queryCountOfTranByCondition(Map<String, Object> map);

    int deleteTranByIds(String[] id);

    Tran queryTranById(String id);
}
