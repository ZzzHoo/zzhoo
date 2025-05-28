package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.AdminService;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 管理员相关接口
 * 负责后台管理的登录、登出、管理员管理、商品管理、订单管理、用户管理等功能
 */
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService; // 注入管理员业务逻辑

    @Autowired
    private IdleItemService idleItemService; // 注入商品业务逻辑

    @Autowired
    private OrderService orderService; // 注入订单业务逻辑

    @Autowired
    private UserService userService; // 注入用户业务逻辑

    /**
     * 管理员登录
     * @param accountNumber 管理员账号
     * @param adminPassword 管理员密码
     * @param session HttpSession用于保存登录态
     * @return 登录结果
     */
    @GetMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotNull @NotEmpty String accountNumber,
                          @RequestParam("adminPassword") @NotNull @NotEmpty String adminPassword,
                          HttpSession session){
        AdminModel adminModel=adminService.login(accountNumber,adminPassword); // 校验账号密码
        if (null == adminModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR); // 登录失败
        }
        session.setAttribute("admin",adminModel); // 登录成功，写入session
        return ResultVo.success(adminModel);
    }

    /**
     * 管理员登出
     * @param session HttpSession
     * @return 操作结果
     */
    @GetMapping("loginOut")
    public ResultVo loginOut( HttpSession session){
        session.removeAttribute("admin"); // 移除session中的admin
        return ResultVo.success();
    }

    /**
     * 获取管理员列表（分页）
     * @param session HttpSession
     * @param page 页码
     * @param nums 每页数量
     * @return 管理员分页列表
     */
    @GetMapping("list")
    public ResultVo getAdminList(HttpSession session,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR); // 未登录或session失效
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1; // 页码校验
        }
        if(null!=nums){
            n=nums>0?nums:8; // 每页数量校验
        }
        return ResultVo.success(adminService.getAdminList(p,n));
    }

    /**
     * 新增管理员
     * @param session HttpSession
     * @param adminModel 管理员实体
     * @return 操作结果
     */
    @PostMapping("add")
    public ResultVo addAdmin(HttpSession session,
                             @RequestBody AdminModel adminModel){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR); // 权限校验
        }
        if(adminService.addAdmin(adminModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PARAM_ERROR);
    }

    /**
     * 获取商品列表（分页，按状态）
     * @param session HttpSession
     * @param status 商品状态
     * @param page 页码
     * @param nums 每页数量
     * @return 商品分页列表
     */
    @GetMapping("idleList")
    public ResultVo idleList(HttpSession session,
                             @RequestParam("status") @NotNull @NotEmpty Integer status,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR); // 权限校验
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.adminGetIdleList(status,p,n));
    }

    /**
     * 修改商品状态（如上架/下架）
     * @param session HttpSession
     * @param id 商品ID
     * @param status 新状态
     * @return 操作结果
     */
    @GetMapping("updateIdleStatus")
    public ResultVo updateIdleStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status
                                     ){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        IdleItemModel idleItemModel=new IdleItemModel();
        idleItemModel.setId(id);
        idleItemModel.setIdleStatus(status.byteValue()); // 设置新状态
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取订单列表（分页）
     * @param session HttpSession
     * @param page 页码
     * @param nums 每页数量
     * @return 订单分页列表
     */
    @GetMapping("orderList")
    public ResultVo orderList(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(orderService.getAllOrder(p,n));
    }

    /**
     * 删除订单
     * @param session HttpSession
     * @param id 订单ID
     * @return 操作结果
     */
    @GetMapping("deleteOrder")
    public ResultVo deleteOrder(HttpSession session,
                              @RequestParam("id") @NotNull @NotEmpty Long id){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if(orderService.deleteOrder(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取用户列表（分页，按状态）
     * @param session HttpSession
     * @param page 页码
     * @param nums 每页数量
     * @param status 用户状态
     * @return 用户分页列表
     */
    @GetMapping("userList")
    public ResultVo userList(HttpSession session,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums,
                             @RequestParam("status") @NotNull @NotEmpty Integer status){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(userService.getUserByStatus(status,p,n));
    }

    /**
     * 修改用户状态（如封禁/解封）
     * @param session HttpSession
     * @param id 用户ID
     * @param status 新状态
     * @return 操作结果
     */
    @GetMapping("updateUserStatus")
    public ResultVo updateUserStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        UserModel userModel=new UserModel();
        userModel.setId(id);
        userModel.setUserStatus(status.byteValue());
        if(userService.updateUserInfo(userModel))
            return ResultVo.success();
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 按商品名称模糊查询商品（支持分页和状态过滤）
     * @param findValue 查询关键字
     * @param page 页码
     * @param nums 每页数量
     * @param status 商品状态
     * @return 查询结果
     */
    @GetMapping("queryIdle")
    public ResultVo queryIdle(@RequestParam(value = "findValue",required = false) String findValue,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums,
                              @RequestParam("status") @NotNull @NotEmpty Integer status){
            if(null==findValue){
                findValue="";
            }
            int p=1;
            int n=8;
            if(null!=page){
                p=page>0?page:1;
            }
            if(null!=nums){
                n=nums>0?nums:8;
            }

        System.out.println(findValue + " " + page + " " + nums + " " + status);

            if(status == 1)
                return ResultVo.success(idleItemService.findIdleItem(findValue,p,n));
            return ResultVo.success(idleItemService.findIdleItem1(findValue,status,p,n));

    }

    /**
     * 按订单号模糊查询订单（支持分页）
     * @param session HttpSession
     * @param page 页码
     * @param nums 每页数量
     * @param searchValue 订单号关键字
     * @return 查询结果
     */
    @GetMapping("queryOrder")
    public ResultVo queryOrder(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums,
                              @RequestParam(value = "searchValue",required = false) String searchValue){

        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }

        if(null == searchValue || "".equals(searchValue))
            return ResultVo.success(orderService.getAllOrder(p,n));
        return ResultVo.success(orderService.findOrderByNumber(searchValue, p, n));
    }

    /**
     * 按用户账号模糊查询用户（支持分页和状态过滤）
     * @param session HttpSession
     * @param searchValue 用户账号关键字
     * @param mode 查询模式（1:正常用户，2:封禁用户，其他:管理员）
     * @param page 页码
     * @param nums 每页数量
     * @return 查询结果
     */
    @GetMapping("queryUser")
    public ResultVo queryUser(HttpSession session,
                              @RequestParam(value = "searchValue",required = false) String searchValue,
                              @RequestParam(value = "mode",required = false) Integer mode,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }

        // mode=1: 正常用户，mode=2: 封禁用户，其他: 管理员
        if(mode == 1){
            if(null == searchValue || "".equals(searchValue)){
                return ResultVo.success(userService.getUserByStatus(0,p,n));
            }else{
                return ResultVo.success(userService.getUserByNumber(searchValue,mode));
            }
        }else if(mode == 2){
            if(null == searchValue || "".equals(searchValue)){
                return ResultVo.success(userService.getUserByStatus(1,p,n));
            }else{
                return ResultVo.success(userService.getUserByNumber(searchValue,mode));
            }
        }else
            return ResultVo.success(adminService.getAdminList(p,n));
    }
}
