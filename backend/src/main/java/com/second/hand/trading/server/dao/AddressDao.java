package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.AddressModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 收货地址数据访问层（DAO）
 * 负责与数据库进行交互，操作用户收货地址相关数据
 */
@Mapper
public interface AddressDao {

    /**
     * 根据主键和用户ID删除地址（防止越权删除）
     * @param id 地址ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByPrimaryKeyAndUser(Long id, Long userId);

    /**
     * 新增地址（所有字段）
     * @param record 地址实体
     * @return 影响行数
     */
    int insert(AddressModel record);

    /**
     * 新增地址（只插入非空字段）
     * @param record 地址实体
     * @return 影响行数
     */
    int insertSelective(AddressModel record);

    /**
     * 根据主键查询地址
     * @param id 地址ID
     * @return 地址实体
     */
    AddressModel selectByPrimaryKey(Long id);

    /**
     * 查询用户的所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressModel> getAddressByUser(Long userId);

    /**
     * 查询用户的默认地址
     * @param userId 用户ID
     * @return 默认地址列表
     */
    List<AddressModel> getDefaultAddress(Long userId);

    /**
     * 更新地址（只更新非空字段，根据主键）
     * @param record 地址实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(AddressModel record);

    /**
     * 根据用户ID批量更新地址（只更新非空字段）
     * @param record 地址实体
     * @return 影响行数
     */
    int updateByUserIdSelective(AddressModel record);

    /**
     * 更新地址（所有字段，根据主键）
     * @param record 地址实体
     * @return 影响行数
     */
    int updateByPrimaryKey(AddressModel record);
}