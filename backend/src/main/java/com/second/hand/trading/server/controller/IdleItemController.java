package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.vo.ResultVo;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 闲置商品相关接口
 * 负责商品的发布、查询、分类查询、更新等功能
 */
@RestController
@RequestMapping("idle")
public class IdleItemController {

    @Autowired
    private IdleItemService idleItemService; // 注入商品业务逻辑
    @Autowired
    private UserService userService; // 注入用户业务逻辑

    /**
     * 发布闲置商品
     * @param shUserId 当前登录用户ID（从Cookie中获取）
     * @param idleItemModel 商品实体
     * @return 操作结果
     */
    @PostMapping("add")
    public ResultVo addIdleItem(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                @RequestBody IdleItemModel idleItemModel){
        // 校验用户状态，禁止被封禁用户发布商品
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user != null && user.getUserStatus() != null && user.getUserStatus() != 0) {
            return ResultVo.fail(ErrorMsg.USER_BANNED);
        }
        // 绑定用户ID、设置初始状态和发布时间
        idleItemModel.setUserId(Long.valueOf(shUserId));
        idleItemModel.setIdleStatus((byte) 1); // 默认上架
        idleItemModel.setReleaseTime(new Date());
        // 调用service发布商品
        if(idleItemService.addIdleItem(idleItemModel)){
            return ResultVo.success(idleItemModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取单个商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("info")
    public ResultVo getIdleItem(@RequestParam Long id){
        return ResultVo.success(idleItemService.getIdleItem(id));
    }

    /**
     * 获取当前用户发布的所有商品
     * @param shUserId 当前登录用户ID
     * @return 商品列表
     */
    @GetMapping("all")
    public ResultVo getAllIdleItem(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(idleItemService.getAllIdelItem(Long.valueOf(shUserId)));
    }

    /**
     * 商品模糊查询（支持分页）
     * @param findValue 查询关键字
     * @param page 页码
     * @param nums 每页数量
     * @return 查询结果
     */
    @GetMapping("find")
    public ResultVo findIdleItem(@RequestParam(value = "findValue",required = false) String findValue,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
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
        return ResultVo.success(idleItemService.findIdleItem(findValue,p,n));
    }

    /**
     * 按分类查询商品（支持分页）
     * @param idleLabel 分类标签
     * @param page 页码
     * @param nums 每页数量
     * @return 查询结果
     */
    @GetMapping("lable")
    public ResultVo findIdleItemByLable(@RequestParam(value = "idleLabel",required = true) Integer idleLabel,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.findIdleItemByLable(idleLabel,p,n));
    }

    /**
     * 更新商品信息
     * @param shUserId 当前登录用户ID
     * @param idleItemModel 商品实体
     * @return 操作结果
     */
    @PostMapping("update")
    public ResultVo updateIdleItem(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                   @RequestBody IdleItemModel idleItemModel){
        idleItemModel.setUserId(Long.valueOf(shUserId));
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
