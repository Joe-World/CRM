package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.CustomerRemark;
import org.burning.crm.workbench.mapper.CustomerRemarkMapper;
import org.burning.crm.workbench.service.CustomerRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerRemarkService")
public class CustomerRemarkServiceImpl implements CustomerRemarkService {
    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;


    @Override
    public int saveCreateCustomerRemarkByList(List<CustomerRemark> remarkList) {
        return customerRemarkMapper.insertCustomerRemarkByList(remarkList);
    }

    @Override
    public int saveCreateCustomerRemark(CustomerRemark remark) {
        return customerRemarkMapper.insertCustomerRemark(remark);
    }

    @Override
    public int deleteCustomerRemarkById(String id) {
        return customerRemarkMapper.deleteCustomerRemarkById(id);
    }

    @Override
    public List<CustomerRemark> queryCustomerRemarkForDetailByCustomerId(String id) {
        return customerRemarkMapper.selectCustomerRemarkForDetailByCustomerId(id);
    }

    @Override
    public int saveEditCustomerRemark(CustomerRemark remark) {
        return customerRemarkMapper.updateCustomerRemark(remark);
    }


}
