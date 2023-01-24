安装项目所需环境：JDK1.8，MySQL5.7，Redis

创建数据库`tdotcn-demo`，执行sql脚本

新增设备`INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`) VALUES ('STWB482012000029', '807060504030219','http://127.0.0.1:6000/callback',6);`

修改common模块log4j文件存储路径

修改common-service模块redis，MySQL连接信息

修改util模块aliyun配置信息



Environment required to install the project: JDK1.8, MySQL5.7, Redis

Create database `tdotcn-demo`，execute sql script

Add Device `INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`) VALUES ('1', '862506049385829',NULL,6);`
Add Device `INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`) VALUES ('2', '862506049388195',NULL,6);`
Add Device `INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`,`soft_version`,`hard_version`,`icc_id`) VALUES ('1', '862506049385829',NULL,6,'EC25EUXGAR08A01M1G#STW','v213','89701013615519740051');`
Add Device `INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`,`hard_version`) VALUES ('1', '862506049385829',NULL,6,'v213');`
Add Device `INSERT INTO `device_info` (`device_no`, `device_uuid`,`url`,`space_nu`) VALUES ('2', '862506049388195',NULL,6);`

INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('1','862506049385829',6);
INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('2','862506049388195',6);
INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('3','862506049395992',6);
INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('4','862506041443907',6);
INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('5','862506049375820',6);
INSERT INTO `device_info` (`device_no`, `device_uuid`,`space_nu`) VALUES ('6','862506049376109',6);

INSERT INTO `powerbank_info` (`power_no`,`donate_level`,`recharge`) VALUES ('B5A200059F',0,0);
INSERT INTO `powerbank_info` (`power_no`,`donate_level`,`recharge`) VALUES ('B4A200058E',0,0);
INSERT INTO `powerbank_info` (`power_no`,`donate_level`,`recharge`) VALUES ('B5A2000594',0,0);
INSERT INTO `powerbank_info` (`power_no`,`donate_level`,`recharge`) VALUES ('B4A20005CA',0,0);
INSERT INTO `powerbank_info` (`power_no`,`donate_level`,`recharge`) VALUES ('B4A20005A3',0,0);

Modify the storage path of the common module log4j file
  
Modify common-service module redis, MySQL connection information

Modify the configuration information of the util module aliyun



## 启动 / start

nohup java -Xmx512m -jar PowerServer-task.jar >task.log 2>&1 &
nohup java -Xmx512m -jar PowerServer-api.jar >api.log 2>&1 & 

## 清空日志 / Clear log
cat /dev/null > nohup.out

## 更新记录 / Update record

### SDK_IOT_2.5

1. 优化充电算法 / Optimize charging algorithm