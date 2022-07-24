package org.burning.crm.workbench.service.impl;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.workbench.domain.*;
import org.burning.crm.workbench.mapper.*;
import org.burning.crm.workbench.service.ClueService;
import org.burning.crm.workbench.service.TranHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("clueService")
public class ClueServiceImpl implements ClueService {
    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private TranRemarkMapper tranRemarkMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public List<Clue> queryClueByConditionForPage(Map<String, Object> map) {
        return clueMapper.selectClueByConditionForPage(map);
    }

    @Override
    public int queryCountOfClueByCondition(Map<String, Object> map) {
        return clueMapper.selectCountOfClueByCondition(map);
    }

    @Override
    public int deleteClueByIds(String[] ids) {
        return clueMapper.deleteClueByIds(ids);
    }

    @Override
    public Clue queryClueById(String id) {
        return clueMapper.selectClueById(id);
    }

    @Override
    public int saveEditClue(Clue clue) {
        return clueMapper.updateClue(clue);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        String clueId = (String) map.get("clueId");
        User user = (User) map.get(Constant.SESSION_USER);
        String isCreateTran = (String) map.get("isCreateTran");

        //根据id查询线索的信息
        Clue clue = clueMapper.selectClueById(clueId);
        //把该线索中有关公司的信息转换到客户表中
        Customer c = new Customer();
        c.setAddress(clue.getAddress());
        c.setContactSummary(clue.getContactSummary());
        c.setCreateBy(user.getId());
        c.setCreateTime(DateUtils.formatDateTime(new Date()));
        c.setDescription(clue.getDescription());
        c.setId(UUIDUtils.getUUID());
        c.setName(clue.getCompany());
        c.setNextContactTime(clue.getNextContactTime());
        c.setOwner(user.getId());
        c.setPhone(clue.getPhone());
        c.setWebsite(clue.getWebsite());
        customerMapper.insertCustomer(c);

        //把线索中有关个人的信息转换到联系人表中
        Contacts contacts = new Contacts();
        contacts.setAddress(clue.getAddress());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setId(UUIDUtils.getUUID());
        contacts.setAppellation(clue.getAppellation());
        contacts.setCustomerId(c.getId());
        contacts.setDescription(clue.getDescription());
        contacts.setEmail(clue.getEmail());
        contacts.setFullname(clue.getFullname());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setSource(clue.getSource());
        contacts.setOwner(user.getId());
        contactsMapper.insertContacts(contacts);

        //把线索的备注信息转换到客户备注表中一份
        //把线索的备注信息转换到联系人备注表中一份
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        if (clueRemarkList != null && clueRemarkList.size() > 0) {
            CustomerRemark customerRemark = null;
            ContactsRemark contactsRemark = null;
            List<CustomerRemark> customerRemarkList = new ArrayList<>();
            List<ContactsRemark> contactsRemarkList = new ArrayList<>();
            for (ClueRemark remark : clueRemarkList) {
                customerRemark = new CustomerRemark();
                customerRemark.setId(UUIDUtils.getUUID());
                customerRemark.setCreateBy(remark.getCreateBy());
                customerRemark.setCreateTime(remark.getCreateTime());
                customerRemark.setCustomerId(c.getId());
                customerRemark.setEditFlag(remark.getEditFlag());
                customerRemark.setNoteContent(remark.getNoteContent());
                customerRemark.setEditBy(remark.getEditBy());
                customerRemark.setEditTime(remark.getEditTime());
                customerRemarkList.add(customerRemark);

                contactsRemark = new ContactsRemark();
                contactsRemark.setId(UUIDUtils.getUUID());
                contactsRemark.setContactsId(contacts.getId());
                contactsRemark.setEditBy(remark.getEditBy());
                contactsRemark.setCreateBy(remark.getCreateBy());
                contactsRemark.setEditTime(remark.getEditTime());
                contactsRemark.setEditFlag(remark.getEditFlag());
                contactsRemark.setNoteContent(remark.getNoteContent());
                contactsRemark.setCreateTime(remark.getCreateTime());
                contactsRemarkList.add(contactsRemark);
            }
            customerRemarkMapper.insertCustomerRemarkByList(customerRemarkList);
            contactsRemarkMapper.insertContactsRemarkByList(contactsRemarkList);
        }

        //把线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);
        if (clueActivityRelations != null && clueActivityRelations.size() > 0) {
            ContactsActivityRelation contactsActivityRelation = null;
            List<ContactsActivityRelation> contactsActivityRelations = new ArrayList<>();
            for (ClueActivityRelation relation : clueActivityRelations) {
                contactsActivityRelation = new ContactsActivityRelation();
                contactsActivityRelation.setActivityId(relation.getActivityId());
                contactsActivityRelation.setContactsId(contacts.getId());
                contactsActivityRelation.setId(UUIDUtils.getUUID());
                contactsActivityRelations.add(contactsActivityRelation);
            }

            contactsActivityRelationMapper.insertContactsActivityRelationByList(contactsActivityRelations);
        }

        //如果需要创建交易,还要往交易表中添加一条记录
        if ("true".equals(isCreateTran)) {
            Tran tran=new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(contacts.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtils.formatDateTime(new Date()));
            tran.setCustomerId(c.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtils.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String) map.get("stage"));
            tranMapper.insertTran(tran);

            //如果需要创建交易,还要把线索的备注信息转换到交易备注表中一份
            if (clueRemarkList != null && clueRemarkList.size() > 0) {
                List<TranRemark> tranRemarks = new ArrayList<>();
                TranRemark tranRemark = null;
                for (ClueRemark remark:clueRemarkList) {
                    tranRemark = new TranRemark();
                    tranRemark.setCreateBy(remark.getCreateBy());
                    tranRemark.setCreateTime(remark.getCreateTime());
                    tranRemark.setEditBy(remark.getEditBy());
                    tranRemark.setId(UUIDUtils.getUUID());
                    tranRemark.setEditFlag(remark.getEditFlag());
                    tranRemark.setEditTime(remark.getEditTime());
                    tranRemark.setTranId(tran.getId());
                    tranRemark.setNoteContent(remark.getNoteContent());
                    tranRemarks.add(tranRemark);
                }

                tranRemarkMapper.insertTranRemarkByList(tranRemarks);

                TranHistory tranHistory = new TranHistory();
                tranHistory.setExpectedDate(tran.getExpectedDate());
                tranHistory.setTranId(tran.getId());
                tranHistory.setStage(tran.getStage());
                tranHistory.setMoney(tran.getMoney());
                tranHistory.setId(UUIDUtils.getUUID());
                tranHistory.setCreateTime(DateUtils.formatDateTime(new Date()));
                tranHistory.setCreateBy(user.getId());

                tranHistoryMapper.insertTranHistory(tranHistory);
            }
        }
        //删除线索的备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        //删除线索和市场活动的关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        //删除线索
        clueMapper.deleteClueById(clueId);
    }
}
