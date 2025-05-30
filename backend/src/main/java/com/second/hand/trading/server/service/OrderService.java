package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.vo.PageVo;

import java.util.List;

public interface OrderService {

    /**
     * 新增订单
     * @param orderModel
     * @return
     */
    boolean addOrder(OrderModel orderModel);

    /**
     * 获取订单信息
     * @param id
     * @return
     */
    OrderModel getOrder(Long id);

    // 通过订单编号获取订单

    PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums);

    /**
     *  获取订单信息
     * @param name
     * */

    /**
     * 更新订单信息
     * @param orderModel
     * @return
     */
    boolean updateOrder(OrderModel orderModel);

    /**
     * 获取某个用户买到的闲置的订单列表
     * @param userId
     * @return
     */
    List<OrderModel> getMyOrder(Long userId);

    /**
     * 获取某个用户卖出的闲置的订单信息
     * @param userId
     * @return
     */
    List<OrderModel> getMySoldIdle(Long userId);

    PageVo<OrderModel> getAllOrder(int page, int nums);

    boolean deleteOrder(long id);

    /**
     * 更新订单支付状态
     * @param orderId 订单ID
     * @param paymentStatus 支付状态
     * @param paymentWay 支付方式
     * @return 更新是否成功
     */
    boolean updateOrderPaymentStatus(Long orderId, int paymentStatus, String paymentWay);
}
