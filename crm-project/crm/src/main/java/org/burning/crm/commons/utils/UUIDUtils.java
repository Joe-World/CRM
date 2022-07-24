package org.burning.crm.commons.utils;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 获取uuid的值
     *
     * @return 由小写字母和数字随机组成的32位字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
