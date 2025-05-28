package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.OrderModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单数据访问层（DAO）
 * 负责与数据库进行交互，操作订单相关数据
 */
@Mapper
public interface OrderDao {

    /**
     * 根据主键删除订单
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增订单（所有字段）
     * @param record 订单实体
     * @return 影响行数
     */
    int insert(OrderModel record);

    /**
     * 新增订单（只插入非空字段）
     * @param record 订单实体
     * @return 影响行数
     */
    int insertSelective(OrderModel record);

    /**
     * 根据主键查询订单
     * @param id 订单ID
     * @return 订单实体
     */
    OrderModel selectByPrimaryKey(Long id);

    /**
     * 查询当前用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderModel> getMyOrder(Long userId);

    /**
     * 分页查询所有订单
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 订单列表
     */
    List<OrderModel> getAllOrder(int begin, int nums);

    /**
     * 根据订单号模糊查询订单（分页）
     * @param searchValue 订单号关键字
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 订单列表
     */
    List<OrderModel> getOrderByNumber(String searchValue, int begin, int nums);

    /**
     * 查询订单总数
     * @return 订单数量
     */
    int countAllOrder();

    /**
     * 根据商品ID列表批量查询订单
     * @param idleIdList 商品ID列表
     * @return 订单列表
     */
    List<OrderModel> findOrderByIdleIdList(List<Long> idleIdList);

    /**
     * 更新订单（只更新非空字段，根据主键）
     * @param record 订单实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(OrderModel record);

    /**
     * 更新订单（所有字段，根据主键）
     * @param record 订单实体
     * @return 影响行数
     */
    int updateByPrimaryKey(OrderModel record);
}