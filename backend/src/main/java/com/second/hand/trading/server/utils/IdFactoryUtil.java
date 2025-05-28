package com.second.hand.trading.server.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 * 用于生成全局唯一的订单号、文件名等，保证并发环境下不重复
 */
public class IdFactoryUtil {

    // 订单号自增部分，原子操作保证线程安全
    private static AtomicInteger orderIdEnd = new AtomicInteger(1);
    // 文件ID自增部分，原子操作保证线程安全
    private static AtomicInteger fileIdEnd = new AtomicInteger(1);

    /**
     * 生成订单号
     * 规则：当前时间戳 + 4位自增数（循环递增，防止并发冲突）
     * @return 唯一订单号字符串
     */
    public static String getOrderId() {
        int newI;
        int ord;
        do {
            ord = orderIdEnd.get();
            newI = (ord + 1) % 10000; // 保证自增部分始终为4位
        } while (!orderIdEnd.compareAndSet(ord, newI)); // CAS原子操作，防止并发冲突
        return System.currentTimeMillis() + "" + (newI + 10000); // 拼接时间戳和自增部分
    }

    /**
     * 生成文件ID
     * 规则：当前时间戳 + 3位自增数（循环递增，防止并发冲突）
     * @return 唯一文件ID字符串
     */
    public static String getFileId() {
        int newI;
        int ord;
        do {
            ord = fileIdEnd.get();
            newI = (ord + 1) % 1000; // 保证自增部分始终为3位
        } while (!fileIdEnd.compareAndSet(ord, newI)); // CAS原子操作，防止并发冲突
        return System.currentTimeMillis() + "" + (newI + 1000); // 拼接时间戳和自增部分
    }
}
