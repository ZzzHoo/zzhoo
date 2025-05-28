/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80200
 Source Host           : localhost:3306
 Source Schema         : 2022011263

 Target Server Type    : MySQL
 Target Server Version : 80200
 File Encoding         : 65001

 Date: 24/05/2025 17:39:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sh_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_address`;
CREATE TABLE `sh_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人姓名',
  `consignee_phone` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人手机号',
  `province_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '省',
  `city_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '市',
  `region_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '区',
  `detail_address` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '详细地址',
  `default_flag` tinyint NOT NULL COMMENT '是否默认地址',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_address
-- ----------------------------
INSERT INTO `sh_address` VALUES (36, '王国栋', '12345678910', '男生宿舍', '14号楼', '二层', '205，楼道东边，靠着厕所', 1, 40);
INSERT INTO `sh_address` VALUES (41, '张皓', '17610635317', '男生宿舍', '10号楼', '二层', '1', 1, 44);
INSERT INTO `sh_address` VALUES (42, '张皓', '12323232131', '女生宿舍', '7号楼', '四层', '', 1, 45);
INSERT INTO `sh_address` VALUES (43, '甲', '11111111111', '男生宿舍', '学一A', '一层', '学一A 1001', 1, 36);
INSERT INTO `sh_address` VALUES (44, '乙', '22222222222', '男生宿舍', '学二B', '二层', '学二B 2020', 1, 37);

-- ----------------------------
-- Table structure for sh_admin
-- ----------------------------
DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `admin_name` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员名字',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_admin
-- ----------------------------
INSERT INTO `sh_admin` VALUES (1, 'admin', '123456', '管理员');
INSERT INTO `sh_admin` VALUES (2, 'admin1', '123456', '管理员二号');

-- ----------------------------
-- Table structure for sh_favorite
-- ----------------------------
DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
  `create_time` datetime NOT NULL COMMENT '加入收藏的时间',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物主键id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC, `idle_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '收藏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_favorite
-- ----------------------------
INSERT INTO `sh_favorite` VALUES (74, '2025-05-21 12:10:39', 36, 194);

-- ----------------------------
-- Table structure for sh_idle_item
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '闲置物名称',
  `idle_details` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '详情',
  `picture_list` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图集',
  `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
  `idle_place` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '发货地区',
  `idle_label` int NOT NULL COMMENT '分类标签',
  `release_time` datetime NOT NULL COMMENT '发布时间',
  `idle_status` tinyint NOT NULL COMMENT '状态（发布1、下架2、删除0）',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 202 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '二手商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_idle_item
-- ----------------------------
INSERT INTO `sh_idle_item` VALUES (192, '索尼耳机wh-1000XM6', '未拆全新出售', '[\"http://localhost:8080/image?imageName=file17478280070301002b77fd33c2015d08c8c4f63434d6e3cc.jpg\"]', 3000.00, '学一A', 1, '2025-05-21 11:46:54', 1, 36);
INSERT INTO `sh_idle_item` VALUES (193, '考研资料', '拆封无笔迹', '[\"http://localhost:8080/image?imageName=file17478287774771003b3d31e755bc0008837c58fa6c079df8.jpg\"]', 80.00, '学一A', 4, '2025-05-21 11:59:42', 1, 36);
INSERT INTO `sh_idle_item` VALUES (194, '羽毛球拍', '自用八成新', '[\"http://localhost:8080/image?imageName=file174782894917410048cd31aa4f028a5c182daed92acd0fb9.jpg\"]', 800.00, '学二B', 3, '2025-05-21 12:02:32', 1, 37);
INSERT INTO `sh_idle_item` VALUES (195, '吹风机', '七成新自用', '[\"http://localhost:8080/image?imageName=file17478290031811005fd427fcdf164ec98c3081010b965278.jpg\"]', 300.00, '学二B', 2, '2025-05-21 12:03:25', 1, 37);
INSERT INTO `sh_idle_item` VALUES (196, '英语考研资料', '全新未拆封', '[\"http://localhost:8080/image?imageName=file174782905429310063e57047dadb9b80e3bfec72e3273404.jpg\"]', 50.00, '学三A', 4, '2025-05-21 12:04:17', 1, 38);
INSERT INTO `sh_idle_item` VALUES (197, '华为手机', '自用99新，急出', '[\"http://localhost:8080/image?imageName=file17478291001691007b16b824ef1e9290a7d19027e7f35a4d.jpg\"]', 5000.00, '学三A', 1, '2025-05-21 12:05:02', 1, 38);
INSERT INTO `sh_idle_item` VALUES (198, '乒乓球拍', '全新仅拆封', '[\"http://localhost:8080/image?imageName=file17478291729311008d883e43e5a9ea67df3e82a87c1aecf8.jpg\"]', 150.00, '学四B', 3, '2025-05-21 12:06:14', 2, 39);
INSERT INTO `sh_idle_item` VALUES (199, '衣架十个', '五成新', '[\"http://localhost:8080/image?imageName=file17478292359711009d7770f337793f28c86fde580631ac46.jpg\"]', 20.00, '学四B', 2, '2025-05-21 12:07:19', 2, 39);
INSERT INTO `sh_idle_item` VALUES (200, '求购闲置相机', '希望价格在4000左右', '[\"http://localhost:8080/image?imageName=file17478293517341010e97dc5e5ab37ff75f59f89afcb99ff4.jpg\"]', 0.00, '学一A', 5, '2025-05-21 12:09:15', 1, 36);
INSERT INTO `sh_idle_item` VALUES (201, '鼠标', '自用八成新', '[\"http://localhost:8080/image?imageName=file17478293986341011a1fd0d47b6b16ea2cd44928d4caf42f.jpg\"]', 400.00, '学一A', 1, '2025-05-21 12:10:00', 1, 36);

-- ----------------------------
-- Table structure for sh_message
-- ----------------------------
DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置主键id',
  `content` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '留言内容',
  `create_time` datetime NOT NULL COMMENT '留言时间',
  `to_user` bigint NOT NULL COMMENT '所回复的用户',
  `to_message` bigint NULL DEFAULT NULL COMMENT '所回复的留言',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `idle_id_index`(`idle_id` ASC) USING BTREE,
  INDEX `to_user_index`(`to_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '留言表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_message
-- ----------------------------
INSERT INTO `sh_message` VALUES (60, 36, 200, '有闲置的可以联系我', '2025-05-21 12:11:41', 36, NULL);
INSERT INTO `sh_message` VALUES (61, 37, 200, '我有闲置相机，怎么联系你', '2025-05-21 12:12:11', 36, NULL);
INSERT INTO `sh_message` VALUES (62, 36, 197, '不如苹果一根', '2025-05-24 09:13:29', 38, NULL);

-- ----------------------------
-- Table structure for sh_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
  `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `payment_status` tinyint NOT NULL COMMENT '支付状态',
  `payment_way` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL COMMENT '订单状态',
  `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  `refund_status` tinyint NULL DEFAULT 0 COMMENT '退款/退货状态 0=无 1=买家申请中 2=卖家同意 3=卖家拒绝 4=退款完成',
  `refund_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '买家退货理由',
  `refund_reply` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '卖家退货回复',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款/退货处理时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 153 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_order
-- ----------------------------
INSERT INTO `sh_order` VALUES (152, '174782944325110002', 36, 199, 20.00, 1, '支付宝', '2025-05-21 12:10:43', '2025-05-21 12:10:50', 1, NULL, 0, NULL, NULL, NULL);
INSERT INTO `sh_order` VALUES (153, '174800154163110002', 36, 198, 150.00, 1, '支付宝', '2025-05-23 11:59:02', '2025-05-23 11:59:04', 1, NULL, 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sh_order_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人',
  `consignee_phone` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '电话',
  `detail_address` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderId`(`order_id` ASC) USING BTREE,
  INDEX `order_id_index`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 135 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_order_address
-- ----------------------------
INSERT INTO `sh_order_address` VALUES (134, 152, '甲', '11111111111', '男生宿舍学一A一层学一A 1001');
INSERT INTO `sh_order_address` VALUES (135, 153, '甲', '11111111111', '男生宿舍学一A一层学一A 1001');

-- ----------------------------
-- Table structure for sh_user
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '账号（手机号）',
  `user_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '登录密码',
  `nickname` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '头像',
  `sign_in_time` datetime NOT NULL COMMENT '注册时间',
  `user_status` tinyint NULL DEFAULT NULL COMMENT '状态（1代表封禁）',
  `student_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '学号',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `bank_account` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '银行账号',
  `signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_user
-- ----------------------------
INSERT INTO `sh_user` VALUES (36, '11111111111', '123456', '甲', 'http://localhost:8080/image?imageName=file17478312851871005b4ec94c758d00588f7eb6dc34087c27.jpg', '2025-05-21 00:53:55', 0, NULL, '11111111111', NULL, NULL, NULL, '我是帅哥');
INSERT INTO `sh_user` VALUES (37, '22222222222', '123456', '乙', 'http://localhost:8080/image?imageName=file17478311935201002b5bff0e84d4399b9b59295af412b5a9.jpg', '2025-05-21 11:20:58', 0, NULL, '22222222222', NULL, NULL, NULL, '');
INSERT INTO `sh_user` VALUES (38, '33333333333', '123456', '丙', 'http://localhost:8080/image?imageName=file17478312535831003033ff11ed458af1b878a50fa56870bc.jpg', '2025-05-22 07:39:39', 0, NULL, '15263698569', NULL, NULL, NULL, '');
INSERT INTO `sh_user` VALUES (39, '44444444444', '123456', '丁', 'http://localhost:8080/image?imageName=file17478312709201004f335eb06cc10df8e9f168185207a8e7.jpg', '2022-02-24 08:43:04', 0, NULL, '15896565656', NULL, NULL, NULL, '');
INSERT INTO `sh_user` VALUES (47, '55555555555', '123456', '戊', 'static/img/默认头像头像.1ea88312.png', '2025-05-23 12:22:12', NULL, NULL, NULL, '1174874286@qq.com', 'male', '1111111111111111', NULL);
INSERT INTO `sh_user` VALUES (48, '66666666666', '123456', '己', 'static/img/默认头像头像.1ea88312.png', '2025-05-23 12:27:36', NULL, NULL, NULL, '1174874288@qq.com', 'female', '2222222222222222', NULL);
INSERT INTO `sh_user` VALUES (49, '17610635317', '123456', '庚', 'static/img/默认头像头像.1ea88312.png', '2025-05-23 12:39:00', NULL, NULL, '17610635317', '1174874280@qq.com', 'male', '0000000000000000', NULL);

SET FOREIGN_KEY_CHECKS = 1;
