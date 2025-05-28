package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.AdminModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员数据访问层（DAO）
 * 负责与数据库进行交互，操作管理员相关数据
 */
@Mapper
public interface AdminDao {

    /**
     * 根据主键删除管理员
     * @param id 管理员ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增管理员（所有字段）
     * @param record 管理员实体
     * @return 影响行数
     */
    int insert(AdminModel record);

    /**
     * 新增管理员（只插入非空字段）
     * @param record 管理员实体
     * @return 影响行数
     */
    int insertSelective(AdminModel record);

    /**
     * 根据主键查询管理员
     * @param id 管理员ID
     * @return 管理员实体
     */
    AdminModel selectByPrimaryKey(Long id);

    /**
     * 更新管理员（只更新非空字段，根据主键）
     * @param record 管理员实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(AdminModel record);

    /**
     * 更新管理员（所有字段，根据主键）
     * @param record 管理员实体
     * @return 影响行数
     */
    int updateByPrimaryKey(AdminModel record);

    /**
     * 管理员登录
     * @param accountNumber 管理员账号
     * @param adminPassword 管理员密码
     * @return 管理员实体
     */
    AdminModel login(@Param("accountNumber") String accountNumber, @Param("adminPassword") String adminPassword);

    /**
     * 分页获取管理员列表
     * @param begin 起始下标
     * @param nums 每页数量
     * @return 管理员列表
     */
    List<AdminModel> getList(int begin, int nums);

    /**
     * 获取管理员总数
     * @return 管理员数量
     */
    int getCount();
}