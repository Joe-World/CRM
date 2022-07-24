package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.ClueActivityRelation;
import org.burning.crm.workbench.domain.ContactsActivityRelation;
import org.burning.crm.workbench.mapper.ContactsActivityRelationMapper;
import org.burning.crm.workbench.service.ClueActivityRelationService;
import org.burning.crm.workbench.service.ContactsActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("contactsActivityRelationService")
public class ContactsActivityRelationServiceImpl implements ContactsActivityRelationService {
    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Override
    public int saveCreateContactsActivityRelationByList(List<ContactsActivityRelation> relationList) {
        return contactsActivityRelationMapper.insertContactsActivityRelationByList(relationList);
    }

    @Override
    public int deleteContactsActivityRelationByContactsIdActivityId(ContactsActivityRelation relation) {
        return contactsActivityRelationMapper.deleteContactsActivityRelationByContactsIdActivityId(relation);
    }
}
