package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;



/**
 * 用户服务实现类
 * 功能：
 *   - 用户登录/注册
 *   - 用户信息管理
 *   - 密码修改
 *   - 用户状态管理(正常/封禁)
 * 安全提示：
 *   - 登录逻辑需增强安全验证
 *   - 信息修改需添加权限校验
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    /**
     * 查询一个用户的公开信息
     * @param id
     * @return
     */
    public UserModel getUser(Long id){
        return userDao.selectByPrimaryKey(id);
    }

    /**
     * 登录，安全问题未解决
     * @param accountNumber
     * @param userPassword
     * @return
     */
    public UserModel userLogin(String accountNumber, String userPassword){
        return userDao.userLogin(accountNumber,userPassword);
    }

    /**
     *注册
     * @param userModel
     * @return
     */
    public boolean userSignIn(UserModel userModel){
        return userDao.insert(userModel) == 1;
    }

    /**
     *修改用户公开信息，未验证用户身份
     * @param userModel
     * @return
     */
    public boolean updateUserInfo(UserModel userModel){
        return userDao.updateByPrimaryKeySelective(userModel)==1;
    }

    /**
     * 修改密码
     * @param newPassword
     * @param oldPassword
     * @param id
     * @return
     */
    public boolean updatePassword(String newPassword, String oldPassword,Long id){
        return userDao.updatePassword(newPassword,oldPassword,id)==1;
    }

    public PageVo<UserModel> getUserByStatus(int status,int page ,int nums){
        List<UserModel> list;
        int count=0;
        if(status==0){
            count=userDao.countNormalUser();
            list=userDao.getNormalUser((page-1)*nums, nums);
        }else {
            count=userDao.countBanUser();
            list=userDao.getBanUser((page-1)*nums, nums);
        }
        return new PageVo<>(list,count);
    }


    /**
     * 通过用户的姓名查询用户的id
     *
     * */
	@Override
	public Long getUserId(String nickname) {
		return userDao.selectByUserName(nickname);
	}


	/**
     * 通过用户账号查询用户信息
     *
     * @return*/
    @Override
    public PageVo<UserModel> getUserByNumber(String searchValue, int mode) {
        List<UserModel> list;

//        System.out.println("-------------------" + 123 + "-----------------------");
        list=userDao.getUserByNumber(searchValue, mode-1);
//
//        System.out.println("-------------------" + 456 + "-----------------------");
//        for (UserModel i: list) {
//            System.out.println(i.getNickname() + " " + i.getUserStatus());
//        }
//        System.out.println("-------------------" + 789 + "-----------------------");
        return new PageVo<>(list, 1);
    }

}
