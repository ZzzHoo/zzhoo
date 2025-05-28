package com.second.hand.trading.server.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * 支付宝SDK配置类
 * 作用：从配置文件读取支付宝参数，初始化SDK全局配置
 */
@Component // 让Spring自动管理该Bean
@ConfigurationProperties(prefix = "alipay") // 绑定配置文件中以alipay开头的属性
public class AliPayConfig {
	// 支付宝应用ID
	private String appId;
	// 商户私钥
	private String appPrivateKey;
	// 支付宝公钥
	private String alipayPublicKey;
	// 支付结果异步通知回调地址
	private String notifyUrl;

	/**
	 * 项目启动后自动执行
	 * 初始化支付宝SDK全局配置
	 */
	@PostConstruct
	public void init() {
		// 创建SDK配置对象
		Config config = new Config();
		config.protocol = "https"; // 通信协议
		config.gatewayHost = "openapi.alipaydev.com"; // 沙箱网关地址
		config.signType = "RSA2"; // 签名算法
		config.appId = this.appId; // 应用ID
		config.merchantPrivateKey = this.appPrivateKey; // 商户私钥
		config.alipayPublicKey = this.alipayPublicKey; // 支付宝公钥
		config.notifyUrl = this.notifyUrl; // 异步通知回调地址
		// 设置全局配置到SDK工厂
		Factory.setOptions(config);
		System.out.println("=======支付宝SDK初始化成功=======");
	}

	// 以下为标准的getter和setter方法，供Spring自动注入和外部调用

	public String getAppId() {
		return appId;
	}

	public String getAppPrivateKey() {
		return appPrivateKey;
	}

	public String getAlipayPublicKey() {
		return alipayPublicKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppPrivateKey(String appPrivateKey) {
		this.appPrivateKey = appPrivateKey;
	}

	public void setAlipayPublicKey(String alipayPublicKey) {
		this.alipayPublicKey = alipayPublicKey;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	// 重写toString方法，便于日志输出和调试
	@Override
	public String toString() {
		return "AliPayConfig{" +
				"appId='" + appId + '\'' +
				", appPrivateKey='" + appPrivateKey + '\'' +
				", alipayPublicKey='" + alipayPublicKey + '\'' +
				", notifyUrl='" + notifyUrl + '\'' +
				'}';
	}
}