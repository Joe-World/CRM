package org.burning.crm.workbench.service.impl;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.workbench.domain.Contacts;
import org.burning.crm.workbench.domain.Customer;
import org.burning.crm.workbench.mapper.ContactsMapper;
import org.burning.crm.workbench.mapper.CustomerMapper;
import org.burning.crm.workbench.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("contactsService")
public class ContactsServiceImpl implements ContactsService {
    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<Contacts> queryContactsForSaveByName(String name) {
        return contactsMapper.selectContactsForSaveByName(name);
    }

    @Override
    public List<Contacts> queryContactsByCustomerId(String id) {
        return contactsMapper.selectContactsByCustomerId(id);
    }

    @Override
    public void saveCreateContacts(Map<String,Object> map) {
        User user = (User) map.get(Constant.SESSION_USER);
        String customerName = (String) map.get("customerName");
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if (customer == null) {
            customer = new Customer();
            customer.setOwner(user.getId());
            customer.setCreateBy(user.getId());
            customer.setCreateTime(DateUtils.formatDateTime(new Date()));
            customer.setName(customerName);
            customer.setId(UUIDUtils.getUUID());
            customerMapper.insertCustomer(customer);
        }

        Contacts contacts = new Contacts();
        contacts.setCustomerId(customer.getId());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));
        contacts.setId(UUIDUtils.getUUID());
        contacts.setOwner((String)map.get("owner"));
        contacts.setSource((String)map.get("source"));
        contacts.setContactSummary((String)map.get("contactSummary"));
        contacts.setMphone((String)map.get("mphone"));
        contacts.setJob((String)map.get("job"));
        contacts.setFullname((String)map.get("fullname"));
        contacts.setEmail((String) map.get("email"));
        contacts.setDescription((String)map.get("description"));
        contacts.setAppellation((String)map.get("appellation"));
        contacts.setAddress((String) map.get("address"));
        contacts.setNextContactTime((String) map.get("nextContactTime"));
        contactsMapper.insertContacts(contacts);
    }

    @Override
    public List<Contacts> queryContactsByConditionForPage(Map<String, Object> map) {
        return contactsMapper.selectContactsByConditionForPage(map);
    }

    @Override
    public int queryCountOfContactsByCondition(Map<String, Object> map) {
        return contactsMapper.selectCountOfContactsByCondition(map);
    }

    @Override
    public int deleteContactsByIds(String[] id) {
        return contactsMapper.deleteContactsByIds(id);
    }

    @Override
    public Contacts queryContactsById(String id) {
        return contactsMapper.selectContactsById(id);
    }

    @Override
    public Contacts queryContactsForDetailById(String id) {
        return contactsMapper.selectContactsForDetailById(id);
    }

    @Override
    public void saveEditContacts(Map<String,Object> map) {
        User user = (User) map.get(Constant.SESSION_USER);
        String customerName = (String) map.get("customerName");
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if (customer == null) {
            customer = new Customer();
            customer.setOwner(user.getId());
            customer.setCreateBy(user.getId());
            customer.setCreateTime(DateUtils.formatDateTime(new Date()));
            customer.setName(customerName);
            customer.setId(UUIDUtils.getUUID());
            customerMapper.insertCustomer(customer);
        }

        Contacts contacts = new Contacts();
        contacts.setId((String)map.get("id"));
        contacts.setCustomerId(customer.getId());
        contacts.setEditBy(user.getId());
        contacts.setEditTime(DateUtils.formatDateTime(new Date()));
        contacts.setOwner((String)map.get("owner"));
        contacts.setSource((String)map.get("source"));
        contacts.setContactSummary((String)map.get("contactSummary"));
        contacts.setMphone((String)map.get("mphone"));
        contacts.setJob((String)map.get("job"));
        contacts.setFullname((String)map.get("fullname"));
        contacts.setEmail((String) map.get("email"));
        contacts.setDescription((String)map.get("description"));
        contacts.setAppellation((String)map.get("appellation"));
        contacts.setAddress((String) map.get("address"));
        contacts.setNextContactTime((String) map.get("nextContactTime"));
        contactsMapper.updateContacts(contacts);
    }
}
