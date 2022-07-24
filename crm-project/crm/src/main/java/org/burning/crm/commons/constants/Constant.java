package org.burning.crm.commons.constants;

/**
 * 专门存放常量值
 */
public class Constant {
    //保存ReturnObject类中的Code值
    public static final String RETURN_OBJECT_CODE_SUCCESS = "1";//成功
    public static final String RETURN_OBJECT_CODE_FAIL = "0";//失败

    //代表”当前用户信息“的key
    public static final String SESSION_USER = "sessionUser";

    //备注信息的”是否被修改过“标记
    public static final String REMARK_EDIT_FLAG_NO_EDITED = "0";//没有被修改过
    public static final String REMARK_EDIT_FLAG_YSE_EDITED = "1";//被修改过
}
