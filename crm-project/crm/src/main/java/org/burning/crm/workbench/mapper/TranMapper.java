package org.burning.crm.workbench.mapper;

import org.burning.crm.workbench.domain.FunnelVO;
import org.burning.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    int insert(Tran record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    int insertSelective(Tran record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    Tran selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    int updateByPrimaryKeySelective(Tran record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran
     *
     * @mbggenerated Sat Jul 16 12:16:41 CST 2022
     */
    int updateByPrimaryKey(Tran record);

    /**
     * 创建交易
     * @param tran
     * @return
     */
    int insertTran(Tran tran);

    /**
     * 根据交易id查询交易明细信息
     * @param id
     * @return
     */
    Tran selectTranForDetailById(String id);

    /**
     * 查询交易表中各个阶段中的数据量
     * @return
     */
    List<FunnelVO> selectCountOfTranGroupByStage();

    /**
     * 根据客户id查询所有相关的交易信息
     * @param id
     * @return
     */
    List<Tran> selectTranByCustomerId(String id);

    /**
     * 根据客户id查询所有相关的交易信息
     * @param id
     * @return
     */
    List<Tran> selectTranByContactsId(String id);

    /**
     * 根据交易id删除交易
     * @param id
     * @return
     */
    int deleteTranById(String id);

    /**
     * 根据条件分页查询，获取符合条件的交易信息
     * @param map
     * @return
     */
    List<Tran> selectTranByConditionForPage(Map<String, Object> map);

    /**
     * 根据条件分页查询，获取符合条件的交易信息条数
     * @param map
     * @return
     */
    int selectCountOfTranByCondition(Map<String, Object> map);

    /**
     * 批量删除交易
     * @param id
     * @return
     */
    int deleteTranByIds(String[] id);

    /**
     * 根据交易id查询交易信息
     * @param id
     * @return
     */
    Tran selectTranById(String id);
}