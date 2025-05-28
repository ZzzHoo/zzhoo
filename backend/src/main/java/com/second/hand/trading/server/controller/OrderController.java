package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.utils.IdFactoryUtil;
import com.second.hand.trading.server.utils.OrderTaskHandler;
import com.second.hand.trading.server.vo.ResultVo;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.model.IdleItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 订单相关接口
 * 负责订单的创建、查询、更新、我的订单等功能
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService; // 注入订单业务逻辑
    @Autowired
    private UserService userService;   // 注入用户业务逻辑
    @Autowired
    private IdleItemService idleItemService; // 注入商品业务逻辑

    /**
     * 新增订单
     * @param shUserId 当前登录用户ID（从Cookie中获取）
     * @param orderModel 订单实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResultVo addOrder(@CookieValue("shUserId")
                             @NotNull(message = "登录异常 请重新登录")
                             @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                             @RequestBody OrderModel orderModel){
        // 校验用户状态，禁止被封禁用户下单
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user != null && user.getUserStatus() != null && user.getUserStatus() != 0) {
            return ResultVo.fail(ErrorMsg.USER_BANNED);
        }
        // 订单任务处理器初始化（防止静态注入丢失）
        if(OrderTaskHandler.orderService==null){
            OrderTaskHandler.orderService=orderService;
        }
        // 生成订单号、设置时间、用户、初始状态
        orderModel.setOrderNumber(IdFactoryUtil.getOrderId());
        orderModel.setCreateTime(new Date());
        orderModel.setUserId(Long.valueOf(shUserId));
        orderModel.setOrderStatus((byte) 0); // 0-待支付
        orderModel.setPaymentStatus((byte)0); // 0-未支付
        // 调用service新增订单
        if(orderService.addOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 查询订单详情
     * @param shUserId 当前登录用户ID
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/info")
    public ResultVo getOrderInfo(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                 @RequestParam Long id){
        OrderModel orderModel=orderService.getOrder(id);
        // 只有买家或卖家本人可以查
        if(orderModel.getUserId().equals(Long.valueOf(shUserId))||
                orderModel.getIdleItem().getUserId().equals(Long.valueOf(shUserId))){
            return ResultVo.success(orderModel);
        }
        ResultVo vo = ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        vo.setMsg("订单不存在或无权限");
        return vo;
    }

    /**
     * 更新订单信息（如支付、发货、收货等状态流转）
     * @param shUserId 当前登录用户ID
     * @param orderModel 订单实体
     * @param price 可选参数，价格
     * @return 操作结果
     */
    @PostMapping("/update")
    public ResultVo updateOrder(@CookieValue("shUserId")
                             @NotNull(message = "登录异常 请重新登录")
                             @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                             @RequestBody OrderModel orderModel,
                             @RequestParam(value = "price",required = false) String price){
        // 如果订单已支付，设置支付时间
        if(orderModel.getPaymentStatus()!=null&&orderModel.getPaymentStatus().equals((byte) 1)){
            orderModel.setPaymentTime(new Date());
        }
        if(orderService.updateOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 查询当前用户的所有订单（买家视角）
     * @param shUserId 当前登录用户ID
     * @return 订单列表
     */
    @GetMapping("/my")
    public ResultVo getMyOrder(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(orderService.getMyOrder(Long.valueOf(shUserId)));
    }

    /**
     * 查询当前用户卖出的所有商品订单（卖家视角）
     * @param shUserId 当前登录用户ID
     * @return 订单列表
     */
    @GetMapping("/my-sold")
    public ResultVo getMySoldIdle(@CookieValue("shUserId")
                               @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(orderService.getMySoldIdle(Long.valueOf(shUserId)));
    }
}
