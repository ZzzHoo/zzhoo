package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.FavoriteModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 收藏数据访问层（DAO）
 * 负责与数据库进行交互，操作用户收藏相关数据
 */
@Mapper
public interface FavoriteDao {

    /**
     * 根据主键删除收藏记录
     * @param id 收藏ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增收藏（所有字段）
     * @param record 收藏实体
     * @return 影响行数
     */
    int insert(FavoriteModel record);

    /**
     * 新增收藏（只插入非空字段）
     * @param record 收藏实体
     * @return 影响行数
     */
    int insertSelective(FavoriteModel record);

    /**
     * 根据主键查询收藏
     * @param id 收藏ID
     * @return 收藏实体
     */
    FavoriteModel selectByPrimaryKey(Long id);

    /**
     * 查询当前用户的所有收藏
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<FavoriteModel> getMyFavorite(Long userId);

    /**
     * 检查某用户是否收藏了某商品
     * @param userId 用户ID
     * @param idleId 商品ID
     * @return 1-已收藏，0-未收藏
     */
    Integer checkFavorite(Long userId, Long idleId);

    /**
     * 更新收藏（只更新非空字段，根据主键）
     * @param record 收藏实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(FavoriteModel record);

    /**
     * 更新收藏（所有字段，根据主键）
     * @param record 收藏实体
     * @return 影响行数
     */
    int updateByPrimaryKey(FavoriteModel record);
}