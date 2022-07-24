package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.ContactsRemark;

import java.util.List;

public interface ContactsRemarkService {
    int saveCreateContactsRemarkByList(List<ContactsRemark> remark);

    int saveCreateContactsRemark(ContactsRemark contactsRemark);

    int deleteContactsRemarkById(String id);

    int saveEditContactsRemark(ContactsRemark contactsRemark);

    List<ContactsRemark> queryContactsRemarkForDetailByContactsId(String id);
}
