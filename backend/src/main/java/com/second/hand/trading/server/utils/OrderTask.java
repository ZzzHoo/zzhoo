package com.second.hand.trading.server.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.second.hand.trading.server.model.OrderModel;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 订单延迟任务对象
 * 用于延迟队列，支持订单超时自动处理（如自动取消未支付订单）
 * 实现了Delayed接口，可直接用于DelayQueue
 */
public class OrderTask implements Delayed {
    /**
     * 任务的触发时间（绝对时间戳，单位ms）
     * 用于判断任务何时到期
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private long time;

    /**
     * 关联的订单对象
     */
    private OrderModel orderModel;

    public OrderTask() {
        // 默认构造
    }

    /**
     * 构造方法
     * @param orderModel 关联订单
     * @param time 延迟时间（单位：秒）
     */
    public OrderTask(OrderModel orderModel, long time) {
        this.orderModel = orderModel;
        // 计算任务到期的绝对时间戳
        this.time = System.currentTimeMillis() + 1000 * time;
    }

    /**
     * 获取剩余延迟时间
     * @param unit 时间单位
     * @return 剩余时间
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    /**
     * 比较两个延迟任务的到期时间
     * @param o 另一个Delayed对象
     * @return 比较结果
     */
    @Override
    public int compareTo(Delayed o) {
        OrderTask Order = (OrderTask) o;
        long diff = this.time - Order.time;
        if (diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }

    // Getter和Setter

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    /**
     * 重写toString方法，便于日志输出和调试
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"time\":")
                .append(time);
        sb.append(",\"orderModel\":")
                .append(orderModel);
        sb.append('}');
        return sb.toString();
    }
}