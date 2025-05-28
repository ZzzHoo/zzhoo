package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.OrderAddressModel;
import com.second.hand.trading.server.service.OrderAddressService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 订单收货地址相关接口
 * 负责订单的收货地址添加、修改、查询等功能
 */
@RestController
@RequestMapping("/order-address")
public class OrderAddressController {

    @Autowired
    private OrderAddressService orderAddressService; // 注入订单地址业务逻辑

    /**
     * 新增订单收货地址
     * @param shUserId 当前登录用户ID（从Cookie中获取）
     * @param orderAddressModel 订单地址实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResultVo addOrderAddress(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestBody OrderAddressModel orderAddressModel){
        // 直接调用service添加订单地址
        return ResultVo.success(orderAddressService.addOrderAddress(orderAddressModel));
    }

    /**
     * 修改订单收货地址
     * @param shUserId 当前登录用户ID
     * @param orderAddressModel 订单地址实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public ResultVo updateOrderAddress(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                       @RequestBody OrderAddressModel orderAddressModel){
        // 调用service更新订单地址
        if(orderAddressService.updateOrderAddress(orderAddressModel)){
            return ResultVo.success(orderAddressModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 查询订单收货地址
     * @param shUserId 当前登录用户ID
     * @param orderId 订单ID
     * @return 订单收货地址详情
     */
    @GetMapping("/info")
    public ResultVo getOrderAddress(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestParam Long orderId){
        // 根据订单ID查询收货地址
        return ResultVo.success(orderAddressService.getOrderAddress(orderId));
    }
}
