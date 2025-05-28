package com.second.hand.trading.server.utils;

import com.second.hand.trading.server.service.OrderService;

import java.util.concurrent.DelayQueue;

/**
 * 订单延迟任务处理器
 * 用于处理订单的超时自动取消等延迟任务
 * 通过DelayQueue实现延迟队列，配合OrderTask使用
 */
public class OrderTaskHandler {

    // 静态注入OrderService，便于在静态线程中调用订单业务
    public static OrderService orderService = null;

    // 延迟队列，存放所有待处理的订单任务
    private static DelayQueue<OrderTask> delayQueue = new DelayQueue<>();

    /**
     * 启动延迟任务处理线程
     * 会不断轮询delayQueue，处理到期的订单任务
     */
    public static void run() {
        new Thread(() -> {
            System.out.println("Success !");
            while (true) {
                // 只有当OrderService已注入且队列不为空时才处理
                if (orderService != null && delayQueue.size() > 0) {
                    // 取出到期的订单任务（未到期则返回null）
                    OrderTask orderTask = delayQueue.poll();
                    if (orderTask != null) {
                        // 调用订单业务进行订单状态更新（如自动取消）
                        if (orderService.updateOrder(orderTask.getOrderModel())) {
                            System.out.println("成功取消订单：" + orderTask.getOrderModel());
                        } else {
                            System.out.println("取消任务：" + orderTask.getOrderModel());
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 向延迟队列中添加订单任务
     * @param o 订单延迟任务
     */
    public static void addOrder(OrderTask o) {
        System.out.println("添加任务：" + o);
        delayQueue.put(o);
    }
}
