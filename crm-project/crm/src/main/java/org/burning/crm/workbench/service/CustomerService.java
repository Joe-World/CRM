package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    int saveCreateCustomer(Customer customer);

    List<Customer> queryCustomerByConditionForPage(Map<String, Object> map);

    int queryCountOfCustomerByCondition(Map<String, Object> map);

    int deleteCustomerByIds(String[] id);

    int saveEditCustomer(Customer customer);

    Customer queryCustomerById(String id);

    Customer queryCustomerForDetailById(String id);

    String[] queryAllCustomerNames();

    String[] queryCustomerNamesByName(String name);
}
