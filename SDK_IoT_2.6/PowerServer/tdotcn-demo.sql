/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : tdotcn-demo

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 13/05/2021 09:26:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for device_info
-- ----------------------------
DROP TABLE IF EXISTS `device_info`;
CREATE TABLE `device_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '设备总id',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `device_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `device_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `cloud_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备cloudId',
  `icc_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sim卡卡号',
  `device_key` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块sn号',
  `device_state` int(10) NULL DEFAULT 2 COMMENT '设备状态(0禁用1在线2离线)',
  `trace` bigint(20) NULL DEFAULT 0 COMMENT '事件id(1主机升级中 2从机升级中)',
  `space_nu` int(8) NULL DEFAULT NULL COMMENT '设备仓位数(总仓位数)',
  `machine_nu` int(8) NULL DEFAULT NULL COMMENT '设备从机数（总从机数）',
  `device_uuid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备uuID(设备上的) (唯一)',
  `soft_version` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '软件版本',
  `hard_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '硬件版本',
  `agreement_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '协议版本',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扫码的url',
  `device_model` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备型号',
  `device_signal` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信号',
  `network_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网络机制（2G、3G、4G）',
  `network_operator` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网络运营商',
  `device_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备IP',
  `sole_uid` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售代表uid',
  `place_uid` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所管理员uid',
  `agent_uid` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理商uid',
  `place_uuid` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'te',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for findback_log
-- ----------------------------
DROP TABLE IF EXISTS `findback_log`;
CREATE TABLE `findback_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '充电宝归还日志',
  `device_uuid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备uuid',
  `machine_uuid` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '设备上传来的数据',
  `event` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '归还的参数（00表示正常01归还成功）',
  `bid` int(11) NULL DEFAULT NULL COMMENT '0 303数据正常上报，1 303数据缺失，2 303数据校验失败，3 归还 4 归还上报清楚 5 203数据正常上报，6 203数据缺失， 7 租借下发，  8 清除租借下发，  9 104时间校验， 10 103下发租借  11 101升级 12 201 dtu升级数据解析 13 302版本数据解析 14 设备网络信息上报 15设备日志文件名 16 106指令',
  `iot_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'iot消息',
  `receive_time` datetime(0) NULL DEFAULT NULL COMMENT '接收时间',
  `exec_time` datetime(0) NULL DEFAULT NULL COMMENT '执行时间',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102486 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for http_client_invoke_log
-- ----------------------------
DROP TABLE IF EXISTS `http_client_invoke_log`;
CREATE TABLE `http_client_invoke_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `parameter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25955 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_place_info
-- ----------------------------
DROP TABLE IF EXISTS `m_place_info`;
CREATE TABLE `m_place_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `charge_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '套餐id',
  `place_uid` int(11) NULL DEFAULT NULL COMMENT '对应_m_user_info 场所负责人uid (唯一)',
  `place_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所名称',
  `place_remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所说明(详细地址)',
  `place_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所编号',
  `lon` double(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `lat` double(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `picture_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所图片url',
  `open_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业时间',
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '0上线1禁用',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0默认，1餐饮，2ktv,3健身房，4酒店，5商铺，6医院',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_log
-- ----------------------------
DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_uuid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备模块号',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `op_type` tinyint(4) NULL DEFAULT NULL COMMENT '操作类型：1 租借下发，2 租借响应，3 归还响应',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `iot_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'iot消息',
  `exec_time` datetime(0) NULL DEFAULT NULL COMMENT '执行时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_pay
-- ----------------------------
DROP TABLE IF EXISTS `order_pay`;
CREATE TABLE `order_pay`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单表id',
  `order_type` int(11) NOT NULL DEFAULT 0 COMMENT '订单类型（0充值，1押金，2租借,3广告支付）',
  `pay_state` int(11) NOT NULL DEFAULT 0 COMMENT '状态0未支付，1已支付，2申请退款，3退款成功，4退款失败',
  `pay_count` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付账号（用户openId）',
  `pay_money` int(11) NOT NULL COMMENT '付款钱数',
  `real_money` int(11) NULL DEFAULT NULL COMMENT '实际付款数',
  `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户订单号',
  `wx_out_trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信订单号',
  `prepay_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'app账号（用户id）',
  `expires_in` int(11) NULL DEFAULT NULL COMMENT '有效时间，毫秒为单位',
  `trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付,MWEB----网页支付）小程序取值如下：JSAPI',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付完成时间',
  `apply_refund_time` datetime(0) NULL DEFAULT NULL COMMENT '申请退款时间',
  `refund_money` int(11) NULL DEFAULT 0 COMMENT '退款钱数',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  `pay_type` int(11) NOT NULL DEFAULT 0 COMMENT '支付类型，0微信，1支付宝，2-app支付，3-网页支付',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_rent_pay
-- ----------------------------
DROP TABLE IF EXISTS `order_rent_pay`;
CREATE TABLE `order_rent_pay`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '租借订单表id',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `power_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '充电宝编号',
  `money` int(11) NOT NULL DEFAULT 0 COMMENT '订单金额',
  `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单备注',
  `order_type` int(11) NOT NULL DEFAULT 0 COMMENT '订单类型(0微信 1支付宝 2app)',
  `order_state` int(11) NOT NULL DEFAULT 0 COMMENT '订单状态(-1异常订单，0已租借，1已归还,2已支付)',
  `pay_way` int(11) NOT NULL DEFAULT 0 COMMENT '支付方式(0默认余额，1租金，2微信支付)',
  `device_uuid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备uuid',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `close_time` timestamp(0) NULL DEFAULT NULL COMMENT '订单结算时间（停止计费时间）',
  `finish_time` timestamp(0) NULL DEFAULT NULL COMMENT '支付完成时间',
  `order_pay_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联order_pay 的out_trade_no',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for powerbank
-- ----------------------------
DROP TABLE IF EXISTS `powerbank`;
CREATE TABLE `powerbank`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '充电宝id',
  `power_no` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '租借状态(0未租借，1已经租借)',
  `power_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充电宝名称',
  `power_ad` int(20) NULL DEFAULT NULL COMMENT '电压值',
  `position_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '仓位id',
  `machine_uuid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '从机id',
  `device_uuid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备UUID',
  `created_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `back_time` datetime(0) NULL DEFAULT NULL COMMENT '归还时间',
  `error_state` int(11) NOT NULL DEFAULT 0 COMMENT '0正常，1异常',
  `all_position_uuid_row` int(11) NULL DEFAULT NULL COMMENT '总机柜位置的行位置',
  `all_position_uuid_col` int(11) NULL DEFAULT NULL COMMENT '总机柜位置的列位置',
  `all_position_uuild` int(11) NULL DEFAULT NULL COMMENT '总机顺序位置（从上到下，从左到右）',
  `charging_switch` tinyint(1) NULL DEFAULT 0 COMMENT '充电开关',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 517 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for powerbank_log
-- ----------------------------
DROP TABLE IF EXISTS `powerbank_log`;
CREATE TABLE `powerbank_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '充电宝日志id',
  `pos_uuid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓位ID',
  `back_result` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留',
  `power_ad` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电压值',
  `power_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充电宝id',
  `temp` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '温度值',
  `powerbank_state` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充电宝充电状态 01 代表充电宝正在充电，00 未充电',
  `bid` int(11) NULL DEFAULT NULL COMMENT '对应findback_log',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136932 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for powerbank_position_log
-- ----------------------------
DROP TABLE IF EXISTS `powerbank_position_log`;
CREATE TABLE `powerbank_position_log`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '租借充电宝日志id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `position_uuid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '仓位id',
  `power_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充电宝no',
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '状态(0可租借，1待归还，2异常)',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `device_uuid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `back_time` datetime(0) NULL DEFAULT NULL COMMENT '归还时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 638 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_device_log
-- ----------------------------
DROP TABLE IF EXISTS `t_device_log`;
CREATE TABLE `t_device_log`  (
  `f_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `f_device_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备编号',
  `f_type` smallint(6) NULL DEFAULT NULL COMMENT '类型（1:归还，2:上下线状态，3:生命周期变更）',
  `f_log` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `f_time` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  `f_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`f_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1505 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wxa_user
-- ----------------------------
DROP TABLE IF EXISTS `wxa_user`;
CREATE TABLE `wxa_user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '小程序用户id',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `head_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像',
  `sex` int(11) NOT NULL DEFAULT 0 COMMENT '0未知，1男 2女',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `rent` int(11) NOT NULL DEFAULT 0 COMMENT '押金',
  `user_type` int(11) NOT NULL DEFAULT 1 COMMENT '用户类型，0没有交押金，1交了',
  `loan_type` int(11) NOT NULL DEFAULT 0 COMMENT '租借状态 0没租 1租了',
  `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放平台上绑定的唯一id(0的话就代表了支付宝小程序)',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信用户对应公众号唯一id，支付宝用户就是user_id',
  `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户uuid',
  `debug` int(11) NOT NULL DEFAULT 0 COMMENT '0正常，1调试',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国家',
  `money` int(11) NOT NULL DEFAULT 0 COMMENT '余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wxa_user_form_id
-- ----------------------------
DROP TABLE IF EXISTS `wxa_user_form_id`;
CREATE TABLE `wxa_user_form_id`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'formId表id',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户openId',
  `form_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'formId',
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '0有效1无效',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for service_user
-- ----------------------------
DROP TABLE IF EXISTS `service_user`;
CREATE TABLE `service_user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `lc_id` int(11) NOT NULL DEFAULT 0,
  `is_card_linked` tinyint(1) NULL DEFAULT 0,
  `token` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `money` int(11) NOT NULL DEFAULT 0,
  `min_deposit` int(11) NOT NULL DEFAULT 0,
  `messenger_level` int(11) NOT NULL DEFAULT 0,
  `website_level` int(11) NOT NULL DEFAULT 0,
  `application_level` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for powerbank_info
-- ----------------------------
DROP TABLE IF EXISTS `referral_info`;
CREATE TABLE `referral_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `referral_user_id` int(11) UNSIGNED NOT NULL,
  `promoter_user_id` int(11) UNSIGNED NOT NULL,
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qiwi_order_pay
-- ----------------------------
DROP TABLE IF EXISTS `qiwi_order_pay`;
CREATE TABLE `qiwi_order_pay`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `service_user_id` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `bill_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` int(11) NOT NULL DEFAULT 0,
  `state` int(11) NOT NULL DEFAULT 0,
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `close_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for place_info
-- ----------------------------
DROP TABLE IF EXISTS `place_info`;
CREATE TABLE `place_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `place_uuid` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所管理员uid',
  `create_time` timestamp(0) NULL DEFAULT NULL,
  `update_time` timestamp(0) NULL DEFAULT NULL,
  `place_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `place_remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lon` double(10, 6) NULL DEFAULT NULL,
  `lat` double(10, 6) NULL DEFAULT NULL,
  `state` tinyint(1) NULL DEFAULT 0,
  `type` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for powerbank_info
-- ----------------------------
DROP TABLE IF EXISTS `powerbank_info`;
CREATE TABLE `powerbank_info`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '充电宝id',
  `power_no` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `donate_level` int(11) NOT NULL DEFAULT 1,
  `recharge` tinyint(1) NULL DEFAULT 1,
  `clean` tinyint(1) NULL DEFAULT 0,
  `worn_percent` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;