package com.second.hand.trading.server;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 请求耗时日志拦截器
 * 用于统计每个HTTP请求的处理耗时，便于性能分析和调试
 */
public class LogCostInterceptor implements HandlerInterceptor {
    // 记录请求开始时间
    private long start = System.currentTimeMillis();

    /**
     * 请求处理前执行
     * 记录开始时间
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        start = System.currentTimeMillis();
        return true; // 返回true继续后续处理
    }

    /**
     * 请求处理后执行（Controller方法调用后，视图渲染前）
     * 打印请求URI、参数和耗时
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("请求：" + httpServletRequest.getRequestURI() + "?" + httpServletRequest.getQueryString()
                + "，耗时" + (System.currentTimeMillis() - start) + "ms  " + new Date());
    }

    /**
     * 请求完成后执行（视图渲染后）
     * 这里未做处理，可用于资源清理等
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // 可选：处理异常、资源释放等
    }
}