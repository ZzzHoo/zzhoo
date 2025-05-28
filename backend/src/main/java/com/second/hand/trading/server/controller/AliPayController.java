package com.second.hand.trading.server.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.second.hand.trading.server.service.OrderService;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(value = "/alipay")
public class AliPayController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/pay")
	public String pay(@RequestParam(value = "orderIds", required = false) String orderIds) {
		try {
			if (orderIds == null || orderIds.isEmpty()) {
				throw new RuntimeException("订单ID不能为空");
			}

			// 解析订单ID
			List<String> orderIdList = Arrays.asList(orderIds.split(","));

			// 计算总价
			// BigDecimal totalAmount = new BigDecimal(0);
			// for (String orderId : orderIdList) {
			// 	totalAmount = totalAmount.add(orderService.getOrder(Long.valueOf(orderId)).getOrderPrice());
			// }

			// 直接模拟支付成功
			for (String orderId : orderIdList) {
				orderService.updateOrderPaymentStatus(Long.valueOf(orderId), 1, "模拟支付");
			}
			return "<html><body><h2>模拟支付成功！</h2><p>订单已标记为已支付，请返回订单页面查看。</p></body></html>";
		} catch (Exception e) {
			System.err.println("模拟支付异常，原因：" + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@PostMapping("/notify")
	public String notify(@RequestParam Map<String, String> params) {
		try {
			// 验证签名
			if (Factory.Payment.Common().verifyNotify(params)) {
				// 交易成功
				if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
					String outTradeNo = params.get("out_trade_no"); // 商户订单号
					
					// 解析批量订单号
					if (outTradeNo.startsWith("BATCH_")) {
						String[] parts = outTradeNo.split("_");
						String[] orderIds = parts[2].split(",");
						
						boolean allSuccess = true;
						// 更新所有订单的支付状态
						for (String orderId : orderIds) {
							try {
								if (!orderService.updateOrderPaymentStatus(Long.valueOf(orderId), 1, "支付宝")) {
									allSuccess = false;
									System.err.println("更新订单 " + orderId + " 状态失败");
								}
							} catch (Exception e) {
								allSuccess = false;
								System.err.println("更新订单 " + orderId + " 状态异常：" + e.getMessage());
							}
						}
						
						return allSuccess ? "success" : "failure";
					}
					
					return "success";
				}
			}
			return "failure";
		} catch (Exception e) {
			System.err.println("支付回调处理异常：" + e.getMessage());
			return "failure";
		}
	}
}
