package org.burning.crm.settings.service;

import org.burning.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User queryUserByLoginActAndPwd(Map<String,Object> map);

    List<User> queryAllUsers();
}
