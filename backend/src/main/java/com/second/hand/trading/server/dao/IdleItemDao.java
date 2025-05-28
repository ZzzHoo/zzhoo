package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.IdleItemModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 闲置商品数据访问层（DAO）
 * 负责与数据库进行交互，操作商品相关数据
 */
@Mapper
public interface IdleItemDao {

    /**
     * 根据主键删除商品
     * @param id 商品ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增商品（所有字段）
     * @param record 商品实体
     * @return 影响行数
     */
    int insert(IdleItemModel record);

    /**
     * 新增商品（只插入非空字段）
     * @param record 商品实体
     * @return 影响行数
     */
    int insertSelective(IdleItemModel record);

    /**
     * 根据主键查询商品
     * @param id 商品ID
     * @return 商品实体
     */
    IdleItemModel selectByPrimaryKey(Long id);

    /**
     * 查询用户发布的所有商品
     * @param userId 用户ID
     * @return 商品列表
     */
    List<IdleItemModel> getAllIdleItem(Long userId);

    /**
     * 模糊查询商品总数
     * @param findValue 查询关键字
     * @return 商品数量
     */
    int countIdleItem(String findValue);

    /**
     * 按分类统计商品总数
     * @param idleLabel 分类标签
     * @return 商品数量
     */
    int countIdleItemByLable(int idleLabel);

    /**
     * 按状态统计商品总数
     * @param status 商品状态
     * @return 商品数量
     */
    int countIdleItemByStatus(int status);

    /**
     * 模糊查询商品（分页）
     * @param findValue 查询关键字
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 商品列表
     */
    List<IdleItemModel> findIdleItem(String findValue, int begin, int nums);

    /**
     * 模糊+状态查询商品（分页）
     * @param findValue 查询关键字
     * @param status 商品状态
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 商品列表
     */
    List<IdleItemModel> findIdleItem1(String findValue, int status, int begin, int nums);

    /**
     * 按分类查询商品（分页）
     * @param idleLabel 分类标签
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 商品列表
     */
    List<IdleItemModel> findIdleItemByLable(int idleLabel, int begin, int nums);

    /**
     * 按状态查询商品（分页）
     * @param status 商品状态
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 商品列表
     */
    List<IdleItemModel> getIdleItemByStatus(int status, int begin, int nums);

    /**
     * 更新商品（只更新非空字段，根据主键）
     * @param record 商品实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(IdleItemModel record);

    /**
     * 更新商品（所有字段，根据主键）
     * @param record 商品实体
     * @return 影响行数
     */
    int updateByPrimaryKey(IdleItemModel record);

    /**
     * 根据ID列表批量查询商品
     * @param idList 商品ID列表
     * @return 商品列表
     */
    List<IdleItemModel> findIdleByList(List<Long> idList);
}