package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.vo.PageVo;

/**
 * 管理员相关业务接口
 * 该接口定义了管理员模块的核心业务方法，供Service实现类实现。
 */
public interface AdminService {

    /**
     * 管理员登录
     * @param accountNumber 管理员账号
     * @param adminPassword 管理员密码
     * @return 登录成功返回AdminModel对象，失败返回null
     */
    AdminModel login(String accountNumber, String adminPassword);

    /**
     * 分页获取管理员列表
     * @param page 页码
     * @param nums 每页数量
     * @return 分页封装的管理员列表
     */
    PageVo<AdminModel> getAdminList(int page, int nums);

    /**
     * 新增管理员
     * @param adminModel 管理员实体
     * @return 添加成功返回true，失败返回false
     */
    boolean addAdmin(AdminModel adminModel);

}
