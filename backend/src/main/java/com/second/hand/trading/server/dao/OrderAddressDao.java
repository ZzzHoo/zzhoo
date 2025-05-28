package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.OrderAddressModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单收货地址数据访问层（DAO）
 * 负责与数据库进行交互，操作订单收货地址相关数据
 */
@Mapper
public interface OrderAddressDao {

    /**
     * 根据主键删除订单收货地址
     * @param id 地址ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增订单收货地址（所有字段）
     * @param record 地址实体
     * @return 影响行数
     */
    int insert(OrderAddressModel record);

    /**
     * 新增订单收货地址（只插入非空字段）
     * @param record 地址实体
     * @return 影响行数
     */
    int insertSelective(OrderAddressModel record);

    /**
     * 根据主键查询订单收货地址
     * @param id 地址ID
     * @return 地址实体
     */
    OrderAddressModel selectByPrimaryKey(Long id);

    /**
     * 根据订单ID查询订单收货地址
     * @param orderId 订单ID
     * @return 地址实体
     */
    OrderAddressModel selectByOrderId(Long orderId);

    /**
     * 更新订单收货地址（只更新非空字段，根据主键）
     * @param record 地址实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(OrderAddressModel record);

    /**
     * 更新订单收货地址（所有字段，根据主键）
     * @param record 地址实体
     * @return 影响行数
     */
    int updateByPrimaryKey(OrderAddressModel record);
}