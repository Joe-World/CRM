package org.burning.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 关于excel文件操作的工具类
 */
public class HSSFUtils {
    /**
     * 从指定的HSSFCell对象中获取列的值
     *
     * @return 列值(String)
     */
    public static String getCellValueForStr(HSSFCell cell) {
        String ret = "";
        //根据cell数据类型的不同，调用不同的”get方法“
        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            ret = cell.getStringCellValue();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            ret = cell.getNumericCellValue() + "";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            ret = cell.getBooleanCellValue() + "";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            ret = cell.getCellFormula();
        } else {
            ret = "";
        }

        return ret;
    }
}
