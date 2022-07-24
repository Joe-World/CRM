package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.Contacts;

import java.util.List;
import java.util.Map;

public interface ContactsService {
    List<Contacts> queryContactsForSaveByName(String name);

    List<Contacts> queryContactsByCustomerId(String id);

    void saveCreateContacts(Map<String ,Object> map);

    List<Contacts> queryContactsByConditionForPage(Map<String, Object> map);

    int queryCountOfContactsByCondition(Map<String, Object> map);

    int deleteContactsByIds(String[] id);

    void saveEditContacts(Map<String, Object> map);

    Contacts queryContactsById(String id);

    Contacts queryContactsForDetailById(String id);
}
