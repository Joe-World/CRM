package org.burning.crm.workbench.service.impl;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.workbench.domain.Customer;
import org.burning.crm.workbench.domain.FunnelVO;
import org.burning.crm.workbench.domain.Tran;
import org.burning.crm.workbench.domain.TranHistory;
import org.burning.crm.workbench.mapper.CustomerMapper;
import org.burning.crm.workbench.mapper.TranHistoryMapper;
import org.burning.crm.workbench.mapper.TranMapper;
import org.burning.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("tranService")

public class TranServiceImpl implements TranService {
    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public void saveCreateTran(Map<String, Object> map) {
        String customerName = (String) map.get("customerName");
        User user = (User) map.get(Constant.SESSION_USER);
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if (customer == null) {
            customer = new Customer();
            customer.setId(UUIDUtils.getUUID());
            customer.setCreateTime(DateUtils.formatDateTime(new Date()));
            customer.setCreateBy(user.getId());
            customer.setName(customerName);
            customer.setOwner(user.getId());
            customerMapper.insertCustomer(customer);
        }

        //保存创建的交易
        Tran tran = new Tran();
        tran.setStage((String)map.get("stage"));
        tran.setContactSummary((String)map.get("contactSummary"));
        tran.setCreateTime(DateUtils.formatDateTime(new Date()));
        tran.setCreateBy(user.getId());
        tran.setDescription((String)map.get("description"));
        tran.setMoney((String) map.get("money"));
        tran.setExpectedDate((String) map.get("expectedDate"));
        tran.setCustomerId(customer.getId());
        tran.setContactsId((String) map.get("contactsId"));
        tran.setActivityId((String)map.get("activityId"));
        tran.setSource((String) map.get("source"));
        tran.setType((String) map.get("type"));
        tran.setId(UUIDUtils.getUUID());
        tran.setName((String) map.get("name"));
        tran.setOwner((String) map.get("owner"));
        tran.setNextContactTime((String)map.get("nextContactTime"));

        tranMapper.insertTran(tran);

        TranHistory tranHistory = new TranHistory();
        tranHistory.setCreateBy(user.getId());
        tranHistory.setCreateTime(DateUtils.formatDateTime(new Date()));
        tranHistory.setTranId(tran.getId());
        tranHistory.setId(UUIDUtils.getUUID());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistoryMapper.insertTranHistory(tranHistory);
    }

    @Override
    public Tran queryTranForDetailById(String id) {
        return tranMapper.selectTranForDetailById(id);
    }

    @Override
    public List<FunnelVO> queryCountOfTranGroupByStage() {
        return tranMapper.selectCountOfTranGroupByStage();
    }

    @Override
    public List<Tran> queryTranByCustomerId(String id) {
        return tranMapper.selectTranByCustomerId(id);
    }

    @Override
    public List<Tran> queryTranByContactsId(String id) {
        return tranMapper.selectTranByContactsId(id);
    }

    @Override
    public int deleteTranById(String id) {
        return tranMapper.deleteTranById(id);
    }

    @Override
    public List<Tran> queryTranByConditionForPage(Map<String, Object> map) {
        return tranMapper.selectTranByConditionForPage(map);
    }

    @Override
    public int queryCountOfTranByCondition(Map<String, Object> map) {
        return tranMapper.selectCountOfTranByCondition(map);
    }

    @Override
    public int deleteTranByIds(String[] id) {
        return tranMapper.deleteTranByIds(id);
    }

    @Override
    public Tran queryTranById(String id) {
        return tranMapper.selectTranById(id);
    }


}
