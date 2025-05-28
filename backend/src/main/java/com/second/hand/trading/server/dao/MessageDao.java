package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 留言/消息数据访问层（DAO）
 * 负责与数据库进行交互，操作商品留言、用户消息相关数据
 */
@Mapper
public interface MessageDao {

    /**
     * 根据主键删除留言
     * @param id 留言ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新增留言（所有字段）
     * @param record 留言实体
     * @return 影响行数
     */
    int insert(MessageModel record);

    /**
     * 新增留言（只插入非空字段）
     * @param record 留言实体
     * @return 影响行数
     */
    int insertSelective(MessageModel record);

    /**
     * 根据主键查询留言
     * @param id 留言ID
     * @return 留言实体
     */
    MessageModel selectByPrimaryKey(Long id);

    /**
     * 查询当前用户的所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    List<MessageModel> getMyMessage(Long userId);

    /**
     * 查询某商品下的所有留言
     * @param idleId 商品ID
     * @return 留言列表
     */
    List<MessageModel> getIdleMessage(Long idleId);

    /**
     * 更新留言（只更新非空字段，根据主键）
     * @param record 留言实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(MessageModel record);

    /**
     * 更新留言（所有字段，根据主键）
     * @param record 留言实体
     * @return 影响行数
     */
    int updateByPrimaryKey(MessageModel record);
}