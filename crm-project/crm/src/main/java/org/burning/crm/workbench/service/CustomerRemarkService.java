package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.CustomerRemark;

import java.util.List;

public interface CustomerRemarkService {
    int saveCreateCustomerRemarkByList(List<CustomerRemark> remarkList);

    int saveCreateCustomerRemark(CustomerRemark remark);

    int deleteCustomerRemarkById(String id);

    List<CustomerRemark> queryCustomerRemarkForDetailByCustomerId(String id);

    int saveEditCustomerRemark(CustomerRemark remark);
}
