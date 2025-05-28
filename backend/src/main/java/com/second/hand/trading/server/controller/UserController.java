package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 用户相关接口
 * 负责用户的注册、登录、登出、信息获取与修改、密码重置等功能
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService; // 注入用户业务逻辑

    /**
     * 用户注册
     * @param userModel 用户实体
     * @return 操作结果
     */
    @PostMapping("sign-in")
    public ResultVo signIn(@RequestBody  UserModel userModel) {
        System.out.println(userModel);
        // 校验邮箱格式
        if (userModel.getEmail() == null || !userModel.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            ResultVo vo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            vo.setMsg("邮箱格式不正确");
            return vo;
        }
        // 校验性别
        if (userModel.getGender() == null || !(userModel.getGender().equals("male") || userModel.getGender().equals("female"))) {
            ResultVo vo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            vo.setMsg("性别必须为'male'或'female'");
            return vo;
        }
        // 校验银行账号
        if (userModel.getBankAccount() == null || !userModel.getBankAccount().matches("^\\d{16}$")) {
            ResultVo vo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            vo.setMsg("银行账号必须为16位数字");
            return vo;
        }
        // 设置注册时间
        userModel.setSignInTime(new Timestamp(System.currentTimeMillis()));
        // 设置默认头像
        if (userModel.getAvatar() == null || "".equals(userModel.getAvatar())) {
            userModel.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }
        // 调用service注册
        if (userService.userSignIn(userModel)) {
            return ResultVo.success(userModel);
        }
        return ResultVo.fail(ErrorMsg.REGISTER_ERROR);
    }

    /**
     * 用户登录
     * @param accountNumber 账号
     * @param userPassword 密码
     * @param response HttpServletResponse
     * @return 登录结果
     */
    @RequestMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotEmpty @NotNull String accountNumber,
                          @RequestParam("userPassword") @NotEmpty @NotNull String userPassword,
                          HttpServletResponse response) {
        UserModel userModel = userService.userLogin(accountNumber, userPassword);
        System.out.println("登录：" + userModel);

        System.out.println("===================================");
        System.out.println(accountNumber + "   " + userPassword);
        System.out.println("===================================");

        if (null == userModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        // 用户名或者密码为空
        if(accountNumber.equals("") || userPassword.equals("")){
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        // 手机号长度不足11位
        if(userModel.getAccountNumber().length() != 11){
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        // 用户被封禁
        if(userModel.getUserStatus()!=null&&userModel.getUserStatus().equals((byte) 1)){
            return ResultVo.fail(ErrorMsg.ACCOUNT_Ban);
        }

        // 如果手机号未设置，则用账号补全
        if(userModel.getPhone() == null || userModel.getPhone().isEmpty()){
            userModel.setPhone(userModel.getAccountNumber());
            userService.updateUserInfo(userModel);
        }

        // 登录成功，写入Cookie
        Cookie cookie = new Cookie("shUserId", String.valueOf(userModel.getId()));
//        cookie.setMaxAge(60 * 60 * 24 * 30);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return ResultVo.success(userModel);
    }

    /**
     * 用户登出
     * @param shUserId 用户ID（从Cookie中获取）
     * @param response HttpServletResponse
     * @return 操作结果
     */
    @RequestMapping("logout")
    public ResultVo logout(@CookieValue("shUserId")
                           @NotNull(message = "登录异常 请重新登录")
                           @NotEmpty(message = "登录异常 请重新登录") String shUserId, HttpServletResponse response) {
        // 清除Cookie
        Cookie cookie = new Cookie("shUserId", shUserId);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResultVo.success();
    }

    /**
     * 获取当前用户信息
     * @param id 用户ID（从Cookie中获取）
     * @return 用户信息
     */
    @GetMapping("info")
    public ResultVo getOneUser(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录")
                                       String id) {
        return ResultVo.success(userService.getUser(Long.valueOf(id)));
    }

    /**
     * 修改用户公开信息
     * @param id 用户ID（从Cookie中获取）
     * @param userModel 用户实体
     * @return 操作结果
     */
    @PostMapping("/info")
    public ResultVo updateUserPublicInfo(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                     @NotEmpty(message = "登录异常 请重新登录")
                                             String id, @RequestBody  UserModel userModel) {
        userModel.setId(Long.valueOf(id));
        if (userService.updateUserInfo(userModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 修改密码
     * @param id 用户ID（从Cookie中获取）
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @GetMapping("/password")
    public ResultVo updateUserPassword(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String id,
                                       @RequestParam("oldPassword") @NotEmpty @NotNull String oldPassword,
                                       @RequestParam("newPassword") @NotEmpty @NotNull String newPassword) {
        if (userService.updatePassword(newPassword,oldPassword,Long.valueOf(id))) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PASSWORD_RESET_ERROR);
    }

    /**
     * 通过用户ID获取用户基本信息（只返回公开字段）
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/getUserById")
    public ResultVo getUserById(@RequestParam("userId") @NotNull Long userId) {
        UserModel userModel = userService.getUser(userId);
        if (userModel != null) {
            // 仅返回必要的公开信息，避免敏感信息泄露
            UserModel safeUserModel = new UserModel();
            safeUserModel.setId(userModel.getId());
            safeUserModel.setNickname(userModel.getNickname());
            safeUserModel.setAvatar(userModel.getAvatar());
            safeUserModel.setSignature(userModel.getSignature());
            // 可以根据需要添加其他非敏感字段
            
            return ResultVo.success(safeUserModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
