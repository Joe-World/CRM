package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.ContactsRemark;
import org.burning.crm.workbench.mapper.ContactsRemarkMapper;
import org.burning.crm.workbench.service.ContactsRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("contactsRemarkService")
public class ContactsRemarkServiceImpl implements ContactsRemarkService {
    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;

    @Override
    public int saveCreateContactsRemarkByList(List<ContactsRemark> remark) {
        return contactsRemarkMapper.insertContactsRemarkByList(remark);
    }

    @Override
    public int saveCreateContactsRemark(ContactsRemark contactsRemark) {
        return contactsRemarkMapper.insertContactsRemark(contactsRemark);
    }

    @Override
    public int deleteContactsRemarkById(String id) {
        return contactsRemarkMapper.deleteContactsRemarkById(id);
    }

    @Override
    public int saveEditContactsRemark(ContactsRemark contactsRemark) {
        return contactsRemarkMapper.updateContactsRemark(contactsRemark);
    }

    @Override
    public List<ContactsRemark> queryContactsRemarkForDetailByContactsId(String id) {
        return contactsRemarkMapper.selectContactsRemarkForDetailByContactsId(id);
    }


}
