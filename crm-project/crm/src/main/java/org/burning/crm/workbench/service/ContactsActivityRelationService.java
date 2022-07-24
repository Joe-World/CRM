package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.ContactsActivityRelation;

import java.util.List;

public interface ContactsActivityRelationService {
    int saveCreateContactsActivityRelationByList(List<ContactsActivityRelation> relationList);

    int deleteContactsActivityRelationByContactsIdActivityId(ContactsActivityRelation relation);
}
