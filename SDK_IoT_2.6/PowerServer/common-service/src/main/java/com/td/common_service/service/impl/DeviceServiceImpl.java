package com.td.common_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.common_service.mapper.*;
import com.td.common_service.mapper.packet.DataPackInfo;
import com.td.common_service.mapper.packet.NetworkPackInfo;
import com.td.common_service.mapper.packet.PowerBankPackInfo;
import com.td.common_service.mapper.packet.VerPackInfo;
import com.td.common_service.model.DeviceInfo;
import com.td.common_service.model.OrderRentPay;
import com.td.common_service.model.Powerbank;
import com.td.common_service.model.PowerbankPositionLog;
import com.td.common_service.service.DeviceService;
import com.td.common_service.service.LogService;
import com.td.common_service.service.OrderPayService;
import com.td.common_service.service.jedis.RedisService;
import com.td.common_service.utils.HttpClientUtils;
import com.td.common_service.vo.PowerbankVo;
import com.td.util.AliyunSign;
import com.td.util.HexToBinaryUtils;
import com.td.util.StringUtils;
import com.td.util.UtilTool;
import com.td.util.config.AliyunConfig;
import com.td.util.device.DeviceUtil;
import com.td.util.vo.PositionVo;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.klock.annotation.Klock;
import org.springframework.boot.autoconfigure.klock.annotation.KlockKey;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static com.td.util.config.DeviceEjectConfig.*;

@SuppressWarnings("ALL")
@Service
public class DeviceServiceImpl implements DeviceService {


    public static final String DEVICE_DATA_REPORT_UPLOAD = "303";
    private static final String GIVE_OUT_POWERBANK = "203";
    private static final String VERSION_UPGRADE = "302";
    private static final String NETWORK_CHECK = "300";
    private static final String SYSTEMTIME_CHECK = "104";
    private static final String DTU_UPDATE_START = "201";
    private static final Integer RENT_EXCEPTION=201;
    private static final Integer DEVICE_NOT_EXIST=211;
    private static final String HAS_EXECUTE = "1";
    private static final String HAS_NOT_EXECUTE = "0";
    private static final String GAIVE_OUT_BYTE_FLAG = "01";
    private static final String NOT_GAIVE_OUT_BYTE_FLAG = "00";
    private static final String IS_NEW_BACK_FLAG = "01";
    private static final String CHARGE_RECENTED_STATE = "01";
    private static final String NOT_CHARGE_RECENTED_STATE = "00";
    private static final String BEGIN_RECENT_COMMAND_CODE="103";
    private static final String SEND_ACK_RECENTED_CODE="5533";
    private static final String SEND_ACK_CLEAN_CODE="5537";
    private static final String UPGRADE = "101";
    private static final String UPLOAD_LOG = "206";
    private static final String OPEN_LOCK = "207";
    private static final String UPLOAD_RADIO = "208";

    // stw start
    private static final Integer CALL_STW_RENT = 1000;          // 设备弹出通知
    private static final String CALL_STW_RENT_SUCCESS = "1001"; // 成功
    private static final String CALL_STW_RENT_ERROR = "1002";   // 失败
    private static final Integer CALL_STW_GIVE_BACK = 2000;     // 设备归还通知
    private static final String CALL_STW_GIVE_BACK_SUCCESS = "2001";    //成功
    private static final String CALL_STW_GIVE_BACK_ERROR = "2001";      // 失败
    private static final String ADVICE_TYPE_NEW = "new_back";   // 全量上报
    private static final String ADVICE_TYPE_BACK = "give_back"; // 归还上报
    // stw end

    /** 满电量前最后一次上报可能存在的前置状态: 9-14 **/
    private static final List POWER_PRE_MAX = new ArrayList(){{add(14);add(13);add(12);add(11);add(10);add(9);}};

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    //@Value("${tdotUp.username}")
    //private String username;//升级账号
    //@Value("${tdotUp.password}")
    //private String password;//升级密码

    @Autowired
    private LogService logService;
    @Autowired
    private PowerbankMapper powerbankMapper;
    @Autowired
    private OrderRentPayMapper rentPayMapper;
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private PowerbankPositionLogMapper positionLogMapper;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private WxaUserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private HttpClientUtils httpClientUtils;

    private ExecutorService  executorService;



    private class PacketHandleThread extends Thread {
        private JSONObject msg;
        private String[] splitData;
        private String requestJson;

        public PacketHandleThread(String[] splitData, String requestJson) {
            this.splitData = splitData;
            this.requestJson = requestJson;
        }

        public PacketHandleThread(JSONObject msg) {
            this.requestJson = new String(JSONArray.parseObject(((JSONArray) msg.remove("payload")).toString(),byte[].class));
            this.splitData = requestJson.split(",");
            this.msg = JSONObject.parseObject(String.valueOf(msg));
        }

        public PacketHandleThread(JSONObject msg,String message) {
            //this.requestJson = new String(JSONArray.parseObject(((JSONArray) msg.remove("payload")).toString(),byte[].class));
            this.splitData = message.split(",");
            this.msg = JSONObject.parseObject(String.valueOf(msg));
        }

        @Override
        public void run() {
            if (splitData[0].equals(DEVICE_DATA_REPORT_UPLOAD)) {
                dataReportUploadHandle(splitData, msg);//数据上报包含充电宝归还
            } else if (splitData[0].equals(GIVE_OUT_POWERBANK)) {
                //TODO 5504 充电响应 203,863418051432938,VQQDAQAArA==   550403010000AC
                giveOutPowerBankHandle(splitData, msg);//203弹出充电宝
            } else if (splitData[0].equals(VERSION_UPGRADE)) {
                versionUpGradehandle(splitData, requestJson, msg);//版本升级
            } else if (splitData[0].equals(NETWORK_CHECK)) {
                networkReportHandle(splitData, requestJson, msg);//网络制式
            } else if (splitData[0].equals(SYSTEMTIME_CHECK)) {
                systemTimeCheckHandle(splitData, requestJson, msg);//系统时间校准
            } else if (splitData[0].equals(DTU_UPDATE_START)) {
                dtuUpdateHandle(splitData, requestJson, msg);//远程升级DTU响应
            } else if (splitData[0].equals(UPLOAD_LOG)) {
                uploadLogHandle(splitData, requestJson, msg);//远程上传日志响应
            } else if (splitData[0].equals(UPLOAD_RADIO)) {
                uploadRadioHandle(splitData, requestJson, msg);//远程上传日志响应
            } else if (splitData[0].equals(OPEN_LOCK)) {
                openLockHandle(splitData, requestJson, msg);//打开电子锁响应
            }
        }
    }



    /**
     * 设备下报接收
     *
     * @param splitData
     * @param requestJson
     * @return
     */
    @Override
    public int backReport(String[] splitData, String requestJson) {
        PacketHandleThread packetHandleThread = new PacketHandleThread(splitData, requestJson);
        //packetHandleThread.start();
        executorService = ThreadPoolServiceImpl.getInstance();
        executorService.execute(packetHandleThread);
        return 0;
    }

    /**
     * 设备下报接收
     *
     * @param splitData
     * @param requestJson
     * @return
     */
    @Override
    public int backReport(JSONObject msg) {
        PacketHandleThread packetHandleThread = new PacketHandleThread(msg);
        //packetHandleThread.start();
        executorService = ThreadPoolServiceImpl.getInstance();
        executorService.execute(packetHandleThread);
        return 0;
    }

    /**
     * 设备下报接收
     *
     * @param splitData
     * @param requestJson
     * @return
     */
    @Override
    public int backReport(JSONObject message,String msg) {
        PacketHandleThread packetHandleThread = new PacketHandleThread(message,msg);
        //packetHandleThread.start();
        executorService = ThreadPoolServiceImpl.getInstance();
        executorService.execute(packetHandleThread);
        return 0;
    }

    private synchronized void putCommandToRedis(final String key, String type) {
        if (type.equals(DEVICE_DATA_REPORT_UPLOAD)) {
            /*if (!redisService.hasKey(key)) {
                redisService.set(key, HAS_NOT_EXECUTE, 1);
            } else {
                long time = redisService.getExpire(key);
                Object obj = redisService.get(key);
                if (time > 3) {//3个周期
                    redisService.delete(key);
                } else {
                    log.info("--------------------------------------key:{}  obj:{}   time:{}", key, obj, time);
                    redisService.set(key, obj, time + 1);
                }
            }*/
            Object obj = redisService.get(key);
            if (obj == null) {
                redisService.set(key, HAS_NOT_EXECUTE, 10);
            }
        }
        if (type.equals(GIVE_OUT_POWERBANK)) {
            if (!redisService.hasKey(key))
                redisService.set(key, HAS_NOT_EXECUTE, 3);
            else {
                long time = redisService.getExpire(key);
                Object obj = redisService.get(key);
                if (time > 3) {   //2个周期
                    redisService.delete(key);
                } else {
                    redisService.set(key, obj, time + 3);
                }
            }
        }

    }


    /**
     * 303上报数据解析
     *
     * @param splitData
     * @param msg
     */
    private void dataReportUploadHandle(String[] splitData, JSONObject msg) {
        msg.put("execTime", new Date().getTime());
        String deviceUuid = splitData[1];
        String command = splitData[2];
        String keyTmp1 = deviceUuid + "&" + command;

        putCommandToRedis(keyTmp1, DEVICE_DATA_REPORT_UPLOAD);//对重复的上报指令的进行过滤处理
        byte[] decode = Base64.getMimeDecoder().decode(command);
        String commandData = HexToBinaryUtils.bytesToHexString(decode);
        log.info("接收到的数据：" + deviceUuid+","+command + "\n" + "解码成功:" + commandData);

        String checkCode = HexToBinaryUtils.getCheckCode(commandData.substring(0, commandData.length() - 2)); //计算校验
        String code = commandData.substring(commandData.length() - 2, commandData.length()); //校验码
        boolean b = checkCode.equalsIgnoreCase(code);
        if (b) {
            DataPackInfo dataPackInfo = new DataPackInfo(commandData);
            Integer allLeng = dataPackInfo.getSubAllPos().length();//仓位信息总长度
            StringBuffer sb= new StringBuffer();
            if (allLeng % 22 == 0 && allLeng > 0) {
                if (redisService.hasKey(keyTmp1)) {
                    Object val = redisService.get(keyTmp1);
                    if (null!=val&& val.toString().equals(HAS_NOT_EXECUTE)) {
                        //log.info("------------------------------------------");
                        //添加上传日志
                        logService.addFindbackLog(deviceUuid, dataPackInfo.getNewBack(), commandData, 0, msg);
                        redisService.set(keyTmp1, HAS_EXECUTE, 10);//注意，这里不用redis清除，应为redis给该keyTmp设置了3秒的生命期，过了2秒Redis会自己自动去清除keyTmp
                        //如果是心跳上报，没有归还标识，需要拿到锁才能执行
                        if(!IS_NEW_BACK_FLAG.equals(dataPackInfo.getNewBack())){
                            //log.info(Thread.currentThread().getName()+"     "+imei+"设备"+dataPackInfo.getMachineUuid()+"从机"+"拿到锁");
                            //循环添加充电宝信息
                            //updatePowerbank(allLeng, dataPackInfo.getNewBack(), dataPackInfo.getSubAllPos(), deviceUuid, dataPackInfo.getMachineUuid(), commandData, msg);
                            String lock = deviceUuid.intern();
                            synchronized (lock){
                                //电量判断（满电关闭）
                                String machineUuid = dataPackInfo.getMachineUuid();
                                // log.info("充电宝信息更新和满电判断，从机："+ machineUuid);
                                updatePowerbank(dataPackInfo.getSubAllPos(),deviceUuid,dataPackInfo.getMachineUuid());
                                //进行充电管理（打开开关）
                                updatePowerbank(deviceUuid,machineUuid);
                            }
                        }else{
                            //log.info("不需要拿锁");
                            //循环添加充电宝信息
                            updatePowerbank(allLeng, dataPackInfo.getNewBack(), dataPackInfo.getSubAllPos(), deviceUuid, dataPackInfo.getMachineUuid(), commandData, msg);
                        }
                    } else {
                        log.info("跳过重复命令");
                    }
                }
            } else if (dataPackInfo.getDataL() == 2) {
                log.info("***********空仓上报数据***********");
                //将在仓充电宝改为不在仓状态
                Powerbank pow = new Powerbank();
                pow.setDeviceUuid(deviceUuid);
                pow.setMachineUuid(dataPackInfo.getMachineUuid());
                pow.setState(0);
                List<Powerbank> pows = powerbankMapper.select(pow);
                for (Powerbank powerbank : pows) {
                    powerbank.setState(1);
                    powerbank.setChargingSwitch(false);
                    powerbankMapper.updateByPrimaryKey(powerbank);
                }
                //查询已打开充电开关且不在仓的充电宝，关闭充电开关
                Powerbank pow1 = new Powerbank();
                pow.setDeviceUuid(deviceUuid);
                pow.setMachineUuid(dataPackInfo.getMachineUuid());
                pow.setState(1);
                pow.setChargingSwitch(true);
                List<Powerbank> pows1 = powerbankMapper.select(pow);

                for (Powerbank powerbank : pows1) {
                    powerbank.setChargingSwitch(false);
                    powerbankMapper.updateByPrimaryKey(powerbank);
                }
                logService.addFindbackLog(deviceUuid, dataPackInfo.getNewBack(), "空仓上报数据：" + commandData, 0, msg);
                advicePowerInfo(deviceUuid, new ArrayList<>(), dataPackInfo.getMachineUuid());
            } else {
                sb.append("数据长度异常：计算为：").append(allLeng).append(";截取值为：").append(dataPackInfo.getDataL());
                log.info(sb.toString());
                sb.delete(0,sb.length());
                sb.append("数据长度异常：计算为：").append(allLeng).append(";截取值为：").append(dataPackInfo.getDataL()).append("数值：").append(commandData);
                logService.addFindbackLog(deviceUuid, dataPackInfo.getNewBack(),sb.toString(), 1, msg);
            }
        } else {

            log.debug("校验验证码不成功，两个参数为：" + "计算值：" + checkCode + " ;截取值" + code);
            logService.addFindbackLog(deviceUuid, NOT_CHARGE_RECENTED_STATE, "数据校验失败:" + commandData, 2, msg);
        }
    }

    /**
     * 全量上报充电宝信息更新
     * @param subAllPos
     * @param deviceUuid
     * @param machineUuid
     */
    private void updatePowerbank(String subAllPos, String deviceUuid, String machineUuid) {
        Map map = new HashMap();
        //数据解析
        int i1 = subAllPos.length() / 22;
        List<String> listA = new ArrayList<>();
        List<String> listB = new ArrayList<>();
        List<PowerbankVo> powerbankVoList = new ArrayList<>();

        for (int i = 1; i <= i1; i++) {

            String powerbankStr = subAllPos.substring(0,22);
            PowerBankPackInfo powerBankPackInfo = new PowerBankPackInfo(powerbankStr);
            PowerbankVo powerbankVo = new PowerbankVo();

            //仓位id
            String positionId = powerbankStr.substring(1,2);
            //归还标识
            //String s2 = powerbank.substring(2,4);
            //充电状态
            String chargerState = powerbankStr.substring(4,6);
            //电量
            String ad = powerbankStr.substring(8,10);
            //充电宝id
            String powerbankId = powerbankStr.substring(10,20);

            powerbankVo.setDeviceUuid(deviceUuid);
            powerbankVo.setPowerNo(powerbankId);
            powerbankVo.setPositionUuid(positionId);
            powerbankVo.setPowerAd(ad);
            powerbankVo.setChargingSwitch(chargerState);

            listA.add(machineUuid+"_"+positionId);
            DeviceInfo deviceInfo = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
            Long time = (Long)redisService.hget("charge_MAX_" + deviceUuid, machineUuid + positionId + "MAX");
            long l = System.currentTimeMillis();
            //更新充电宝信息到数据库，更新对应从机、仓位的充电宝id，电量，充电标识
            Powerbank powerbank = new Powerbank();
            powerbank.setDeviceUuid(deviceUuid);
            powerbank.setMachineUuid(machineUuid);
            powerbank.setPositionUuid(positionId);
            powerbank = powerbankMapper.selectOne(powerbank);
            if (UtilTool.isNull(powerbank)) {
                powerbank = new Powerbank();
                powerbank.setCreatedTime(new Date(System.currentTimeMillis()));
                powerbank.setPowerAd(powerBankPackInfo.getPowerADInt());
                powerbank.setPowerNo(powerBankPackInfo.getPowerNo());
                powerbank.setState(0);
                powerbank.setPositionUuid(powerBankPackInfo.getPosId());
                powerbank.setChargingSwitch(CHARGE_RECENTED_STATE.equals(chargerState));
                powerbank.setMachineUuid(machineUuid);
                powerbank.setDeviceUuid(deviceUuid);
                powerbank.setErrorState(0);
                powerbankMapper.insertSelective(powerbank);
            }
            if(!powerbank.getPowerNo().equals(powerbankId)){
                log.info("充电宝id发生变化");
                powerbank.setPowerNo(powerbankId);
            }
            if(powerbank.getState() == 1){
                powerbank.setState(0);
            }
            powerbank.setPowerAd(powerBankPackInfo.getPowerADInt());
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbank.setChargingSwitch(false);
            powerbank.setState(powerbank.getState() == null || powerbank.getState() != 5 ? 0 : 5);
            powerbank.setErrorState(powerbank.getErrorState() == null || powerbank.getErrorState() != 2 ? 0 : 2);
            powerbank.setAllPositionUuild(Integer.parseInt(machineUuid) * 6 + Integer.parseInt(powerBankPackInfo.getPosId()) - 6);

            //如果充电开关是打开的
            if("01".equals(chargerState)){
                boolean chargingSwitch = true;
                //当电量第一次为0f时，存入redis
                if(time == null && "0f".equalsIgnoreCase(ad)){
                    redisService.hset("charge_MAX_"+deviceUuid,machineUuid + positionId + "MAX",System.currentTimeMillis());
                    log.info("hset："+deviceUuid+","+machineUuid+positionId+"MAX");
                }else if(time != null && "0f".equalsIgnoreCase(ad)){
                    //当电量第一次为0f时，且已存过redis
                    //当前时间-存入redis的时间是否大于30min，是的话，下发关闭充电指令
                    long l1 = (System.currentTimeMillis() - time)/1000/60;
                    log.info("从机："+machineUuid+"，仓位："+positionId+"，时间："+String.valueOf(l1)+"分钟");
                    if(l1>=30){
                        redisService.hdel("charge_MAX_" + deviceUuid, machineUuid + positionId + "MAX");
                        //log.info("hdel："+deviceUuid+","+machineUuid+positionId+"MAX");
                        //下发关闭指令
                        // log.info("\n"+"下发关闭充电指令"+"\n"+
                        //         "设备id："+deviceUuid+"\n"+
                        //         "从机id："+machineUuid+"\n"+
                        //         "仓位id："+positionId);
                        //DeviceUtil.chargeBattery(deviceUuid, machineUuid, positionId, false, deviceInfo.getHardVersion());
                        String command = DeviceUtil.chargeBattery(deviceUuid, machineUuid, positionId, false, deviceInfo.getHardVersion());
                        DeviceUtil.pubCommand(deviceUuid, command);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("execTime", new Date().getTime());
                        logService.addFindbackLog(deviceUuid,"103",command +"==> 关闭充电开关（已满30min），从机："+machineUuid+"，仓位："+positionId,5503,jsonObject);
                        redisService.hset("chargState_"+deviceUuid,machineUuid+"_"+positionId,"00");
                        powerbankVo.setChargingSwitch("00");
                        chargingSwitch = false;
                    }
                }else if(time != null && !"0f".equalsIgnoreCase(ad)){
                    //如果时间不为null，并且电量也不足0f，需要删除该key，重新计时
                    redisService.hdel("charge_MAX_" + deviceUuid, machineUuid + positionId + "MAX");
                    // log.info("hdel："+deviceUuid+","+machineUuid+positionId+"MAX");
                }
                powerbank.setChargingSwitch(chargingSwitch);
                // log.info("更新数据");
                powerbankMapper.updateByPrimaryKey(powerbank);
            }else{
                if(time != null){
                    //如果充电开关不是打开的，并且time不为null，应该删除
                    //如果时间不为null，并且电量也不足0f，需要删除该key，重新计时
                    redisService.hdel("charge_MAX_" + deviceUuid, machineUuid + positionId + "MAX");
                    // log.info("hdel："+deviceUuid+","+machineUuid+positionId+"MAX");
                }
                // log.info("更新数据"+powerbank.toString());
                powerbankMapper.updateByPrimaryKey(powerbank);
            }
            Integer spaceNu = deviceInfo.getSpaceNu();
            if (spaceNu == 9) {
                powerbankVo.setPositionUuid(powerBankPackInfo.getPosId());
            } else {
                Integer allPositionUuild = powerbank.getAllPositionUuild();
                powerbankVo.setPositionUuid(allPositionUuild != null ? allPositionUuild.toString() : getPowerPosition(powerBankPackInfo.getPosId(), machineUuid));
            }

            powerbankVo.setPowerNo(powerBankPackInfo.getPowerNo());
            powerbankVo.setPowerAd(String.valueOf(powerBankPackInfo.getPowerADInt()));
            logService.addPowerBankLog(powerBankPackInfo);

            powerbankVoList.add(powerbankVo);
            //截取未处理的数据
            subAllPos = subAllPos.substring(22,subAllPos.length());
        }
        //发起全量上报回调的方法
        advicePowerInfo(deviceUuid, powerbankVoList, machineUuid);

        Powerbank pow = new Powerbank();
        pow.setDeviceUuid(deviceUuid);
        pow.setMachineUuid(machineUuid);
        pow.setState(0);
        List<Powerbank> pows = powerbankMapper.select(pow);

        for (Powerbank powerbank : pows) {
            String b = powerbank.getMachineUuid() + "_" + powerbank.getPositionUuid();
            for (int i = 0; i < listA.size(); i++) {
                if(listA.get(i).equals(b)){
                    break;
                }else{
                    //继续遍历，继续判断，当最后一个，都不存在的话，说明该充电宝不存在，需要修改状态
                    if(i+1 == listA.size()){
                        powerbank.setState(1);
                        powerbank.setChargingSwitch(false);
                        powerbankMapper.updateByPrimaryKey(powerbank);
                    }
                }
            }
        }

        //查询已打开充电开关且不在仓的充电宝，关闭充电开关
        Powerbank pow1 = new Powerbank();
        pow.setDeviceUuid(deviceUuid);
        pow.setMachineUuid(machineUuid);
        pow.setState(1);
        pow.setChargingSwitch(true);
        List<Powerbank> pows1 = powerbankMapper.select(pow);

        for (Powerbank powerbank : pows1) {
            powerbank.setChargingSwitch(false);
            powerbankMapper.updateByPrimaryKey(powerbank);
        }
    }


    /**
     * 管理某一从机充电开关
     * @param deviceUuid
     * @param machineUuid
     */
    public void updatePowerbank(String deviceUuid,String machineUuid) {//private было
        /**
         * 查询正在充电数量
         * 查询剩余可充电数量
         * if(查询剩余可充电数量>0){
         *     查询不在充电宝的充电宝信息，按电量排序，依次进行判断
         *     判断是否达最大充电数&&同组是否已打开充电开关，否则，打开该仓位充电开关
         * }
         */
        DeviceInfo deviceInfo = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
        String hardVersion = deviceInfo.getHardVersion();
        Integer spaceNu = deviceInfo.getSpaceNu();
        // 获取设备正在充电的充电宝数量
        Powerbank p = new Powerbank();
        p.setDeviceUuid(deviceUuid);
        p.setChargingSwitch(true);
        int count4Charging = powerbankMapper.selectCount(p); // 正在充电数
        log.info("查询数据库设备正在充电数: " + count4Charging);

        //6、9口、12口最大充3个，24、36、48口最大12个
        // 机柜可同时充电的设备最大数
        int sNu = 3;
        //log.info("设备最大充电数初始化: "+sNu);
        if (spaceNu != null && spaceNu > 12) {
            sNu = 12;
        }
        int i = sNu - count4Charging;
        log.info("还可以充电的数量: "+ i);

        if(i > 0){
            //9口优先充同组电量均处于0-10的组，这样充3次就能充完
            if(spaceNu == 9){
                //查询0-10电量的仓位，按仓位排序
                List<Powerbank> powerbankList4 = powerbankMapper.selectPowerBank4Ad4(deviceUuid,machineUuid);
                if(powerbankList4.size()>0) {
                    i = openCharge(deviceUuid, machineUuid, deviceInfo, i, powerbankList4);
                }
            }

            if(i > 0){
                //查询未达可租借电量的充电宝，按电量排序
                List<Powerbank> powerbankList1 = powerbankMapper.selectPowerBank4Ad1(deviceUuid,machineUuid);
                if(powerbankList1.size()>0) {
                    i = openCharge(deviceUuid, deviceInfo, spaceNu, i, powerbankList1);
                }
            }

            if(i > 0){
                //查询达可租借电量的充电宝，按电量排序
                List<Powerbank> powerbankList2 = powerbankMapper.selectPowerBank4Ad2(deviceUuid,machineUuid);
                if(powerbankList2.size()>0) {
                    i = openCharge(deviceUuid, deviceInfo, spaceNu, i, powerbankList2);
                }
            }

            if(i > 0){
                //查询所有从机低于15的充电宝数，等于0时，才能进行深度充电
                int count = powerbankMapper.selectPowerBankCount(deviceUuid,machineUuid);
                if(count ==  0){
                    log.info("可进行深度充电");
                    //查询满电充电宝，进行深度充电
                    List<Powerbank> powerbankList5 = powerbankMapper.selectPowerBank4Ad5(deviceUuid,machineUuid);
                    //log.info("可进行深度充电的充电宝："+powerbankList5.toString());
                    if(powerbankList5.size()>0) {
                        i = openCharge4FullyCharged(deviceUuid, deviceInfo, spaceNu, i, powerbankList5);
                    }
                }
            }

        }else if(i < 0){
            //超出最大充电数，下发关闭
            int j = -i;
            log.info("超过最大充电数，需要关闭"+j+"个");
            // log.info("\n"+"-----------------------下发关闭开始-------------------------------");
            //查询充电中充电宝，按电量低到高排序
            List<Powerbank> powerbankList3 = powerbankMapper.selectPowerBank4Ad3(deviceUuid);
            for (int i1 = 0; i1 < j; i1++) {
                Powerbank powerbank = powerbankList3.get(i1);
                String machineUuid_close = powerbank.getMachineUuid();
                String positionUuid_close = powerbank.getPositionUuid();
                // log.info("\n"+"下发关闭充电指令"+"\n"+
                //         "设备id："+deviceUuid+"\n"+
                //         "从机id："+machineUuid_close+"\n"+
                //         "仓位id："+positionUuid_close);
                String command = DeviceUtil.chargeBattery(deviceUuid, machineUuid_close, positionUuid_close, false, deviceInfo.getHardVersion());
                DeviceUtil.pubCommand(powerbank.getDeviceUuid(), command);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("execTime", new Date().getTime());
                logService.addFindbackLog(deviceUuid,"103",command +"==> 关闭充电开关（超出最大充电数），从机："+machineUuid_close+"，仓位："+positionUuid_close,5503,jsonObject);
                powerbank.setChargingSwitch(false);
                powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
                //修改数据库 充电状态
                powerbankMapper.updateByPrimaryKey(powerbank);
            }
            // log.info("\n"+"-----------------------下发关闭结束-------------------------------");
        }
    }

    /**
     * 9口设备优先打开同组电量均处于0-10的组
     * @param deviceUuid
     * @param machineUuid
     * @param deviceInfo
     * @param i
     * @param powerbankList4
     * @return
     */
    private int openCharge(String deviceUuid, String machineUuid, DeviceInfo deviceInfo, int i, List<Powerbank> powerbankList4) {
        Map<String,String> map = new HashMap<String,String>();
        //获取同组电量均低于10的组
        for (Powerbank powerbank : powerbankList4) {
            String positionUuid = powerbank.getPositionUuid();
            String group = "";
            switch (positionUuid){
                case "1":
                    group = "1";
                    break;
                case "2":
                    group = "1";
                    break;
                case "3":
                    group = "2";
                    break;
                case "4":
                    group = "2";
                    break;
                case "5":
                    group = "3";
                    break;
                case "6":
                    group = "3";
                    break;
                case "7":
                    group = "4";
                    break;
                case "8":
                    group = "4";
                    break;
                case "9":
                    group = "5";
                    break;
            }
            map.put(positionUuid,group);
        }
        Collection groups = map.values();
        Object[] s = groups.toArray();
        List<String> list = new ArrayList();
        for (int j = 0; j < s.length; j++) {
            if(j <(s.length-1)){
                if(s[j].equals(s[j+1])){
                    log.info(s[j]+"组存在两个，可以打开");
                    list.add(s[j].toString());
                }
            }else if(j == s.length-1){
                if(s[j].equals("5")){
                    log.info(s[j]+"组，可以打开");
                    list.add(s[j].toString());
                }else{
                    log.info(s[j]+"组，只有一个未打开，不可打开");
                    //list.add(s[j].toString());
                }
            }
        }
        //打开组中任一仓位
        log.info("下发打开充电指令");
        String positionUuid = "";
        for (String o : list) {
            switch (o){
                case "1":
                    positionUuid = "1";
                    break;
                case "2":
                    positionUuid = "3";
                    break;
                case "3":
                    positionUuid = "5";
                    break;
                case "4":
                    positionUuid = "7";
                    break;
                case "5":
                    positionUuid = "9";
                    break;
            }
            String command = DeviceUtil.chargeBattery(deviceUuid, machineUuid, positionUuid, true, deviceInfo.getHardVersion());
            DeviceUtil.pubCommand(deviceUuid, command);
            Powerbank powerbank = new Powerbank();
            powerbank.setDeviceUuid(deviceUuid);
            powerbank.setMachineUuid(machineUuid);
            powerbank.setPositionUuid(positionUuid);
            powerbank = powerbankMapper.selectOne(powerbank);
            powerbank.setChargingSwitch(true);
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            // redisService.hset("chargState_"+deviceUuid,machineUuid+"_"+positionUuid_,"01");
            // log.info("设置chargState，item为"+machineUuid+"_"+positionUuid_);
            //修改数据库 充电状态
            powerbankMapper.updateByPrimaryKey(powerbank);
            i--;
            if(i == 0){
                log.info("等于0，跳出for循环");
                return 0;
            }
        }
        return i;
    }

    private int openCharge(String deviceUuid, DeviceInfo deviceInfo, Integer spaceNu, int i, List<Powerbank> powerbankList) {
        for (Powerbank powerbank : powerbankList) {
            //判断同组是否已打开充电开关，否则，打开该仓位充电开关
            String machineUuid = powerbank.getMachineUuid();
            String positionUuid_ = powerbank.getPositionUuid();
            String positionUuid = "";
            if (spaceNu == 9) {
                switch (positionUuid_){
                    case "1":
                        positionUuid = "2";
                        break;
                    case "2":
                        positionUuid = "1";
                        break;
                    case "3":
                        positionUuid = "4";
                        break;
                    case "4":
                        positionUuid = "3";
                        break;
                    case "5":
                        positionUuid = "6";
                        break;
                    case "6":
                        positionUuid = "5";
                        break;
                    case "7":
                        positionUuid = "8";
                        break;
                    case "8":
                        positionUuid = "7";
                        break;
                    case "9":
                        positionUuid = "9";
                        break;
                }
            }else{
                switch (positionUuid_){
                    case "1":
                        positionUuid = "2";
                        break;
                    case "2":
                        positionUuid = "1";
                        break;
                    case "3":
                        positionUuid = "4";
                        break;
                    case "4":
                        positionUuid = "3";
                        break;
                    case "5":
                        positionUuid = "6";
                        break;
                    case "6":
                        positionUuid = "5";
                        break;
                }
            }
            Powerbank powerbank1 = new Powerbank();
            powerbank1.setDeviceUuid(deviceUuid);
            powerbank1.setMachineUuid(machineUuid);
            powerbank1.setPositionUuid(positionUuid);
            Powerbank powerbank2 = powerbankMapper.selectOne(powerbank1);
            //同组是否已打开
            if(powerbank2 != null){
                if(powerbank2.getChargingSwitch() == false){
                    //下发打开指令
                    log.info("下发打开充电指令");
                    String command = DeviceUtil.chargeBattery(deviceUuid, machineUuid, positionUuid_, true, deviceInfo.getHardVersion());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("execTime", new Date().getTime());
                    logService.addFindbackLog(deviceUuid,"103",command +"==> 打开充开关，从机："+machineUuid+"，仓位："+positionUuid_,5503,jsonObject);
                    DeviceUtil.pubCommand(powerbank.getDeviceUuid(), command);
                    powerbank.setChargingSwitch(true);
                    powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
                    // redisService.hset("chargState_"+deviceUuid,machineUuid+"_"+positionUuid_,"01");
                    // log.info("设置chargState，item为"+machineUuid+"_"+positionUuid_);
                    //修改数据库 充电状态
                    powerbankMapper.updateByPrimaryKey(powerbank);
                    i--;
                }
            }
            if(i == 0){
                log.info("等于0，跳出for循环");
                return 0;
            }
        }
        return i;
    }

    /**
     * 深度充电
     * @param deviceUuid
     * @param deviceInfo
     * @param spaceNu
     * @param i
     * @param powerbankList
     * @return
     */
    private int openCharge4FullyCharged(String deviceUuid, DeviceInfo deviceInfo, Integer spaceNu, int i, List<Powerbank> powerbankList) {
        //TODO 这里可能会出现最大充电数超出的情况：
        //     当全量上报并发，这里是处理所有从机的数据，打开了充电开关的从机如果在之后进行了数据更新，
        //     会将已经更新为打开的仓位错误修改为关闭，导致可充电数量数据比实际充电数量小，下发打开充电，数量超出。
        // 将在下次更新数据时，下发关闭指令,后续增加缓存。
        // 9.17已修复
        Integer o = (Integer)redisService.get(deviceUuid + "_RechargeableQuantity");
        if(o != null && o == 0){
            return o;
        }
        //遍历充电宝集合，判断redis中是否存在该key（MAP：key=从机id+仓位id，value=充电宝id+充电次数）
        for (Powerbank powerbank : powerbankList) {
            String machineUuid = powerbank.getMachineUuid();
            String positionUuid_ = powerbank.getPositionUuid();
            String powerNo = powerbank.getPowerNo();
            // log.info("遍历数据："+machineUuid+"_"+positionUuid_+"_"+powerNo);
            String fullChargePowerbank = (String) redisService.hget(deviceUuid + "_Full_Charged", machineUuid + "_" + positionUuid_);
            if (StringUtils.isNotEmpty(fullChargePowerbank)) {
                //判断value中充电宝id是否一致
                String[] s = fullChargePowerbank.split("_");
                if (!(StringUtils.isNotEmpty(s[0]) && powerNo.equals(s[0]))) {
                    redisService.hset(deviceUuid + "_Full_Charged", machineUuid + "_" + positionUuid_, powerNo + "_" + 0,24*60*60);
                }
            } else {
                //添加进redis中
                redisService.hset(deviceUuid + "_Full_Charged", machineUuid + "_" + positionUuid_, powerNo + "_" + 0,24*60*60);
            }
        }
        Map<Object, Object> map1 = redisService.hmget(deviceUuid + "_Full_Charged");

        Set<Object> keys = map1.keySet();

        Map<String,String> map2 = new HashMap<>();

        //按充电次数排序redis充电宝集合
        for (Object key : keys) {
            Object value = map1.get(key);
            String count = "0";
            if(value != null && value.toString().length()>0){
                String[] s = value.toString().split("_");
                count = s[1];
            }
            map2.put(key.toString(),count);
        }

        List<Map.Entry<String,String>> list = new ArrayList<>(map2.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        Map<String,String> fullChargePowerbankMap = new LinkedHashMap<>();
        for(Map.Entry<String,String> entry : list){
            fullChargePowerbankMap.put(entry.getKey(),entry.getValue());
        }
        // 最终map：从机id_仓位id : 充电次数
        //log.info("深度充电最终数据："+fullChargePowerbankMap);
        //遍历redis充电宝集合
        for (String machineId_positionId : fullChargePowerbankMap.keySet()) {
            int count = Integer.valueOf(fullChargePowerbankMap.get(machineId_positionId));
            String[] s = machineId_positionId.split("_");
            //判断同组是否已打开充电开关，否则，打开该仓位充电开关
            String machineUuid = s[0];
            String positionUuid_ = s[1];
            Powerbank powerbank = new Powerbank();
            powerbank.setDeviceUuid(deviceUuid);
            powerbank.setMachineUuid(machineUuid);
            powerbank.setPositionUuid(positionUuid_);
            powerbank = powerbankMapper.selectOne(powerbank);
            // 充电宝不在仓，或者已打开充电开关，进行下一轮遍历
            if(powerbank.getState()==1 || powerbank.getChargingSwitch()==true){
                continue;
            }
            String powerNo = powerbank.getPowerNo();
            String positionUuid = "";
            // 查询同组另外一个充电宝
            if (spaceNu == 9) {
                switch (positionUuid_){
                    case "1":
                        positionUuid = "2";
                        break;
                    case "2":
                        positionUuid = "1";
                        break;
                    case "3":
                        positionUuid = "4";
                        break;
                    case "4":
                        positionUuid = "3";
                        break;
                    case "5":
                        positionUuid = "6";
                        break;
                    case "6":
                        positionUuid = "5";
                        break;
                    case "7":
                        positionUuid = "8";
                        break;
                    case "8":
                        positionUuid = "7";
                        break;
                    case "9":
                        positionUuid = "9";
                        break;
                }
            }else{
                switch (positionUuid_){
                    case "1":
                        positionUuid = "2";
                        break;
                    case "2":
                        positionUuid = "1";
                        break;
                    case "3":
                        positionUuid = "4";
                        break;
                    case "4":
                        positionUuid = "3";
                        break;
                    case "5":
                        positionUuid = "6";
                        break;
                    case "6":
                        positionUuid = "5";
                        break;
                }
            }
            Powerbank powerbank1 = new Powerbank();
            powerbank1.setDeviceUuid(deviceUuid);
            powerbank1.setMachineUuid(machineUuid);
            powerbank1.setPositionUuid(positionUuid);
            Powerbank powerbank2 = powerbankMapper.selectOne(powerbank1);
            // 判断同组是否已打开充电开关，否则，打开该仓位充电开关
            if(powerbank2 != null){
                if(powerbank2.getChargingSwitch() == false){
                    //下发打开指令
                    log.info("下发打开充电指令-深度充电");
                    String command = DeviceUtil.chargeBattery(deviceUuid, machineUuid, positionUuid_, true, deviceInfo.getHardVersion());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("execTime", new Date().getTime());
                    logService.addFindbackLog(deviceUuid,"103",command +"==> 打开深度充电，从机："+machineUuid+"，仓位："+positionUuid_,5503,jsonObject);
                    DeviceUtil.pubCommand(powerbank.getDeviceUuid(), command);
                    powerbank.setChargingSwitch(true);
                    powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
                    // redisService.hset("chargState_"+deviceUuid,machineUuid+"_"+positionUuid_,"01");
                    // log.info("设置chargState，item为"+machineUuid+"_"+positionUuid_);
                    //修改数据库 充电状态
                    int i1 = powerbankMapper.updateByPrimaryKey(powerbank);
                    //log.info("深度充电-修改充电状态:"+i1);
                    //修改redis充电次数
                    count++;
                    redisService.hset(deviceUuid + "_Full_Charged", machineUuid + "_" + positionUuid_, powerNo + "_" + count,24*60*60);
                    //log.info("修改深度充电redis数据："+machineUuid + "_" + positionUuid_+","+powerNo + "_" + count);
                    i--;
                }
            }
            if(i == 0){
                //设置可充电数量，防止多个从机全量上报并发，导致第一个从机上报时，已经打开了所有从机充电开关，下次上报时，更新为关闭，又打开了其它仓位，使充电数超过限制值
                redisService.set(deviceUuid + "_RechargeableQuantity",i,20);
                log.info("等于0，跳出for循环");
                return 0;
            }
        }
        return i;
    }

    /**
     * 203数据解析
     *
     * @param splitData
     * @param receiveTime
     */
    private synchronized void giveOutPowerBankHandle(String[] splitData, JSONObject msg) {

        //这里检验一下，如果是包数据不完整（丢包），则直接logService.addFindbackLog()返回给设备端异常;让他重发
        if (splitData.length >= 3) {
            String deviceUuid = splitData[1]; //设备uuid
            String command = splitData[2];  //待解码
            byte[] decode = Base64.getMimeDecoder().decode(command);
            String commandData = HexToBinaryUtils.bytesToHexString(decode);
            //TODO 5504 充电响应 203,863418051432938,VQQDAQAArA==   550403010000AC
            String commandHead = commandData.substring(0, 4);
            if("5504".equals(commandHead)){
                log.info("[msgId: " +msg.get("messageId") + "]接收到串口穿透响应(充电)： " + deviceUuid + "," + commandData);
                return;
            }
            msg.put("execTime", new Date().getTime());
            log.info("处理弹出响应: " + msg);
            log.info("[msgId: " +msg.get("messageId") + "]接收到串口穿透响应(弹出)： " + deviceUuid + "," + commandData);
            // 处理后面具体的数据（两两为一位），再次按照空格进行拆分
            String content = HexToBinaryUtils.change(commandData);
            String[] contentData = content.split(" "); //  55 31 03 01 02 01 9a
            String machine_uuid = contentData[3]; // 从机ID
            String position_uuid = contentData[4]; // 仓位ID
            StringBuffer sb =  new StringBuffer();
            sb.append(deviceUuid).append("#").append(machine_uuid).append("#").append(position_uuid.substring(position_uuid.length() - 1, position_uuid.length()));// deviceUUID+ 从机号+ 仓位号
            String keyTmp1 =sb.toString();// deviceUUID+ 从机号+ 仓位号
            putCommandToRedis(keyTmp1, GIVE_OUT_POWERBANK);
            String codeTemp = commandData.substring(0, commandData.length() - 2);//截取计算值

            String hardVersion = deviceInfoMapper.getHardVersion(deviceUuid);
            if (checkSafeData(commandData,codeTemp)) {
                String power_no = codeTemp.substring(20); // 充电宝ID

                redisService.del("state" + deviceUuid);
                //String deviceEjectRedisName = DeviceUtil.getDeviceEjectRedisName(deviceUuid, machine_uuid, position_uuid);
                if(position_uuid.length()>1){
                    position_uuid = position_uuid.substring(1);
                }
                Object object = redisService.get("DeviceEject_"+deviceUuid+"_"+machine_uuid+"_"+position_uuid);

                if (GAIVE_OUT_BYTE_FLAG.equals(contentData[5]) ) { //弹出成功
                    sb.delete(0,sb.length());
                    sb.append("203_弹出成功!uuid: ").append(deviceUuid).append(" 从机id：").append(machine_uuid).append(" 仓位id： ").append(position_uuid).append("充电宝： ").append(power_no);
                    JSONObject obj = new JSONObject();

                    //弹宝回调，无需订单

                    if (!UtilTool.isNull(object)) {
                        String[] res = object.toString().split(",");
                        successReturn(res);
                    }

                    ////弹宝回调，如果后台没有收到，也不会再次发送
                    //obj.put("action", "弹宝回调");
                    //obj.put("device_uuid", deviceUuid);
                    //obj.put("machine_uuid",machine_uuid);
                    //obj.put("position_uuid",position_uuid);
                    //obj.put("power_no",power_no);
                    //
                    //DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
                    //if (info.getUrl() != null && !"".equals(info.getUrl())) {
                    //    String result = httpClientUtils.doPostJson(info.getUrl(), obj.toJSONString());
                    //    //String msg1 = "203弹出回调通知stw服务,deviceUuid:" + deviceUuid + ",powerbanks:"+ JSONObject.toJSONString(powerbankVoList);
                    //    log.info("~~~~~~~~~~~~~~~~~~" + msg + "~~~~~~~~~~~~~~~~~~~~~~~~");
                    //
                    //    //obj.put("execTime", new Date().getTime());
                    //    //logService.addFindbackLog(deviceUuid, CALL_STW_GIVE_BACK_SUCCESS, msg, CALL_STW_GIVE_BACK, obj);
                    //} else {
                    //    log.info("### 未设置回调地址 ###");
                    //}

                    log.info(sb.toString());
                    //String orderNo = null;
                    //if (redisService.hasKey(keyTmp1)) {
                    //    String val = redisService.get(keyTmp1).toString();
                    //    if (val.equals(HAS_NOT_EXECUTE) && !UtilTool.isNull(object)) {
                    //        redisService.set(keyTmp1, HAS_EXECUTE, 3);//注意，这里不用redis清除，应为redis给该keyTmp设置了3秒的生命期，过了3秒Redis会自己自动去清除keyTmp
                    //        String[] res = object.toString().split(",");
                    //        //创建成功订单，发起回调
                    //        orderNo = createdOrder(res);
                    //        logService.addOrderLog(deviceUuid, orderNo, commandData, 2, msg); // 订单日志
                    //    }
                    //}
                    updatePowerbankUsedState(deviceUuid, machine_uuid, position_uuid.substring(position_uuid.length() - 1, position_uuid.length()), 0);
                  /*  Powerbank powerbank = new Powerbank();
                    powerbank.setPositionUuid(contentData[4].substring(contentData[4].length() - 1, contentData[4].length()));
                    powerbank.setMachineUuid(contentData[3]);
                    powerbank.setDeviceUuid(deviceUuid);
                    powerbank = powerbankMapper.selectOne(powerbank);
                    if (!UtilTool.isNull(powerbank)) {
                        // *******************stw:弹出成功通知stw后台********************
                        advicePowerbankInfo(powerbank.getDeviceUuid(), powerbank.getPowerNo(), 1);
                        // *******************stw:弹出成功通知stw后台********************
                    }*/
                    redisService.del(DeviceUtil.getDeviceEjectRedisName(deviceUuid, machine_uuid, position_uuid));
                    //addDebugLog(sb,deviceUuid,contentData,codeTemp);
                    //logService.addFindbackLog(deviceUuid, NOT_CHARGE_RECENTED_STATE, commandData, 7, msg);
                    boolean res = DeviceUtil.cleanPos(deviceUuid, machine_uuid, position_uuid, SEND_ACK_RECENTED_CODE, hardVersion);
                    String cleanPosMsg = DeviceUtil.getCleanPosMsg(deviceUuid, machine_uuid, position_uuid, SEND_ACK_RECENTED_CODE, hardVersion);
                    String s = DeviceUtil.commandParse(cleanPosMsg);
                    //String machineUuid = cleanPosMsg + " ==> 从机：" + machine_uuid + "，仓位：" + position_uuid + "，充电宝ID：" + power_no + "，「租借」清除仓位：" + s;
                    //logService.addFindbackLog(deviceUuid, NOT_CHARGE_RECENTED_STATE, machineUuid, 8, msg);
                    //logService.addOrderLog(deviceUuid, orderNo, machineUuid, 2, msg); // 订单日志
                    //下发充电宝清楚租借的命令
                    log.info("***************************清除设备的下发租借状态：" + res);
                } else {
                    //String[] ress1 = object.toString().split(",");
                    //errorReturn(ress1);
                    if (redisService.hasKey(keyTmp1)) {
                        String val = redisService.get(keyTmp1).toString();
                        if (val.equals(HAS_NOT_EXECUTE) && !UtilTool.isNull(object)) {
                            redisService.set(keyTmp1, HAS_EXECUTE, 3);
                        }
                    }
                    boolean res = DeviceUtil.cleanPos(deviceUuid, machine_uuid, position_uuid, SEND_ACK_RECENTED_CODE, hardVersion);
                    //添加上传日志
                    logService.addFindbackLog(deviceUuid, contentData[6], sb.append("整条数据：").append(commandData).toString(), 7, msg);
                    // TODO: 2019/5/22 异常处理
                    redisService.del(DeviceUtil.getDeviceEjectRedisName(deviceUuid, machine_uuid, position_uuid));
                    if (!UtilTool.isNull(object)) {
                        String[] ress = object.toString().split(",");
                        //创建失败订单，发起回调
                        errorReturn(ress);
                        //String orderNo = errorDispose(ress);
                        //logService.addOrderLog(deviceUuid, orderNo, "[pup error]" + commandData, 2, msg); // 订单日志
                    } else {
                        Powerbank powerbank = new Powerbank();
                        powerbank.setPositionUuid(position_uuid.substring(position_uuid.length() - 1, position_uuid.length()));
                        powerbank.setMachineUuid(machine_uuid);
                        powerbank.setDeviceUuid(deviceUuid);
                        powerbank = powerbankMapper.selectOne(powerbank);
                        /*if (!UtilTool.isNull(powerbank)) {
                            // *******************stw:弹出失败通知stw后台订单失效********************
                            advicePowerbankInfo(0, powerbank.getDeviceUuid(), powerbank.getPowerNo(), 2);
                            // *******************stw:弹出失败通知stw后台订单失效********************
                        }*/
                        updatePowerbankUsedState(deviceUuid, machine_uuid, position_uuid.substring(position_uuid.length() - 1, position_uuid.length()), 1);
                    }
                }
                redisService.del(power_no);//删除充电宝号码

            } else {
                log.info("设备的203校验错误，异常");
                logService.addFindbackLog(splitData[1], NOT_CHARGE_RECENTED_STATE, commandData, 6, msg);
            }
        } else {
            log.info("异常的203上报日志数据缺失");
            logService.addFindbackLog(splitData[1], NOT_CHARGE_RECENTED_STATE, HexToBinaryUtils.bytesToHexString(Base64.getMimeDecoder().decode(splitData[2])), 5, msg);
        }
    }


    private  void addDebugLog(StringBuffer sb,final String deviceUuid, final String[] contentData,String codeTemp){
        sb.append("203_业务异常!uuid: ").append(deviceUuid).append(" 从机id：").append(contentData[3]).append(" 仓位id： ").append(contentData[4]).append("充电宝： ").append(codeTemp.substring(20));
        log.info(sb.toString());
        sb.delete(0,sb.length());
        sb.append("更新异常的充电宝信息").append(contentData[6]);
        log.info(sb.toString());
        sb.delete(0,sb.length());
        sb.append("203_业务异常!uuid: ").append(deviceUuid).append(" 从机id：").append(contentData[3]).append(" 仓位id： ").append(contentData[4]).append("异常值：").append(contentData[6]);

    }


    private String getCommandData(final String command) {

        byte[] decode = Base64.getMimeDecoder().decode(command);
        String commandData = HexToBinaryUtils.bytesToHexString(decode);
        return commandData;
    }


    /**
     * 验证校验码
     */
    private boolean checkSafeData(final String commandData,final String codeTemp) {

        //得到嵌入式传递过来的校验码
        String code = commandData.substring(commandData.length() - 2, commandData.length());
        String checkCode = HexToBinaryUtils.getCheckCode(codeTemp);//得到校验码
        return checkCode.equalsIgnoreCase(code);
    }

    /**
     * 302数据解析
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void versionUpGradehandle(String[] splitData, String requestJson, JSONObject msg) {
        msg.put("execTime", new Date().getTime());
        VerPackInfo verPackInfo = new VerPackInfo(splitData);
        //开机更新充电宝状态
        //alterPowerState(verPackInfo.getDeviceUuid());
        log.info("设备ID{} ;软件版本{} ;硬件版本{} ;协议版本{}", verPackInfo.toString());
        logService.addFindbackLog(verPackInfo.getDeviceUuid(), VERSION_UPGRADE, requestJson, 13, msg);
        if (!UtilTool.isNull(verPackInfo.getDeviceUuid())) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceUuid(verPackInfo.getDeviceUuid());
            deviceInfo = deviceInfoMapper.selectOne(deviceInfo);
            if (null != deviceInfo) {
                deviceInfo.setSoftVersion(verPackInfo.getSoftVersion());
                deviceInfo.setHardVersion(verPackInfo.getHardVersion());
                deviceInfo.setAgreementVersion(verPackInfo.getAgreementVersion());
                deviceInfo.setTrace(0L);//重置事件标识
                deviceInfo.setDeviceState(1);
                if(!verPackInfo.getDeviceModel().equals(deviceInfo.getDeviceModel()) && !"0".equals(verPackInfo.getDeviceModel())){ //设备型号改变时
                    deviceInfo.setDeviceModel(verPackInfo.getDeviceModel());
                    if (StringUtils.isNotEmpty(verPackInfo.getDeviceModel())) {
                        deviceInfo.setSpaceNu(Integer.valueOf(verPackInfo.getDeviceModel()));
                    }
                    addMachinePowerBankInfo(verPackInfo.getDeviceUuid(),verPackInfo.getDeviceModel());//更变从机、充电宝
                }
                int i = deviceInfoMapper.updateByPrimaryKeySelective(deviceInfo);
                if (i > 0) {
                    log.info("更改设备DTU版本成功_{} ", requestJson);
                }
            }
        } else {
            log.info("上报设备的dtu版本为空：" + verPackInfo.getDeviceUuid());
        }
    }

    /**
     * 300数据解析
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void networkReportHandle(String[] splitData, String requestJson, JSONObject msg) {
        msg.put("execTime", new Date().getTime());
        NetworkPackInfo networkPackInfo = new NetworkPackInfo(splitData);
        log.info("设备ID{} ;信号{} ;网络{} ;运营商{}", networkPackInfo.toString());
        logService.addFindbackLog(networkPackInfo.getDeviceUuid(), NETWORK_CHECK, requestJson, 14, msg);
        if (!UtilTool.isNull(networkPackInfo.getDeviceUuid())) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceUuid(networkPackInfo.getDeviceUuid());
            deviceInfo = deviceInfoMapper.selectOne(deviceInfo);
            if (null != deviceInfo) {
                deviceInfo.setDeviceSignal(networkPackInfo.getSignalVersion());
                deviceInfo.setNetworkType(networkPackInfo.getNetworkVersion());
                deviceInfo.setNetworkOperator(networkPackInfo.getOperatorVersion());
                deviceInfo.setIccId(networkPackInfo.getIccid());
                deviceInfo.setDeviceState(1);
                int i = deviceInfoMapper.updateByPrimaryKeySelective(deviceInfo);
                if (i > 0) {
                    log.info("300上报设备的信息为空{} ", requestJson);
                }
            }
        } else {
            log.info("300上报设备的信息为空：" + networkPackInfo.getDeviceUuid());
        }
    }

    /**
     * 104数据解析
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void systemTimeCheckHandle(String[] splitData, String requestJson, JSONObject msg) {
        msg.put("execTime", new Date().getTime());
        String deviceUuid = splitData[1]; //设备uuid
        String token = splitData[2];  //token
        logService.addFindbackLog(deviceUuid, SYSTEMTIME_CHECK, requestJson, 9, msg);
        // String message = 204 + "," + deviceUuid + "," + token + "," + new Date().getTime();
        String message = 204 + "," + deviceUuid + "," + "check" + "," + new Date().getTime();
        log.info("104系统时间校验：" + requestJson);
        message = message.substring(0, message.length() - 3);
        log.info("参数格式：" + message);
        boolean res = DeviceUtil.checkTime(deviceUuid, message);
        logService.addFindbackLog(deviceUuid, "204", message, 9, msg);
        log.info("返回下发校验结果：" + res);
    }

    /**
     * 201 dtu升级数据解析
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void dtuUpdateHandle(String[] splitData, String requestJson, JSONObject msg) {
        String deviceUuid = splitData[1]; //设备uuid
        msg.put("execTime", new Date().getTime());
        logService.addFindbackLog(deviceUuid, DTU_UPDATE_START, requestJson, 12, msg);
    }

    /**
     * 206远程上传日志响应
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void uploadLogHandle(String[] splitData, String requestJson, JSONObject msg) {
        String deviceUuid = splitData[1]; //设备uuid
        msg.put("execTime", new Date().getTime());
        logService.addFindbackLog(deviceUuid, UPLOAD_LOG, requestJson, 15, msg);
    }

    /**
     * 208远程上传日志响应
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void uploadRadioHandle(String[] splitData, String requestJson, JSONObject msg) {
        String deviceUuid = splitData[1]; //设备uuid
        msg.put("execTime", new Date().getTime());
        logService.addFindbackLog(deviceUuid, UPLOAD_RADIO, requestJson, 208, msg);
    }

    /**
     * 207打开电子锁响应
     * @param splitData
     * @param requestJson
     * @param receiveTime
     */
    private synchronized void openLockHandle(String[] splitData, String requestJson, JSONObject msg) {
        String deviceUuid = splitData[1]; //设备uuid
        msg.put("execTime", new Date().getTime());
        logService.addFindbackLog(deviceUuid, OPEN_LOCK, requestJson, 207, msg);
    }


    /**
     * 更新充电宝电压
     *
     * @param deviceUuid
     * @param machineUuid
     * @param positionUuid
     */
    @Transactional
    public void updatePowerbankUsedState(String deviceUuid, String machineUuid, String positionUuid, int state) {
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(deviceUuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        if (!UtilTool.isNull(powerbank)) {
            //powerbank.setErrorState(state);
            if(state == 0){
                //弹宝成功时，修改租借状态和充电状态
                powerbank.setState(1);
                powerbank.setChargingSwitch(false);
            }
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbankMapper.updateByPrimaryKey(powerbank);
        }
    }

    /**
     * 通过uuid获取设备
     *
     * @param uuid
     * @return
     */
    public DeviceInfo getDeviceInfoByUuid(String uuid) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceUuid(uuid);
        deviceInfo = deviceInfoMapper.selectOne(deviceInfo);
        if (deviceInfo != null) {
            Powerbank powerbank = new Powerbank();
            powerbank.setDeviceUuid(deviceInfo.getDeviceUuid());
            powerbank.setState(0);
            //List<Powerbank> powerbankList = powerbankMapper.select(powerbank);
            List<Powerbank> powerbankList = powerbankMapper.selectPowerbankByDeviceUuid(uuid);
            /*for (Powerbank powerbank_ : powerbankList) {
                if (deviceInfo.getSpaceNu() != 9) {
                    Integer allPositionUuild = powerbank_.getAllPositionUuild();
                    powerbank_.setPositionUuid(allPositionUuild!=null?allPositionUuild.toString():getPowerPosition(powerbank_.getPositionUuid(), powerbank_.getMachineUuid()));
                }
            }*/
            deviceInfo.setPowerbankList(powerbankList);
        }
        return deviceInfo;
    }



    @Override
    @Transactional
    public int updateState(String deviceNo, Integer deviceState, String ip) {
        DeviceInfo info = new DeviceInfo();
        info.setDeviceUuid(deviceNo);
        info = deviceInfoMapper.selectOne(info);
        // **********************上下线通知stw后台*************************
        advicePowerbankOnOffLine(deviceNo, deviceState == 1 ? 1 : 0);
        log.info(info + " regerge");
        if (!UtilTool.isNull(info) && info.getDeviceState() != 0) {
            info.setDeviceState(deviceState);
            info.setDeviceIP(ip);
            deviceInfoMapper.updateByPrimaryKeySelective(info);
        }
        return 0;
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////                                                                                  订单日志                                                                             ////
    /*
     *//**
     * 添加上报日志
     *
     * @param deviceUuid
     * @param newBack
     * @param machineUuid
     * @param bid
     * @return
     *//*
    @Transactional
    public int logService.addFindbackLog(String deviceUuid, String event, String machineUuid, int bid) {
        FindbackLog findbackLog = new FindbackLog();
        findbackLog.setBid(bid);
        findbackLog.setCreatedTime(new Date(System.currentTimeMillis()));
        findbackLog.setDeviceUuid(deviceUuid);
        findbackLog.setEvent(event);
        findbackLog.setMachineUuid(machineUuid);
        findbackLogMapper.insertSelective(findbackLog);
        return 0;
    }

    *//**
     * 添加订单日志
     *//*
    @Transactional
    public int logService.addOrderLog(String deviceUuid, String orderNo, String machineUuid, int opType) {
        if (StringUtils.isEmpty(orderNo)) {
            return 1;
        }
        OrderLog orderLog = new OrderLog();
        orderLog.setOpType(opType);
        orderLog.setOrderNo(orderNo);
        orderLog.setDeviceUuid(deviceUuid);
        orderLog.setContent(machineUuid);
        orderLogMapper.insertSelective(orderLog);
        return 0;
    }*/

////                                                                                  订单日志                                                                             ////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 更新充电宝的信息
     *
     * @param bid
     * @param allLeng
     * @param newBack
     * @param subAllPos
     * @param deviceUuid
     * @param machineUuid
     * @param commandData
     * @param receiveTime
     * @param currentTime
     * @return
     */
    public  int updatePowerbank(int allLeng, String newBack, String subAllPos, String deviceUuid, String machineUuid, String commandData, JSONObject msg) {
        int posNum = allLeng / 22;
//        int count4Charging = 0;
        List<PowerbankVo> powerbankVoList = new ArrayList<>();
        List<Powerbank> chargeBatteries = new ArrayList(); // 打开充电开关的充电宝
        DeviceInfo deviceInfo = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
        String hardVersion = deviceInfo.getHardVersion();
        Integer spaceNu = deviceInfo.getSpaceNu();

        // 获取设备正在充电的充电宝数量
        Powerbank p = new Powerbank();
        p.setDeviceUuid(deviceUuid);
        //p.setState(0);
        p.setChargingSwitch(true);
        int count4Charging = powerbankMapper.selectCount(p); // 正在充电数
        log.info("查询数据库设备正在充电数: " + count4Charging);

        //6、9口最大充3个，12口最大6口，24、36、48口最大12个

        p.setMachineUuid(machineUuid);
        int i2 = powerbankMapper.selectCount(p);
        log.info("查询数据库，当前从机，即 " + machineUuid +"从机正在充电数: " + i2);
        count4Charging -= powerbankMapper.selectCount(p);
        log.info("其它从机正在充电数: " + count4Charging);
        // 机柜可同时充电的设备最大数
        int sNu = 3;
        //log.info("设备最大充电数初始化: "+sNu);
        if (spaceNu != null && spaceNu > 9) {
            if (spaceNu < 24) {
                sNu = spaceNu / 2;
            } else {
                sNu = 12;
            }
        }
        log.info("设备最大充电数初始化: "+sNu);
        log.info("\n"+"----------------------------------------遍历当前从机仓位开始----------------------------------------");

        for (int i = 0; i < posNum; i++) { //每循环一次，代表遍历一个仓位。
            String posInfo = subAllPos.substring(i * 22, (i + 1) * 22); //一个仓位
            String key= "cabin*"+deviceUuid + "@"+machineUuid + "@" +posInfo;
            if(redisService.hasKey(key)/*&&NOT_CHARGE_RECENTED_STATE.equals(posInfo.substring(2, 4))*/){
                continue;
            };
            redisService.set(key, HAS_EXECUTE, 3);
            //只要收到上报信息，这个地方必须做一下校验，如果遇到是异常包（比如说有丢包现象导致字节长度不对），就会logService.addFindbackLog返回异常错误码，让设备重发。
            PowerBankPackInfo powerBankPackInfo = new PowerBankPackInfo(posInfo);
            Powerbank powerbank_ = new Powerbank();
            powerbank_.setDeviceUuid(deviceUuid);
            powerbank_.setMachineUuid(machineUuid);
            powerbank_.setPositionUuid(powerBankPackInfo.getPosId());
            Powerbank powerbank = powerbankMapper.selectOne(powerbank_);
            String posState = powerBankPackInfo.getPosState(); // 充电标识
            //先不用判断他是归还上报还是状态上报，首先查一下仓位信息表，如果没有先补仓位信息。
            Integer powerADInt = powerBankPackInfo.getPowerADInt();

            if (UtilTool.isNull(powerbank)) {
                powerbank = new Powerbank();
                powerbank.setCreatedTime(new Date(System.currentTimeMillis()));
                powerbank.setPowerAd(powerADInt);
                powerbank.setPowerNo(powerBankPackInfo.getPowerNo());
                powerbank.setState(0);
                powerbank.setPositionUuid(powerBankPackInfo.getPosId());
                powerbank.setChargingSwitch(CHARGE_RECENTED_STATE.equals(posState));
                powerbank.setMachineUuid(machineUuid);
                powerbank.setDeviceUuid(deviceUuid);
                powerbank.setErrorState(0);
                if("01".equals(powerBankPackInfo.getBackRes())){
                    returnCallback(powerbank);
                }
                /**
                 * 表示上报归还充电宝，即需要创建待付款订房
                 */
                boolean flag = false;
                if (IS_NEW_BACK_FLAG.equals(powerBankPackInfo.getBackRes()) && !redisService.hasKey(powerBankPackInfo.getPowerNo())) {
                    log.info("====有空数据上报归还事件命令=========================================================");
//                    OrderRentPay orderRentPay = orderPayService.updateOrderRentPay(powerbank);
//                    int state = orderRentPay.getOrderState();
                    boolean res = DeviceUtil.cleanPos(deviceUuid, machineUuid, powerBankPackInfo.getPosId(), SEND_ACK_CLEAN_CODE, hardVersion);
                    String cleanPosMsg = DeviceUtil.getCleanPosMsg(deviceUuid, machineUuid, powerBankPackInfo.getPosId(), SEND_ACK_CLEAN_CODE, hardVersion);
                    String s = DeviceUtil.commandParse(cleanPosMsg);
                    //logService.addFindbackLog(deviceUuid, powerBankPackInfo.getBackRes(), cleanPosMsg, 4);//上报的命令
                    String giveBackCommand = cleanPosMsg + " ==> 仓位：" + powerbank.getAllPositionUuild() + "，充电宝ID：" + powerbank.getPowerNo() + "，「归还」清除仓位：" + s;
                    logService.addFindbackLog(deviceUuid, powerBankPackInfo.getBackRes(), giveBackCommand, 4, msg);//上报的命令
                    //logService.addOrderLog(deviceUuid, orderRentPay.getOrderNo(), giveBackCommand, 3, msg);
                    //log.info("更改状态: " + state + ", 下发清除状态：是否成功: " + res);
                    powerbank.setState(0);
                    powerbank.setBackTime(new Date(System.currentTimeMillis()));
                    powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
                    //if (!UtilTool.isNull(orderRentPay.getId())) {
                    //    logService.addOrderLog(deviceUuid, orderRentPay.getOrderNo(), commandData, 3, msg); // 订单日志
                    //    adviceNewBack(powerbank, ADVICE_TYPE_BACK, orderRentPay.getOrderNo());
                    //}
                    flag = true;
                }
                powerbankMapper.insertSelective(powerbank);
                if (!flag) {
//                    adviceNewBack(powerbank, ADVICE_TYPE_NEW);
                }
            } else {
                if (IS_NEW_BACK_FLAG.equals(newBack)) {
                    updatePowerbankNewBack(deviceUuid, machineUuid, powerbank, powerBankPackInfo, commandData, msg);
                    if("01".equals(powerBankPackInfo.getBackRes())){
                        returnCallback(powerbank);
                    }
                } else {
                    updatePowerbankUpdate(powerbank, powerBankPackInfo);
//                    adviceNewBack(powerbank, ADVICE_TYPE_NEW);
                }
            }
            if (!IS_NEW_BACK_FLAG.equals(newBack)) {
                PowerbankVo powerbankVo = new PowerbankVo();
                powerbankVo.setDeviceUuid(deviceUuid);
                if (spaceNu == 9) {
                    powerbankVo.setPositionUuid(powerBankPackInfo.getPosId());
                } else {
                    Integer allPositionUuild = powerbank.getAllPositionUuild();
                    powerbankVo.setPositionUuid(allPositionUuild != null ? allPositionUuild.toString() : getPowerPosition(powerBankPackInfo.getPosId(), machineUuid));
                }
                powerbankVo.setPowerNo(powerBankPackInfo.getPowerNo());
                powerbankVo.setPowerAd(String.valueOf(powerADInt));
                powerbankVo.setChargingSwitch(posState);

                ///////////  智能充电  ////////////  START
//                try {
//                    if (CHARGE_RECENTED_STATE.equals(posState)) {
//                        if(count4Charging >= sNu){
//                            log.info("遍历当前从机，正在充电数为："+(count4Charging+1));
//                            log.info("正在充电数已达最大充电数，当前仓位需要下发关闭充电指令");
//                            DeviceUtil.chargeBattery(powerbank.getDeviceUuid(), machineUuid, powerbank.getPositionUuid(), false, hardVersion);
//                        }else if(count4Charging < sNu){
//                            count4Charging++;
//                            log.info("遍历当前从机，正在充电数为："+count4Charging);
//                        }
//
//                        powerbank.setChargingSwitch(true);
//                    } else {
//                        powerbank.setChargingSwitch(false);
//                    }
//                    powerbank.setPowerAd(powerADInt);
//                    String closeCharge = "CLOSE_CHARGE_" + powerbank.getDeviceUuid() + "_" + powerbank.getPowerNo();
//                    boolean isCloseCharge = NOT_CHARGE_RECENTED_STATE.equals(posState) && redisService.get(closeCharge) != null;
//                    if (!isCloseCharge) {
////                    count4Charge = smartCharging(powerbank, deviceInfo, machineUuid, posState, count4Charge);
//                        // 同组充电开关是否打开
//                        boolean canCharge = true;
//                        int i1 = 0;
//                        if (Integer.parseInt(powerBankPackInfo.getPosId()) % 2 != 0) {
//                            i1 = i + 1;
//                            try {
//                                String substring = subAllPos.substring(i1 * 22, (i1 + 1) * 22);
//                                PowerBankPackInfo powerDataPackge = new PowerBankPackInfo(substring); // 同组另一宝数据
//                                boolean b1 = Integer.parseInt(powerDataPackge.getPosId()) == Integer.parseInt(powerBankPackInfo.getPosId()) + 1; // 是否同组
////                            canCharge = !b1 || NOT_CHARGE_RECENTED_STATE.equals(powerDataPackge.getPosState()); // 另一宝是否未充电
//                                canCharge = !b1 || powerDataPackge.getPowerADInt() <= powerbank.getPowerAd(); // 另一宝电量小于当前宝
//                                canCharge = canCharge || NOT_CHARGE_RECENTED_STATE.equals(powerDataPackge.getPosState()); // 另一宝未充电
//                            } catch (Exception e) {
//                            }
//                        } else if (powerbankVoList.size() > 0) {
//                            PowerbankVo powerData = powerbankVoList.get(powerbankVoList.size() - 1);
//                            int parseInt = Integer.parseInt(powerData.getPositionUuid()); // 分机仓位
//                            if (Integer.parseInt(machineUuid) != 01) {
//                                parseInt %= 6;
//                            }
//                            boolean b1 = parseInt == Integer.parseInt(powerBankPackInfo.getPosId()) - 1; // 同组充电宝
//                            // 非同组 || (同组 && 另一宝未充电 && 电量<当前宝)则继续
////                        canCharge = !b1 || (NOT_CHARGE_RECENTED_STATE.equals(powerData.getChargingSwitch()) && Integer.parseInt(powerData.getPowerAd()) < powerbank.getPowerAd());
//                            // 非同组 || (同组 && 另一宝电量<当前宝)
//                            canCharge = !b1 || Integer.parseInt(powerData.getPowerAd()) < powerbank.getPowerAd();
//                            canCharge = canCharge || NOT_CHARGE_RECENTED_STATE.equals(powerData.getChargingSwitch()); // 另一宝未充电
//                        }
//
//                        String command = "";
//                        if (canCharge) {
//
//                            // [充电中，满电量]
//                            boolean powerMax = CHARGE_RECENTED_STATE.equals(posState) && powerBankPackInfo.getPowerADInt() == 15;
//                            // [充电中，电量将满]
//                            boolean powerPreMax = CHARGE_RECENTED_STATE.equals(posState) && POWER_PRE_MAX.contains(powerBankPackInfo.getPowerADInt());
//                            // [未充电, 电量未满]
//                            boolean powerNotMax = NOT_CHARGE_RECENTED_STATE.equals(posState) && powerBankPackInfo.getPowerADInt() != 15;
//                            String maxCharge = "MAX_CHARGE_" + powerbank.getDeviceUuid() + "_" + powerbank.getPowerNo();
//                            if (powerMax) { // 关闭充电开关
//                                if (redisService.get(maxCharge) == null) {
//                                    redisService.set(closeCharge, 1, MIN_CHARGE_TIME);
//                                    log.info("电量已满，关闭充电开关");
//                                    command = DeviceUtil.chargeBattery(powerbank.getDeviceUuid(), machineUuid, powerbank.getPositionUuid(), false, hardVersion);
////                                count4Charging--; // 下次上报时再打开新开关，确认被关闭
//                                }
//                            } else if (powerPreMax) {// 电量快满的进入慢速充电
//                                redisService.set(maxCharge, 1, MAX_CHARGE_TIME);
//                            } else if (powerNotMax) {// 准备打开充电开关
////                                if (redisService.get(closeCharge) == null) {
//                                powerbankVo.setChargingSwitch(CHARGE_RECENTED_STATE);
//                                chargeBatteries.add(sortIndex(chargeBatteries, powerbank), powerbank);
////                                }
//                            }
//                        } else if (CHARGE_RECENTED_STATE.equals(powerBankPackInfo.getPosState())) { // [另一宝充电|(另一宝未充电 & 电量>当前宝)] 关闭充电开关
//                            command = DeviceUtil.chargeBattery(powerbank.getDeviceUuid(), machineUuid, powerbank.getPositionUuid(), false, hardVersion);
//                        }
//                        boolean b = DeviceUtil.pubCommand(powerbank.getDeviceUuid(), command);
//                        if (b) {
//                            log.info("关闭充电开关成功");
//                            String machineUuid1 =
//                                    command + " ==> 从机：" + powerbank.getMachineUuid() + "，仓位：" + powerbank.getPositionUuid() + "，充电宝ID：" + powerbank.getPowerNo() + "，充电开关「关闭」：" + DeviceUtil.commandParse(command);
//                            log.info(machineUuid1);
//                            JSONObject jObj = new JSONObject();
//                            jObj.put("execTime", System.currentTimeMillis());
//                            logService.addFindbackLog(powerbank.getDeviceUuid(), "03", machineUuid1, 03, jObj);
//                        }
//                    }
//                    powerbankMapper.updateByPrimaryKeySelective(powerbank);
//                } catch (Exception e) {
//                    log.error("设备【" + powerbank.getDeviceUuid() + "】仓位【" + powerbank.getAllPositionUuild() + "】充电宝【" + powerbank.getPowerNo() + "】智能充电异常");
//                }
                ///////////  智能充电 ////////////  END
                powerbankVoList.add(powerbankVo);
            }
            /////////////////////日志
            logService.addPowerBankLog(powerBankPackInfo);
        }
        log.info("\n"+"----------------------------------------遍历当前从机仓位结束----------------------------------------");

        //log.info("正在充电数: "+count4Charging);
        //int count4Charge = sNu - count4Charging;
        //count4Charge = count4Charge > 0 ? count4Charge : 0; // 还可以充电的数量
        //log.info("还可以充电的数量: "+count4Charge);
        // 打开充电开关
        //log.info("\n"+"----------------------------------------打开充电开关开始----------------------------------------");
        //for (int i = 0; i < chargeBatteries.size() && i < count4Charge; i++) {
        //    Powerbank powerbank = chargeBatteries.get(i);
        //
        //    String command = DeviceUtil.chargeBattery(powerbank.getDeviceUuid(), machineUuid, powerbank.getPositionUuid(), true, hardVersion);
        //    boolean b = DeviceUtil.pubCommand(powerbank.getDeviceUuid(), command);
        //    if (b) {
        //        powerbank.setChargingSwitch(true);
        //        String machineUuid1 =
        //                command + " ==> 从机：" + powerbank.getMachineUuid() + "，仓位：" + powerbank.getPositionUuid() + "，充电宝ID：" + powerbank.getPowerNo() + "，充电开关「打开」：" + DeviceUtil.commandParse(command);
        //        //log.info(machineUuid1);
        //        log.info(powerbank.getMachineUuid() + "从机，" + powerbank.getPositionUuid()+"仓位，下发打开充电开指令成功");
        //        JSONObject jObj = new JSONObject();
        //        jObj.put("execTime", System.currentTimeMillis());
        //        logService.addFindbackLog(powerbank.getDeviceUuid(), "03", machineUuid1, 03, jObj);
        //        powerbankMapper.updateByPrimaryKeySelective(powerbank);
        //    }
        //}
        //log.info("\n"+"----------------------------------------打开充电开关结束----------------------------------------");

        if (powerbankVoList.size() > 0) {
            advicePowerInfo(deviceUuid, powerbankVoList, machineUuid);
        }
        return 0;
    }


    /** 获取 list中电量从大到小排序 的索引 **/
    private int sortIndex(List<Powerbank> list, Powerbank entry) {
        assert list != null;
        if (list.size() > 0) {
            Powerbank maxPower = list.get(0);
            if (entry.getPowerAd() > maxPower.getPowerAd()) {
                return 0;
            }
            Powerbank minPower = list.get(list.size() - 1);
            if (entry.getPowerAd() <= minPower.getPowerAd()) {
                return list.size();
            }
            if (list.size() == 2) {
                return 1;
            }
            return sortIndex(list.subList(1, list.size() - 1), entry) + 1;
        }
        return 0;
    }

    /**
     * 更新充电宝的信息
     *
     * @param allLeng
     * @param newBack
     * @param subAllPos
     * @param deviceUuid
     * @param machineUuid
     * @param bid
     * @return
     */
    @Transactional
    public int updatePowerbank2(int allLeng, String newBack, String subAllPos, String deviceUuid, String machineUuid, JSONObject msg) {
        int posNum = allLeng / 22;
        for (int i = 0; i < posNum; i++) { //每循环一次，代表遍历一个仓位。
            String posInfo = subAllPos.substring(i * 22, (i + 1) * 22); //一个仓位

            PowerBankPackInfo powerBankPackInfo = new PowerBankPackInfo(posInfo);
            Powerbank powerbank = checkExistInDB(deviceUuid, machineUuid, powerBankPackInfo.getPosId());
            String key= "cabin*"+deviceUuid + "@"+machineUuid + "@" +posInfo;
            if(redisService.hasKey(key)&&"00".equals(posInfo.substring(2, 4))){
                continue;
            };
            redisService.set(key, HAS_EXECUTE, 3);
            //先不用判断他是归还上报还是状态上报，首先查一下仓位信息表，如果没有先补仓位信息。
            if (UtilTool.isNull(powerbank)) {
                powerbank = new Powerbank();
                powerbank.parsePowerBankPacket(powerBankPackInfo, deviceUuid, machineUuid);
                /**
                 * 表示上报归还充电宝，即需要创建待付款订房
                 */
                if (CHARGE_RECENTED_STATE.equals(powerBankPackInfo.getBackRes()) && !redisService.hasKey(powerBankPackInfo.getPowerNo())) {


                    log.info("====有空数据上报归还事件命令=========================================================");
                    OrderRentPay orderRentPay = orderPayService.updateOrderRentPay(powerbank);
                    /*if (UtilTool.isNull(orderRentPay.getId())) {
                        continue;
                    }*/
                    int state = orderRentPay.getOrderState();
                    String hardVersion = deviceInfoMapper.getHardVersion(deviceUuid);
                    boolean res = DeviceUtil.cleanPos(deviceUuid, machineUuid, powerBankPackInfo.getPosId(), SEND_ACK_CLEAN_CODE, hardVersion);
                    String cleanPosMsg = DeviceUtil.getCleanPosMsg(deviceUuid, machineUuid, powerBankPackInfo.getPosId(), SEND_ACK_CLEAN_CODE, hardVersion);
//                    logService.addFindbackLog(deviceUuid, powerBankPackInfo.getBackRes(), cleanPosMsg, 4);//上报的命令
                    String s = DeviceUtil.commandParse(cleanPosMsg);
                    logService.addFindbackLog(deviceUuid,powerBankPackInfo.getBackRes(), cleanPosMsg+" ==> 仓位："+powerbank.getAllPositionUuild() +"，充电宝ID："+ powerbank.getPowerNo() + "，「归还」清除仓位：" + s, 4, msg);//上报的命令
                    // log.info("更改状态: " + state + ", 下发清除状态：是否成功: " + res);
                    powerbank.setState(0);
                    powerbank.setBackTime(new Date(System.currentTimeMillis()));
                    powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
                }
                powerbankMapper.insertSelective(powerbank);
            } else {
                if (IS_NEW_BACK_FLAG.equals(newBack)) {
//                    updatePowerbankNewBack(deviceUuid, machineUuid, powerbank, powerBankPackInfo);
                } else {
                    updatePowerbankUpdate(powerbank, powerBankPackInfo);
                }
            }
            /////////////////////日志
            logService.addPowerBankLog(powerBankPackInfo);

        }
        return 0;
    }

    private Powerbank checkExistInDB(String deviceUuid, String machineUuid, String pos) {
        Powerbank powerbank_ = new Powerbank();
        powerbank_.setDeviceUuid(deviceUuid);
        powerbank_.setMachineUuid(machineUuid);
        powerbank_.setPositionUuid(pos);
        Powerbank powerbank = powerbankMapper.selectOne(powerbank_);
        return powerbank;
    }/*
    @Transactional
    public void addPowbackLog(PowerBankPackInfo powerBankPackInfo) {
        PowerbankLog powerbankLog = new PowerbankLog();
        powerbankLog.parsePowerbankPacket(powerBankPackInfo);
        powerbankLogMapper.insertSelective(powerbankLog);
    }*/

    /**
     * 更新充电宝的信息
     * newback
     *
     * @param allLeng
     * @param newBack
     * @param subAllPos
     * @param bid
     * @param deviceUuid
     * @param machineUuid
     * @param commandData
     * @param receiveTime
     * @param currentTime
     * @return
     */
    @Transactional
    public int updatePowerbankNewBack(String deviceUuid, String machineUuid, Powerbank powerbank, PowerBankPackInfo powerBankPackInfo, String commandData, JSONObject msg) {
        if (CHARGE_RECENTED_STATE.equals(powerBankPackInfo.getBackRes()) && !redisService.hasKey(powerBankPackInfo.getPowerNo())) {

            log.info("====有上报归还事件命令=========================================================");
            log.info("====单个仓位信息: 仓位Id" + powerBankPackInfo.getPosId() + " ;归还结果: " + powerBankPackInfo.getBackRes() + ";充电宝状态" + powerBankPackInfo.getPosState() + ";电压值: " + powerBankPackInfo.getPowerADInt()
                    + "充电宝Id: " + powerBankPackInfo.getPowerNo() + " ;温度： " + powerBankPackInfo.getTemp());
            powerbank.setState(0);
            powerbank.setErrorState(0);
            powerbank.setPowerAd(powerBankPackInfo.getPowerADInt());
            powerbank.setBackTime(new Date(System.currentTimeMillis()));
            powerbank.setPowerNo(powerBankPackInfo.getPowerNo());
            powerbank.setChargingSwitch(CHARGE_RECENTED_STATE.equals(powerBankPackInfo.getPosState()));
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbankMapper.updateByPrimaryKeySelective(powerbank);//更改充电宝状态
            //即需要跟新用户未付款订单：已归还，更新小程序的租借订单状态
            OrderRentPay orderRentPay = orderPayService.updateOrderRentPay(powerbank);
            int state = orderRentPay.getOrderState();
            String hardVersion = deviceInfoMapper.getHardVersion(deviceUuid);
            boolean res = DeviceUtil.cleanPos(deviceUuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), SEND_ACK_CLEAN_CODE, hardVersion);
            String cleanPosMsg = DeviceUtil.getCleanPosMsg(deviceUuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), SEND_ACK_CLEAN_CODE, hardVersion);
            String s = DeviceUtil.commandParse(cleanPosMsg);
            logService.addFindbackLog(deviceUuid,CHARGE_RECENTED_STATE, cleanPosMsg+" ==> 仓位："+powerbank.getAllPositionUuild() +"，充电宝ID："+ powerbank.getPowerNo() + "，「归还」清除仓位：" + s, 4, msg);//上报的命令
            log.info("订单结算状态：更改状态: " + state + ", 下发give_back清除状态：是否成功: " + res);
            DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
            if(!UtilTool.isNull(orderRentPay.getId()) && StringUtils.isNotEmpty(info.getUrl())){//将结果及时回调给调用方
                powerbank.setDeviceUuid(deviceUuid);
                powerbank.setMachineUuid(machineUuid);
                powerbank.setPositionUuid(powerBankPackInfo.getPosId());
                // ************充电宝信息更新，通知stw后台***************
                logService.addOrderLog(deviceUuid, orderRentPay.getOrderNo(), commandData, 3, msg); // 订单日志
                adviceNewBack(powerbank, ADVICE_TYPE_BACK, orderRentPay.getOrderNo());
            }
        }else {
            if (powerbank.getErrorState() != 2) {
                powerbank.setErrorState(0);
            }
            powerbank.setPowerNo(powerBankPackInfo.getPowerNo());
            powerbank.setState(0);
            powerbank.setPowerAd(powerBankPackInfo.getPowerADInt());
            powerbank.setChargingSwitch(CHARGE_RECENTED_STATE.equals(powerBankPackInfo.getPosState()));
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbankMapper.updateByPrimaryKeySelective(powerbank);//更改充电宝状态
        }
        return 0;
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////                                                                                  回调日志                                                                             ////



    /**
     * 归还回调
     * @param callbackUrl
     * @param deviceUuid
     * @param machineUuid
     * @param positionId
     * @param powerNo
     */
    /*private void parseHttpReq(String callbackUrl,String deviceUuid,String machineUuid,String positionId, String powerNo, Integer powerAd){
        JSONObject sendJson= new JSONObject();
        sendJson.put("action","give_back");
        sendJson.put("state","0");
        sendJson.put("message","ok");
        sendJson.put("power_no",powerNo);
        sendJson.put("device_uuid",deviceUuid);
        sendJson.put("machine_uuid",machineUuid);
        sendJson.put("position_id",positionId);
        sendJson.put("power_ad", powerAd);

        String result= HttpClientUtils.doPostJson(callbackUrl,sendJson.toJSONString());
        if(StringUtils.isNotEmpty(result)){
            Map map = JSON.parseObject(result);
            String value= map.get("result").toString();
            if( StringUtils.isNotEmpty(value) &&value.equals("sc_ok")){
                //打印
                log.info("归还回调成功!");
            }
        }
    }*/

    /**
     * TODO 2020-02-01
     * 更新充电宝信息包含新归还设备，通知stw后台
     * @param flag 操作类型
     * @param powerbank
     */
    @Klock
    private void adviceNewBack(Powerbank powerbank, String action, @KlockKey String orderNo) {
        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(powerbank.getDeviceUuid());
        String actionStr = null;
        JSONObject obj= new JSONObject();
        if (action.equals(ADVICE_TYPE_NEW)) {
            actionStr = "充电宝信息上报";
        } else if (action.equals(ADVICE_TYPE_BACK)) {
            actionStr = "设备归还";
            if (orderNo != null && !"".equals(orderNo)) {
                obj.put("order_no", orderNo);
            }
        }
        if (StringUtils.isNotEmpty(info.getUrl())) {
            obj.put("action", action);
            obj.put("message","ok");
            obj.put("state","0");
            obj.put("power_no", powerbank.getPowerNo());
            obj.put("device_uuid",powerbank.getDeviceUuid());
            obj.put("machine_uuid",powerbank.getMachineUuid());
            if (info.getSpaceNu() == 9) {
                obj.put("position_uuid",powerbank.getPositionUuid());
            } else {
                Integer allPositionUuild = powerbank.getAllPositionUuild();
                obj.put("position_uuid",allPositionUuild!=null?allPositionUuild.toString():getPowerPosition(powerbank.getPositionUuid(), powerbank.getMachineUuid()));
            }
            obj.put("power_ad", powerbank.getPowerAd());
            String msg = actionStr +"回调通知stw服务,deviceUuid:"+obj.toJSONString();
            log.info("~~~~~~~~~~~~~~~~~~"+msg+"~~~~~~~~~~~~~~~~~~~~~~~~");
//            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_GIVE_BACK_SUCCESS, msg, CALL_STW_GIVE_BACK);
            obj.put("execTime", new Date().getTime());
            logService.addOrderLog(powerbank.getDeviceUuid(), orderNo, msg, 3, obj); // 订单日志
            String result= httpClientUtils.doPostJson(info.getUrl(), obj.toJSONString());
            //        if(StringUtils.isNotEmpty(result)){
            //            Map map = JSON.parseObject(result);
            //            String value= map.get("result").toString();
            //            if( StringUtils.isNotEmpty(value) &&value.equals("sc_ok")){
            //                //打印
            //                log.info("归还回调成功!");
            //            }
            //        }
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }

    /**
     * TODO 2020-02-02
     * 充电宝弹出回调，通知stw后台
     * @param deviceUuid
     * @param powerNo
     * @param state 1:成功  2:异常
     */
    @Klock
    private void advicePowerbankInfo(int userId, Powerbank powerbank, int state, @KlockKey String orderNo) {
        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(powerbank.getDeviceUuid());
        if (StringUtils.isNotEmpty(info.getUrl())) {
            String msg = "弹出充电宝回调通知stw服务,userId:"+ userId +",deviceUuid:"+powerbank.getDeviceUuid()+",powerNo:"+ powerbank.getPowerNo() +", state:"+ state +"";
            log.info("~~~~~~~~~~~~~~~~~~~~~"+ msg + "~~~~~~~~~~~~~~~~~~~~");
//            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_RENT_SUCCESS, msg, CALL_STW_RENT);
            JSONObject obj= new JSONObject();
            obj.put("execTime", new Date().getTime());
            logService.addOrderLog(powerbank.getDeviceUuid(), orderNo, msg, 2, obj); // 订单日志
            obj.put("action", "state_update");
            obj.put("state", state);
            obj.put("device_uuid",powerbank.getDeviceUuid());
            obj.put("power_no", powerbank.getPowerNo());
            obj.put("user_id", userId);
            obj.put("order_no", orderNo);
            if (info.getSpaceNu() == 9) {
                obj.put("position_uuid", powerbank.getPositionUuid());
            } else {
                Integer allPositionUuild = powerbank.getAllPositionUuild();
                obj.put("position_uuid",allPositionUuild!=null?allPositionUuild.toString():getPowerPosition(powerbank.getPositionUuid(), powerbank.getMachineUuid()));
            }

            String result= httpClientUtils.doPostJson(info.getUrl(), obj.toJSONString());

            /*System.out.println(JSONObject.parse(result));*/
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }

    /**
     * TODO 2020-02-02
     * 设备上下线通知
     * @param deviceUuid
     * @param state 1:上线  0:下线
     */
    public void advicePowerbankOnOffLine(String deviceUuid, int state) {
        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
        if(info ==null) return;
        if (StringUtils.isNotEmpty(info.getUrl())) {
            String msg = "充电宝上下线回调通知stw服务,deviceUuid:" + deviceUuid + ",state" + state;
            JSONObject obj = new JSONObject();
            obj.put("action", "1001");
            obj.put("execTime", new Date().getTime());
            logService.addFindbackLog(deviceUuid, CALL_STW_RENT_SUCCESS, msg, CALL_STW_RENT, obj);
            obj.put("state", state);
            obj.put("deviceUuid", deviceUuid);
            // log.info("\n"+"------------------------------设备上下线回调开始------------------------------"+"\n"
            //         +"action:" + "1001" + "\n" +
            //         "execTime:" + obj.get("execTime") + "\n" +
            //         "state:" + state + "\n" +
            //         "deviceUuid:" + deviceUuid + "\n" );
            String result = httpClientUtils.doPostJson(info.getUrl(), obj.toJSONString());
            // log.info("\n"+"------------------------------设备上下线回调结束------------------------------"+"\n");
            /*System.out.println(JSONObject.parse(result));*/
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }

    /**
     * 设备信息全量上报回调
     * @param deviceUuid
     * @param powerbankVoList
     * @param machineUuid
     */
    private void advicePowerInfo(String deviceUuid, List<PowerbankVo> powerbankVoList, String machineUuid) {
        JSONObject obj = new JSONObject();
        obj.put("action", "1002");
        obj.put("deviceUuid", deviceUuid);
        obj.put("machineUuid", machineUuid);
        obj.put("powerbanks", powerbankVoList);
        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(deviceUuid);
        if (info.getUrl() != null && !"".equals(info.getUrl())) {
            // log.info("\n"+"------------------------------全量上报开始------------------------------"+"\n" +
            //         "action：" + obj.get("action")+"\n"+
            //         "deviceUuid：" + obj.get("deviceUuid")+"\n"+
            //         "machineUuid：" + obj.get("machineUuid")+"\n"+
            //         "powerbanks：" + obj.get("powerbanks")+"\n");
            String result= httpClientUtils.doPostJson(info.getUrl(), obj.toJSONString());
            // log.info("\n"+"------------------------------全量上报结束------------------------------"+"\n");

            String msg = "设备信息全量上报回调通知stw服务,deviceUuid:" + deviceUuid + ",powerbanks:"+ JSONObject.toJSONString(powerbankVoList);
            //log.info("~~~~~~~~~~~~~~~~~~" + msg + "~~~~~~~~~~~~~~~~~~~~~~~~");
            obj.put("execTime", new Date().getTime());
            logService.addFindbackLog(deviceUuid, CALL_STW_GIVE_BACK_SUCCESS, msg, CALL_STW_GIVE_BACK, obj);
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }

    /**
     * TODO 2021-04-13
     * 充电宝弹出回调，无需订单
     * @param deviceUuid
     * @param powerNo
     * @param state 0:成功  1:异常
     */
    private void advicePowerbankInfo(Powerbank powerbank, int state) {
        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(powerbank.getDeviceUuid());
        if (StringUtils.isNotEmpty(info.getUrl())) {

            //log.info("~~~~~~~~~~~~~~~~~~~~~"+ msg + "~~~~~~~~~~~~~~~~~~~~");
//            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_RENT_SUCCESS, msg, CALL_STW_RENT);
            JSONObject obj= new JSONObject();
            obj.put("action", "1003");
            //obj.put("execTime", new Date().getTime());
            obj.put("state", state);
            obj.put("deviceUuid",powerbank.getDeviceUuid());
            obj.put("powerNo", powerbank.getPowerNo());
            obj.put("machineUuid",powerbank.getMachineUuid());
            //logService.addOrderLog(powerbank.getDeviceUuid(), orderNo, msg, 2, obj); // 订单日志
            //obj.put("user_id", userId);
            //obj.put("order_no", orderNo);
            /*if (info.getSpaceNu() == 9) {
                obj.put("positionUuid", powerbank.getPositionUuid());
            } else {
                Integer allPositionUuild = powerbank.getAllPositionUuild();
                obj.put("positionUuid",allPositionUuild!=null?allPositionUuild.toString():getPowerPosition(powerbank.getPositionUuid(), powerbank.getMachineUuid()));
            }*/
            obj.put("positionUuid", powerbank.getPositionUuid());
            String msg = "设备服务弹出回调:"+obj.toString();
            //需要修改--3s发一次，有效期20s或者收到请求地址响应参数后停止发送
            // log.info("\n"+"------------------------------弹出回调开始------------------------------"+"\n" +
            //         "action: "+obj.get("action")+"\n"+
            //         //"execTime: "+obj.get("execTime")+"\n"+
            //         "state: "+obj.get("state")+"\n"+
            //         "deviceUuid: "+obj.get("deviceUuid")+"\n"+
            //         "powerNo: "+obj.get("powerNo")+"\n"+
            //         "machineUuid: "+obj.get("machineUuid")+"\n"+
            //         "positionUuid: "+obj.get("positionUuid")+"\n");
            httpClientUtils.callback(info, obj);
            // log.info("\n"+"------------------------------弹出回调结束------------------------------"+"\n");
            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_RENT_SUCCESS, msg, CALL_STW_RENT, obj);
            /*System.out.println(JSONObject.parse(result));*/
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }

    /**
     * TODO 2021-04-13
     * 归还回调，无需订单
     * @param flag 操作类型
     * @param powerbank
     */
    private void returnCallback(Powerbank powerbank) {
        OrderRentPay orderRentPay = orderPayService.updateOrderRentPay(powerbank);
        //int state = orderRentPay.getOrderState();


        DeviceInfo info = deviceInfoMapper.selectDeviceInfoByUuId(powerbank.getDeviceUuid());
        //String actionStr = null;
        JSONObject obj= new JSONObject();
        //if (action.equals(ADVICE_TYPE_NEW)) {
        //    actionStr = "充电宝信息上报";
        //} else if (action.equals(ADVICE_TYPE_BACK)) {
        //    actionStr = "设备归还";
        //    /*if (orderNo != null && !"".equals(orderNo)) {
        //        obj.put("order_no", orderNo);
        //    }*/
        //}
        if (StringUtils.isNotEmpty(info.getUrl())) {
            obj.put("action", "1004");
            //obj.put("message","ok");
            //obj.put("state","0");
            obj.put("powerNo", powerbank.getPowerNo());
            obj.put("deviceUuid",powerbank.getDeviceUuid());
            obj.put("machineUuid",powerbank.getMachineUuid());
            //if (info.getSpaceNu() == 9) {
            //    obj.put("positionUuid",powerbank.getPositionUuid());
            //} else {
            //    Integer allPositionUuild = powerbank.getAllPositionUuild();
            //    obj.put("positionUuid",allPositionUuild!=null?allPositionUuild.toString():getPowerPosition(powerbank.getPositionUuid(), powerbank.getMachineUuid()));
            //}
            obj.put("positionUuid",powerbank.getPositionUuid());
            obj.put("powerAd", powerbank.getPowerAd());
            String msg = "设备服务归还回调："+obj.toJSONString();
            //log.info("~~~~~~~~~~~~~~~~~~"+msg+"~~~~~~~~~~~~~~~~~~~~~~~~");
//            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_GIVE_BACK_SUCCESS, msg, CALL_STW_GIVE_BACK);
//            obj.put("execTime", new Date().getTime());
            //logService.addOrderLog(powerbank.getDeviceUuid(), orderNo, msg, 3, obj); // 订单日志
            // log.info("\n"+"------------------------------归还回调开始------------------------------"+"\n" +
            //                 "action: "+obj.get("action")+"\n"+
            //         "powerNo: "+obj.get("powerNo")+"\n"+
            //         "deviceUuid: "+obj.get("deviceUuid")+"\n"+
            //         "machineUuid: "+obj.get("machineUuid")+"\n"+
            //         "positionUuid: "+obj.get("positionUuid")+"\n"+
            //         "powerAd: "+obj.get("powerAd")+"\n");
            httpClientUtils.callback(info, obj);
            // log.info("\n"+"------------------------------归还回调结束------------------------------"+"\n");
            logService.addFindbackLog(powerbank.getDeviceUuid(), CALL_STW_RENT_SUCCESS, msg, CALL_STW_RENT, obj);

            //        if(StringUtils.isNotEmpty(result)){
            //            Map map = JSON.parseObject(result);
            //            String value= map.get("result").toString();
            //            if( StringUtils.isNotEmpty(value) &&value.equals("sc_ok")){
            //                //打印
            //                log.info("归还回调成功!");
            //            }
            //        }
        } else {
            log.info("### 未设置回调地址 ###");
        }
    }



    /**
     * 更新充电宝的信息
     *
     * @param allLeng
     * @param newBack
     * @param subAllPos
     * @param deviceUuid
     * @param machineUuid
     * @param bid
     * @return
     */
    @Transactional
    public int updatePowerbankUpdate(Powerbank powerbank, PowerBankPackInfo powerBankPackInfo) {

        powerbank.setState(0);
        powerbank.setPowerAd(powerBankPackInfo.getPowerADInt());
        if (powerbank.getErrorState() != 2) {
            powerbank.setErrorState(0);
        }
        powerbank.setPowerNo(powerBankPackInfo.getPowerNo());
        powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
        powerbankMapper.updateByPrimaryKeySelective(powerbank);//更改充电宝状态
//        log.info("---------------更新充电宝的电压信息-------------");
        return 0;
    }

////                                                                                  回调日志                                                                             ////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    @Transactional
    @Klock
    public R startRent(String userId, @KlockKey String uuid, String positionUuid, String orderNo) {

        if (UtilTool.isNull(uuid)) {
            return RUtil.error("the device uuid is null!");
        }

        DeviceInfo deviceInfo = getDeviceInfoByUuid(uuid);
        if (UtilTool.isNull(deviceInfo)) {
            return RUtil.error("the device is not exist");
        } else if (deviceInfo.getDeviceState() == 0) {
            return RUtil.error("the device is in-activation");
        } else if (getDeviceState(uuid) == false) {
            return RUtil.error("the device is offline");
        } else {
            /* 该逻辑注释：可能存在用户可租借多个订单的需求
            // 判断用户是否存在订单
            OrderRentPay lastOrderByUserWithin1min = rentPayMapper.getLastOrderByUserWithin1min(userId);
            if (lastOrderByUserWithin1min != null && lastOrderByUserWithin1min.getOrderState() != 1) {
                return RUtil.error("the user has unfinished order");
            }*/

            Powerbank powerbank = null;
            if (StringUtils.isEmpty(positionUuid)) {
                powerbank = powerbankMapper.selectMaxPowerBank(deviceInfo.getDeviceUuid());
            } else {
                if (deviceInfo.getSpaceNu() != 9) {
                    PositionVo position = StringUtils.getPosition(positionUuid);
                    String machineUuid = position.getMachineUuid();
                    powerbank = powerbankMapper.selectPowerBankByUuidAndAllPositionUuid(uuid, positionUuid, machineUuid);
                } else {
                    powerbank = powerbankMapper.selectPowerBankByUuidAndPositionId(uuid, positionUuid, "01");
                }
            }
            if (UtilTool.isNull(powerbank)) {
                return RUtil.error("the device has no any charge");
            }
            // 判断充电宝是否存在订单
            OrderRentPay lastOrderBybatteryWithin1min = rentPayMapper.getLastOrderByBatteryWithin1min(powerbank.getPowerNo());
            if (lastOrderBybatteryWithin1min != null && lastOrderBybatteryWithin1min.getOrderState() != 1) {
                return RUtil.error("the device is bussy");
            }

            if (!redisService.setLock("state" + uuid, uuid, 1)) {
                return RUtil.error(201, "the device is busing now,scanner code again please");
            }

            log.info("scanCode:" + JSON.toJSONString(powerbank) + "");

            boolean result = sendMsgTo4g(Integer.parseInt(userId), uuid, powerbank, 0, orderNo);
            log.info("the result of sending command to device ：" + result);
            if (result) {
                redisService.set(powerbank.getPowerNo(), "0", 20);
                OrderRentPay rentPay = orderPayService.createdOrderRentPay(userId, powerbank, 0, orderNo);
                log.info("it has generated a pre-order,power no:" + rentPay.getPowerNo());
                //powerbank.setState(2); было так
                powerbankMapper.updateByPrimaryKey(powerbank);
                Map<String, String> r = new HashMap<>();
                r.put("powerNo", powerbank.getPowerNo());
                Integer allPositionUuild = powerbank.getAllPositionUuild();
                if (allPositionUuild != null) {
                    r.put("positionUuid", allPositionUuild.toString());
                } else {
                    r.put("positionUuid", powerbank.getPositionUuid());
                    r.put("machineUuid", powerbank.getMachineUuid());
                }
                return RUtil.success(r);
//                return RUtil.success(powerbank.getPositionUuid());
            } else {
                return RUtil.error("the device has a error,wait a while and do again");
            }
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public boolean sendMsgTo4g(int userId, String uuid, Powerbank powerbank, int state, String orderNo) {
        String str_machineId = powerbank.getMachineUuid();
        String str_positionNo = StringUtils.leftFillZero(powerbank.getPositionUuid(), 2);

        String str = userId + "," + uuid + "," + powerbank.getMachineUuid() + "," + powerbank.getPositionUuid();
        String hardVersion = deviceInfoMapper.getHardVersion(uuid);

        boolean result = DeviceUtil.eject(uuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), hardVersion);

        String ejectCommand = DeviceUtil.getEjectCommand(uuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), hardVersion);

        String s = DeviceUtil.commandParse(ejectCommand);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("execTime", new Date().getTime());
        logService.addFindbackLog(uuid, BEGIN_RECENT_COMMAND_CODE, ejectCommand+" ==> 仓位："+powerbank.getAllPositionUuild() +"，充电宝ID："+ powerbank.getPowerNo() + "，「租借」弹出：" + s, 10, jsonObject);
        logService.addOrderLog(uuid, orderNo, ejectCommand+" ==> "+s, 1, jsonObject);
        if (result) {
            //redisService.set(powerbank.getPowerNo(), "0", 20);
            redisService.set(DeviceUtil.getDeviceEjectRedisName(uuid, str_machineId, str_positionNo), str, EXPIRE_TIME);
        }
        return result;
    }

    /**
     * 弹宝
     * @param uuid
     * @param machineId the mcu No; a device include one or more mcu
     * @param positionNo the postion of warehouse
     * @return
     */
    @Override
    public Boolean leaseIssue(String uuid, String machineId, String positionNo) {
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionNo.substring(positionNo.length() - 1, positionNo.length()));
        powerbank.setMachineUuid(machineId);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        if(!UtilTool.isNull(powerbank)){
            String powerbankNo = powerbank.getPowerNo();
            //if(machineId.length() < 2){
            //    machineId = "0" + machineId;
            //}
            //if(positionNo.length() < 2){
            //    positionNo = "0" + positionNo;
            //}
            redisService.set("DeviceEject_"+uuid+"_"+machineId+"_"+positionNo,uuid +","+machineId+","+positionNo,60);
            //return false;
        }else{
            log.info("\n"+"------------------------该仓位不存在充电宝----------------"+"\n");
        }

        return DeviceUtil.eject(uuid, machineId, positionNo, deviceInfoMapper.getHardVersion(uuid));
    }

    /**
     * 传时间戳进行弹宝
     * @param execTime 时间戳
     * @param uuid 设备uuid
     * @param machineId 从机id
     * @param positionNo 仓位id
     * @return
     */
    @Override
    public Boolean leaseIssue(String execTime,String uuid, String machineId, String positionNo) {
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionNo.substring(positionNo.length() - 1, positionNo.length()));
        powerbank.setMachineUuid(machineId);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        if(!UtilTool.isNull(powerbank)){
            String powerbankNo = powerbank.getPowerNo();
            //if(machineId.length() < 2){
            //    machineId = "0" + machineId;
            //}
            //if(positionNo.length() < 2){
            //    positionNo = "0" + positionNo;
            //}
            redisService.set("DeviceEject_"+uuid+"_"+machineId+"_"+positionNo,uuid +","+machineId+","+positionNo,60);
            //return false;
        }else{
            log.info("\n"+"------------------------该仓位不存在充电宝----------------"+"\n");
        }
        String positionId = "";
        if(positionNo.length()==1){
            positionId = positionNo;
        }else if(positionNo.length() == 2){
            positionId = positionNo.substring(1);
        }

        return DeviceUtil.eject(execTime,uuid, machineId, positionNo, deviceInfoMapper.getHardVersion(uuid));
    }

    /**
     * 弹宝
     * @param uuid
     * @param machineId the mcu No; a device include one or more mcu
     * @param positionNo the postion of warehouse
     * @return
     */
    @Override
    public Boolean openLock(String uuid, String machineId, String positionNo) {
        return DeviceUtil.openLock(uuid, machineId, positionNo);
    }


    @Override
    public List<DeviceInfo> queryDeviceList(DeviceInfo deviceInfo) {

        return deviceInfoMapper.queryDeviceList(deviceInfo);
    }




    @Override
    @Transactional
    public int addDeviceInfo(DeviceInfo deviceInfo) {
        return deviceInfoMapper.insertSelective(deviceInfo);
    }

    @Override
    public int delDeviceInfo(String deviceUuid) {
        return deviceInfoMapper.deleteByDeviceUuid(deviceUuid);
    }



    @Override
    public int editDeviceInfo(DeviceInfo deviceInfo) {
        return deviceInfoMapper.updateByDeviceUuidSelective(deviceInfo);
    }



    @Override
    public void updateNettyDeviceToOffline() {
        deviceInfoMapper.updateNettyDeviceToOffline();
    }




    @Override
    public List<DeviceInfo> selectDeviceInfoByHardVersion(String versionInfo) {
        return deviceInfoMapper.selectDeviceInfoByHardVersion(versionInfo);
    }

    @Override
    public List<DeviceInfo> selectDeviceInfoByProtocolVersion(String versionInfo) {
        return deviceInfoMapper.selectDeviceInfoByAgreementVersion(versionInfo);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public R getPowerbankDetail(String orderNo, String deviceUuid) {
        Map<String,Object> mapRentPay= orderPayService.getOrderRentDetail(orderNo, deviceUuid);
        if(null == mapRentPay){
            return RUtil.error("the order is not exist.");
        }
        String meno= mapRentPay.get("memo").toString();
        Powerbank powerbank = powerbankMapper.selectByPrimaryKey(Integer.valueOf(meno));
        if (null == powerbank) {
            return RUtil.error("the device is not exist.");
        }
        DeviceInfo info = new DeviceInfo();
        info.setDeviceUuid(powerbank.getDeviceUuid());
        info = deviceInfoMapper.selectOne(info);
        Map<String, Object> m = new HashMap<>();
        m.put("positionUuid", powerbank.getPositionUuid());
        m.put("machineUuid", powerbank.getMachineUuid());
        m.put("deviceModel", info.getSpaceNu());
        if (info.getSpaceNu().equals("6")) {
            m.put("allPositionUuidCol", 1);
            m.put("allPositionUuidRow", 1);
        } else if (info.getSpaceNu().equals("9")) {
            m.put("allPositionUuidCol", 1);
            m.put("allPositionUuidRow", 1);
        } else if (info.getSpaceNu().equals("12")) {
            m.put("allPositionUuidCol", 2);
            m.put("allPositionUuidRow", 1);
        } else if (info.getSpaceNu().equals("36")) {
            m.put("allPositionUuidCol", 6);
            m.put("allPositionUuidRow", 1);
        } else if (info.getSpaceNu().equals("54")) {
            m.put("allPositionUuidCol", 3);
            m.put("allPositionUuidRow", 3);
        } else if (info.getSpaceNu().equals("108")) {
            m.put("allPositionUuidCol", 6);
            m.put("allPositionUuidRow", 3);
        }
        m.put("allPositionUuild", powerbank.getAllPositionUuild());
        Map<String, Object> map = new HashMap<>();
        map.put("orderRentPay", mapRentPay);
        map.put("message", m);
        return RUtil.success(map);
    }


    public String createdOrder(String[] str) {


        int userId = Integer.parseInt(str[0]);
        String uuid = str[1];
        String machineUuid = str[2];
        String positionUuid = str[3];
//        WxaUser wxaUser = userMapper.selectByPrimaryKey(userId);
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);

        OrderRentPay orderRentPay = orderPayService.getOrderRentPayByState(userId, powerbank);
        if (UtilTool.isNull(orderRentPay)) {
            return "";
        }

     /*   wxaUser.setLoanType(1);
        userMapper.updateByPrimaryKey(wxaUser);*/

        log.info(" warehouse id {}, machine id{}", powerbank.getPositionUuid(), powerbank.getMachineUuid());

        powerbank.setState(1);
        powerbank.setUpdateTime(new Date(System.currentTimeMillis()));

        int i = powerbankMapper.updateByPrimaryKeySelective(powerbank);



        PowerbankPositionLog powerbankPositionLog = new PowerbankPositionLog();

        powerbankPositionLog.setPowerNo(powerbank.getPowerNo());
        powerbankPositionLog.setUserId(userId);
        powerbankPositionLog.setPositionUuid(powerbank.getPositionUuid());
        powerbankPositionLog.setModifyTime(new Date(System.currentTimeMillis()));
        powerbankPositionLog.setDeviceUuid(uuid);

        powerbankPositionLog.setState(1);
        powerbankPositionLog.setCreateTime(new Date(System.currentTimeMillis()));
        positionLogMapper.insertSelective(powerbankPositionLog);
        // *******************stw:弹出成功通知stw后台********************
        advicePowerbankInfo(userId, powerbank,1, orderRentPay.getOrderNo());
        // *******************stw:弹出成功通知stw后台********************
        return orderRentPay.getOrderNo();
    }

    @Transactional
    public String errorDispose(String[] str) {

        int userId = Integer.parseInt(str[0]);
        String uuid = str[1];
        String machineUuid = str[2];
        String positionUuid = str[3];
//        WxaUser wxaUser = userMapper.selectByPrimaryKey(userId);
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        if (UtilTool.isNull(powerbank)) {
            powerbank = new Powerbank();
            powerbank.setPositionUuid(positionUuid);
            powerbank.setMachineUuid(machineUuid);
            powerbank.setDeviceUuid(uuid);
            powerbank.setPowerNo("00");
            powerbank.setPowerAd(10);
            //powerbank.setErrorState(1);
            //powerbank.setState(1);
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbank.setCreatedTime(new Date(System.currentTimeMillis()));
            powerbankMapper.insertSelective(powerbank);
        } else {
            //powerbank.setState(1);
            //powerbank.setErrorState(1);
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbankMapper.updateByPrimaryKey(powerbank);
        }

        // *******************stw:弹出失败通知stw后台订单失效********************
        OrderRentPay orderRentPay = orderPayService.getOrderRentPayByState(userId, powerbank);
        if (UtilTool.isNull(orderRentPay)) {
            return "";
        }
        advicePowerbankInfo(userId, powerbank,2, orderRentPay.getOrderNo());
        // *******************stw:弹出失败通知stw后台订单失效********************
      /*  Powerbank p = powerbankMapper.selectPowerBank(uuid, powerbank.getId());

        if (UtilTool.isNull(powerbank)) {
            log.info("the device " + uuid + " is empty");
            redisService.del("state" + uuid);
            // *******************stw:弹出失败通知stw后台订单失效********************
            advicePowerbankInfo(userId, powerbank.getDeviceUuid(), powerbank.getPowerNo(), 2);
            // *******************stw:弹出失败通知stw后台订单失效********************
        } else {
            // 重试弹出
            sendMsgTo4g(userId, uuid, p, 1);
            OrderRentPay rentPay = orderPayService.createdOrderRentPay(userId+"", powerbank, -1);
            log.info("it has generated a pre-order,power no:" + rentPay.getPowerNo());
            powerbank.setState(2);
            powerbankMapper.updateByPrimaryKey(powerbank);
        }*/
        return orderRentPay.getOrderNo();
    }

    /**
     * 发起弹宝成功回调
     * @param str
     * @return
     */
    @Async
    public void successReturn(String[] str) {
        String uuid = str[0];
        String machineUuid = str[1];
        String positionUuid = str[2];
//        WxaUser wxaUser = userMapper.selectByPrimaryKey(userId);
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);


     /*   wxaUser.setLoanType(1);
        userMapper.updateByPrimaryKey(wxaUser);*/

        //log.info(" warehouse id {}, machine id{}", powerbank.getPositionUuid(), powerbank.getMachineUuid());

        powerbank.setState(1);
        powerbank.setUpdateTime(new Date(System.currentTimeMillis()));

        int i = powerbankMapper.updateByPrimaryKeySelective(powerbank);

        PowerbankPositionLog powerbankPositionLog = new PowerbankPositionLog();

        powerbankPositionLog.setPowerNo(powerbank.getPowerNo());
        //powerbankPositionLog.setUserId(userId);
        powerbankPositionLog.setPositionUuid(powerbank.getPositionUuid());
        powerbankPositionLog.setModifyTime(new Date(System.currentTimeMillis()));
        powerbankPositionLog.setDeviceUuid(uuid);
        powerbankPositionLog.setState(1);
        powerbankPositionLog.setCreateTime(new Date(System.currentTimeMillis()));
        positionLogMapper.insertSelective(powerbankPositionLog);
        // *******************stw:弹出成功通知stw后台********************
        //state：0:成功；1:异常
        advicePowerbankInfo(powerbank,0);
        // *******************stw:弹出成功通知stw后台********************

    }

    /**
     * 发起弹宝失败回调
     * @param str
     * @return
     */
    @Async
    @Transactional
    public void errorReturn(String[] str) {

        //int userId = Integer.parseInt(str[0]);
        String uuid = str[0];
        String machineUuid = str[1];
        String positionUuid = str[2];
//        WxaUser wxaUser = userMapper.selectByPrimaryKey(userId);
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(uuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        if (UtilTool.isNull(powerbank)) {
            powerbank = new Powerbank();
            powerbank.setPositionUuid(positionUuid);
            powerbank.setMachineUuid(machineUuid);
            powerbank.setDeviceUuid(uuid);
            powerbank.setPowerNo("00");
            powerbank.setPowerAd(10);
            //powerbank.setErrorState(1);
            powerbank.setState(1);
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbank.setCreatedTime(new Date(System.currentTimeMillis()));
            powerbankMapper.insertSelective(powerbank);
        } else {
            //TODO 弹出失败，不修改租借状态
            //powerbank.setState(1);
            //powerbank.setErrorState(1);
            powerbank.setUpdateTime(new Date(System.currentTimeMillis()));
            powerbankMapper.updateByPrimaryKey(powerbank);
        }

        // *******************stw:弹出失败通知stw后台订单失效********************
        //state，0:成功；1:失败
        advicePowerbankInfo(powerbank,1);
        // *******************stw:弹出失败通知stw后台订单失效********************

        //return "ok";
    }

    @Override
    public R sendMsg(String uuid, String positionId) {
        DeviceInfo deviceInfo = getDeviceInfoByUuid(uuid);
        if (UtilTool.isNull(deviceInfo)) {
            return RUtil.error(210, "the device is not exist.");
        } else if (deviceInfo.getDeviceState() == 0) {
            return RUtil.error(211, "the device is offline.");
        } else {
            Object object = redisService.get("state" + uuid);
            if (!UtilTool.isNull(object)) {
                return RUtil.error(201, "the device is busing now,scanner code again please");
            }
            redisService.set("state" + uuid, uuid, 10);
            if (getDeviceState(uuid) == false) {
                RUtil.error("the device is offline.");
            }
            Powerbank powerbank = null;
            if (StringUtils.isEmpty(positionId)) {
                powerbank = powerbankMapper.selectMaxPowerBank(deviceInfo.getDeviceUuid());
            } else {
                if (deviceInfo.getSpaceNu() != 9) {
                    PositionVo position = StringUtils.getPosition(positionId);
                    powerbank = powerbankMapper.selectPowerBankByUuidAndAllPositionUuid(uuid, positionId, position.getMachineUuid());
                } else if( deviceInfo.getSpaceNu() == 6){
                    powerbank = powerbankMapper.selectPowerBankByUuidAndPositionId(uuid, positionId, "01");
                }
                else {
                    powerbank = powerbankMapper.selectPowerBankByUuidAndPositionId(uuid, positionId, "01");
                }
            }
            if (UtilTool.isNull(powerbank)) {
                redisService.del("state" + uuid);
                return RUtil.error("the device is empty");
            }

            //发送给设备状态
            boolean result = DeviceUtil.eject(uuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), deviceInfo.getHardVersion());

            String ejectCommand = DeviceUtil.getEjectCommand(uuid, powerbank.getMachineUuid(), powerbank.getPositionUuid(), deviceInfo.getHardVersion());
            String[] split = ejectCommand.split(",");
            String command = split[split.length - 1];
            byte[] decode = Base64.getMimeDecoder().decode(command);
            String s = com.td.util.HexToBinaryUtils.bytesToHexString(decode);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("execTime", new Date().getTime());
            logService.addFindbackLog(uuid, BEGIN_RECENT_COMMAND_CODE, ejectCommand+" ==> 仓位："+powerbank.getAllPositionUuild() +"，充电宝ID："+ powerbank.getPowerNo() + "，「强制」弹出：" + s, 10, jsonObject);
            redisService.set(uuid, uuid, 30);
            log.info("the result of sending command to device：" + result);
            if (result==true) {
                powerbank = powerbankMapper.selectByPrimaryKey(powerbank.getId());
                redisService.del("state" + uuid);
                powerbank.setState(1);
                powerbankMapper.updateByPrimaryKey(powerbank);
                return RUtil.success(powerbank);

            } else {
                return RUtil.error(201, "the device has a  error，wait a while and do again");
            }

        }
    }

    @Override
    public Powerbank getPowerbank(String positionUuid, String machineUuid, String deviceUuid) {
        Powerbank powerbank = new Powerbank();
        powerbank.setPositionUuid(positionUuid);
        powerbank.setMachineUuid(machineUuid);
        powerbank.setDeviceUuid(deviceUuid);
        powerbank = powerbankMapper.selectOne(powerbank);
        return powerbank;
    }


    @Override
    public List<Powerbank> getPowerbanks(String deviceUuid) {
        return powerbankMapper.selectAllByDeviceUuid(deviceUuid);
    }



    public boolean getDeviceState(String uuid) {
        String nonc = UtilTool.getOrderCode();
        String date = AliyunSign.getISOTime(new Date());
        Map<String, String> map = new HashMap<String, String>();
        map.put("Action", "GetDeviceStatus");
        map.put("DeviceName", uuid);
        map.put("ProductKey", AliyunConfig.PRODUCT_KEY);
        String signature = AliyunSign.getSignature(nonc, map, date);
        map.put("Signature", signature);
        map.put("SignatureNonce", nonc);
        map.put("Timestamp", date);
        String url = AliyunSign.getUrl(map);
        log.info("*****************url:" + url);
        String result = null;
        try {
            result = httpClientUtils.sendGetData(url, "uft-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("the result:：" + result);
        if (!UtilTool.isNull(result) && result.length() > 10) {
            JSONObject object = JSONObject.parseObject(result);
            if (object.getBoolean("Success")) {
                JSONObject jsonObject = JSONObject.parseObject(object.getString("Data"));
                if ("ONLINE".equals(jsonObject.getString("Status"))) {
                    return true;
                }
            }
        }
        return false;
    }



    public int alterPowerState(String uuid) {
        Powerbank p = new Powerbank();
        p.setDeviceUuid(uuid);
        List<Powerbank> powerbanks = powerbankMapper.select(p);
        for (Powerbank powerbank : powerbanks) {
            //设置充电宝状态
            powerbank.setState(1);
            powerbankMapper.updateByPrimaryKey(powerbank);
        }
        return 0;
    }



    public DeviceInfo getDeviceListByImei(String imei) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSn(imei);
        return deviceInfoMapper.selectOne(deviceInfo);
    }



    public int alterPowebankState(Powerbank powerbank) {
        powerbank.setState(1);
        powerbankMapper.updateByPrimaryKeySelective(powerbank);
        return 0;
    }

    @Override
    public R getRemainingPower(String deviceUuid) {
        boolean deviceState = this.getDeviceState(deviceUuid);
        if (!deviceState) {
            return RUtil.error("no device online");
        }
        int remainingPower = powerbankMapper.getRemainingPower(deviceUuid);
        return RUtil.success(remainingPower);
    }

    @Override
    public boolean upgrade(String uuId, String type, String filename) {
        String command = "101," + uuId + "," + type + ",8.129.26.167,ftptest,123456,"+filename;// 直接传递base64之后的字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("execTime", new Date().getTime());
        logService.addFindbackLog(uuId, UPGRADE, command, 11, jsonObject);
        return DeviceUtil.deviceUpgrade(uuId, type, "8.129.26.167", "ftptest", "123456", filename);
    }

    @Override
    public boolean upgradeAll(String type, String filename) {
        List<DeviceInfo> deviceInfos = deviceInfoMapper.selectAll();
        for (DeviceInfo deviceInfo : deviceInfos) {
            upgrade(deviceInfo.getDeviceUuid(), type, filename);
        }
        return true;
    }

    @Override
    public boolean uploadLog(String uuId, String ip, String username, String password) {
        String str_ejectCommand = "106," + uuId + "," + ip + "," + username + "," + password;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("execTime", new Date().getTime());
        logService.addFindbackLog(uuId, "106", str_ejectCommand, 0, jsonObject);
        return DeviceUtil.uploadLog(uuId, ip, username, password);
    }

    @Override
    public boolean updateParameter(String uuId) {
        String str_ejectCommand = "111," + uuId + ",1";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("execTime", new Date().getTime());
        logService.addFindbackLog(uuId, "111", str_ejectCommand, 0, jsonObject);
        return DeviceUtil.updateParameter(uuId);
    }

    @Override
    public boolean uploadRadio(String uuId, String url, String startDate, String endDate, String startTime, String endTime, String plays, String playTime) {
        // 108,869091031298820,1,http://admin-bing-an-print.oss-cn-beijing.aliyuncs.com/20201212122.mp4,2020/03/25,2020/12/31,08/00,23/59,0,0
        String comand = url + "," + startDate + "," + endDate + "," + startTime + "," + endTime + "," + plays + "," + playTime;
        return radio(uuId, "1", comand);
    }

    @Override
    public boolean deleteAllResource(String uuId) {
        // 108,869091031298820,0,,2020/03/25,2020/12/31,08/00,23/59,0,0
//        String comand = url + "," + startDate + "," + endDate + "," + startTime + "," + endTime + "," + plays + "," + playTime;
        String comand = ",2020/03/25,2030/12/30,08/00,23/59,0,0";
        return radio(uuId, "0", comand);
    }

    @Override
    public boolean deleteRadio(String uuId, String filename) {
        return radio(uuId, "2", filename);
    }

    private boolean radio(String uuId, String type, String command) {
        String str_ejectCommand = "108," + uuId + "," + type + "," + command;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("execTime", new Date().getTime());
        logService.addFindbackLog(uuId, "108", str_ejectCommand, 108, jsonObject);
        return DeviceUtil.uploadRadio(uuId, type, str_ejectCommand);
    }

    public static void main(String[] args) {

        JSONObject msg = new JSONObject();
        msg.put("payload","303,999133041313486,VTZEBAABAAAAALSqAAJ7AAIAAAAAtKoAA5wAAwAAAAC0qgABWgAEAAAAALSqAAJ6AAUAAAAAtKoAA5sABgAAAAC0qgAALgCo");
        msg.put("topic","/a4wOOtSxL5i/999133041313486/update");
        msg.put("generateTime","1619515719777");
        msg.put("qos",1);
        String payload = msg.remove("payload").toString();
        String requestJson = new String(JSONArray.parseObject(((JSONArray) msg.remove("payload")).toString(),byte[].class));
        String[] splitData = requestJson.split(",");
        msg = JSONObject.parseObject(String.valueOf(msg));

        //System.out.println("==============================================");
        //
        //
        //byte[] decode = java.util.Base64.getMimeDecoder().decode("VTZEAQEBAQAAD8aiEALDAAIBAAAPxqIQA1sAAwEAAA/GohACYAAEAQAAD8aiEANnAAUBAAAPxqIQAXkABgEAAA/GohADTQB2");
        //String commandData = com.td.util.HexToBinaryUtils.bytesToHexString(decode);
        //System.out.println(commandData);
        //String checkCode = HexToBinaryUtils.getCheckCode(commandData.substring(0, commandData.length() - 2)); //
        //String code = commandData.substring(commandData.length() - 2, commandData.length()); // check code
        //
        //
        //String dataLen = commandData.substring(4, 6); // the length of data
        //String newBack = commandData.substring(8, 10); // return event
        //Integer dataL = HexToBinaryUtils.getDecimal(dataLen);
        //int posLen = dataL - 2; // the length of warehouse information
        //int l = 11; //
        //if (posLen % l == 0 && posLen / l > 0) {
        //    int posNum = posLen / 11; // how many warehouse
        //    log.info("====warehouses: " + posNum + " ;the length of data " + dataLen + "->" + dataL);
        //    String subAllPos = commandData.substring(10, commandData.length() - 2); //  the length of warehouse information
        //    System.out.println("subAllPos=" + subAllPos);
        //}
    }

    /**
     * 添加从机信息、充电宝信息
     * @param uuid 设备UUID
     * @param type 设备口机
     */
    private void addMachinePowerBankInfo(String uuid, String type) {
        if (type.equals("6")) {
            addPowerBankDefault(uuid, 6);
        } else if (type.equals("9")) {
            addPowerBankDefault(uuid, 9);
        } else if (type.equals("12")) {
            addPowerBankDefault(uuid, 12);
        } else if (type.equals("24")) {
            addPowerBankDefault(uuid, 24);
        } else if (type.equals("36")) {
            addPowerBankDefault(uuid, 36);
        } else if (type.equals("48")) {
            addPowerBankDefault(uuid, 48);
        } else {
            addPowerBank(uuid, Integer.valueOf(type));
        }
    }

    private void addPowerBankDefault(String uuid, Integer deviceModel) {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Powerbank powerBank = new Powerbank();
        powerBank.setDeviceUuid(uuid);
        List<Powerbank> powerBanks = powerbankMapper.select(powerBank);
        powerBank.setPowerNo("C000");
        powerBank.setCreatedTime(new Date((System.currentTimeMillis())));
        if (null == powerBanks || powerBanks.size() < 1) {
            addPowerBanks(deviceModel, powerBank);//添加充电宝
        } else {
            for (Powerbank p : powerBanks) {//删除已存在充电宝
                powerbankMapper.deleteByPrimaryKey(p.getId());
            }
            addPowerBanks(deviceModel, powerBank);//添加充电宝
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void addPowerBanks(Integer deviceModel, Powerbank powerBank) {
        switch (deviceModel) {
            case 6:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 1, 2, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 1, 3, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 1, 4, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 1, 5, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 1, 6, 6);
                break;
            case 9:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 1, 2, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 1, 3, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 1, 4, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 1, 5, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 1, 6, 6);
                setAddPowerBankInfo(powerBank, "01", 7, 1, 7, 7);
                setAddPowerBankInfo(powerBank, "01", 8, 1, 8, 8);
                setAddPowerBankInfo(powerBank, "01", 9, 1, 9, 9);
                break;
            case 12:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 2, 1, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 3, 1, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 4, 1, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 5, 1, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 6, 1, 6);
                setAddPowerBankInfo(powerBank, "02", 1, 1, 2, 7);
                setAddPowerBankInfo(powerBank, "02", 2, 2, 2, 8);
                setAddPowerBankInfo(powerBank, "02", 3, 3, 2, 9);
                setAddPowerBankInfo(powerBank, "02", 4, 4, 2, 10);
                setAddPowerBankInfo(powerBank, "02", 5, 5, 2, 11);
                setAddPowerBankInfo(powerBank, "02", 6, 6, 2, 12);
                break;
            case 24:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 2, 1, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 3, 1, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 4, 1, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 5, 1, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 6, 1, 6);
                setAddPowerBankInfo(powerBank, "02", 1, 7, 1, 7);
                setAddPowerBankInfo(powerBank, "02", 2, 8, 1, 8);
                setAddPowerBankInfo(powerBank, "02", 3, 9, 1, 9);
                setAddPowerBankInfo(powerBank, "02", 4, 10, 1, 10);
                setAddPowerBankInfo(powerBank, "02", 5, 11, 1, 11);
                setAddPowerBankInfo(powerBank, "02", 6, 12, 1, 12);
                setAddPowerBankInfo(powerBank, "03", 1, 1, 2, 13);
                setAddPowerBankInfo(powerBank, "03", 2, 2, 2, 14);
                setAddPowerBankInfo(powerBank, "03", 3, 3, 2, 15);
                setAddPowerBankInfo(powerBank, "03", 4, 4, 2, 16);
                setAddPowerBankInfo(powerBank, "03", 5, 5, 2, 17);
                setAddPowerBankInfo(powerBank, "03", 6, 6, 2, 18);
                setAddPowerBankInfo(powerBank, "04", 1, 7, 2, 19);
                setAddPowerBankInfo(powerBank, "04", 2, 8, 2, 20);
                setAddPowerBankInfo(powerBank, "04", 3, 9, 2, 21);
                setAddPowerBankInfo(powerBank, "04", 4, 10, 2, 22);
                setAddPowerBankInfo(powerBank, "04", 5, 11, 2, 23);
                setAddPowerBankInfo(powerBank, "04", 6, 12, 2, 24);
                break;
            case 36:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 2, 1, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 3, 1, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 4, 1, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 5, 1, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 6, 1, 6);
                setAddPowerBankInfo(powerBank, "02", 1, 7, 1, 7);
                setAddPowerBankInfo(powerBank, "02", 2, 8, 1, 8);
                setAddPowerBankInfo(powerBank, "02", 3, 9, 1, 9);
                setAddPowerBankInfo(powerBank, "02", 4, 10, 1, 10);
                setAddPowerBankInfo(powerBank, "02", 5, 11, 1, 11);
                setAddPowerBankInfo(powerBank, "02", 6, 12, 1, 12);
                setAddPowerBankInfo(powerBank, "03", 1, 13, 1, 13);
                setAddPowerBankInfo(powerBank, "03", 2, 14, 1, 14);
                setAddPowerBankInfo(powerBank, "03", 3, 15, 1, 15);
                setAddPowerBankInfo(powerBank, "03", 4, 16, 1, 16);
                setAddPowerBankInfo(powerBank, "03", 5, 17, 1, 17);
                setAddPowerBankInfo(powerBank, "03", 6, 18, 1, 18);
                setAddPowerBankInfo(powerBank, "04", 1, 1, 2, 19);
                setAddPowerBankInfo(powerBank, "04", 2, 2, 2, 20);
                setAddPowerBankInfo(powerBank, "04", 3, 3, 2, 21);
                setAddPowerBankInfo(powerBank, "04", 4, 4, 2, 22);
                setAddPowerBankInfo(powerBank, "04", 5, 5, 2, 23);
                setAddPowerBankInfo(powerBank, "04", 6, 6, 2, 24);
                setAddPowerBankInfo(powerBank, "05", 1, 7, 2, 25);
                setAddPowerBankInfo(powerBank, "05", 2, 8, 2, 26);
                setAddPowerBankInfo(powerBank, "05", 3, 9, 2, 27);
                setAddPowerBankInfo(powerBank, "05", 4, 10, 2, 28);
                setAddPowerBankInfo(powerBank, "05", 5, 11, 2, 29);
                setAddPowerBankInfo(powerBank, "05", 6, 12, 2, 30);
                setAddPowerBankInfo(powerBank, "06", 1, 13, 2, 31);
                setAddPowerBankInfo(powerBank, "06", 2, 14, 2, 32);
                setAddPowerBankInfo(powerBank, "06", 3, 15, 2, 33);
                setAddPowerBankInfo(powerBank, "06", 4, 16, 2, 34);
                setAddPowerBankInfo(powerBank, "06", 5, 17, 2, 35);
                setAddPowerBankInfo(powerBank, "06", 6, 18, 2, 36);
                break;
            case 48:
                setAddPowerBankInfo(powerBank, "01", 1, 1, 1, 1);
                setAddPowerBankInfo(powerBank, "01", 2, 2, 1, 2);
                setAddPowerBankInfo(powerBank, "01", 3, 3, 1, 3);
                setAddPowerBankInfo(powerBank, "01", 4, 4, 1, 4);
                setAddPowerBankInfo(powerBank, "01", 5, 5, 1, 5);
                setAddPowerBankInfo(powerBank, "01", 6, 6, 1, 6);
                setAddPowerBankInfo(powerBank, "02", 1, 7, 1, 7);
                setAddPowerBankInfo(powerBank, "02", 2, 8, 1, 8);
                setAddPowerBankInfo(powerBank, "02", 3, 9, 1, 9);
                setAddPowerBankInfo(powerBank, "02", 4, 10, 1, 10);
                setAddPowerBankInfo(powerBank, "02", 5, 11, 1, 11);
                setAddPowerBankInfo(powerBank, "02", 6, 12, 1, 12);
                setAddPowerBankInfo(powerBank, "03", 1, 1,  2, 13);
                setAddPowerBankInfo(powerBank, "03", 2, 2,  2, 14);
                setAddPowerBankInfo(powerBank, "03", 3, 3,  2, 15);
                setAddPowerBankInfo(powerBank, "03", 4, 4,  2, 16);
                setAddPowerBankInfo(powerBank, "03", 5, 5,  2, 17);
                setAddPowerBankInfo(powerBank, "03", 6, 6,  2, 18);
                setAddPowerBankInfo(powerBank, "04", 1, 7, 2, 19);
                setAddPowerBankInfo(powerBank, "04", 2, 8, 2, 20);
                setAddPowerBankInfo(powerBank, "04", 3, 9, 2, 21);
                setAddPowerBankInfo(powerBank, "04", 4, 10,2, 22);
                setAddPowerBankInfo(powerBank, "04", 5, 11,2, 23);
                setAddPowerBankInfo(powerBank, "04", 6, 12,2, 24);
                setAddPowerBankInfo(powerBank, "05", 1, 1, 3, 25);
                setAddPowerBankInfo(powerBank, "05", 2, 2, 3, 26);
                setAddPowerBankInfo(powerBank, "05", 3, 3, 3, 27);
                setAddPowerBankInfo(powerBank, "05", 4, 4,  3, 28);
                setAddPowerBankInfo(powerBank, "05", 5, 5,  3, 29);
                setAddPowerBankInfo(powerBank, "05", 6, 6,  3, 30);
                setAddPowerBankInfo(powerBank, "06", 1, 7,  3, 31);
                setAddPowerBankInfo(powerBank, "06", 2, 8,  3, 32);
                setAddPowerBankInfo(powerBank, "06", 3, 9,  3, 33);
                setAddPowerBankInfo(powerBank, "06", 4, 10, 3, 34);
                setAddPowerBankInfo(powerBank, "06", 5, 11, 3, 35);
                setAddPowerBankInfo(powerBank, "06", 6, 12, 3, 36);
                setAddPowerBankInfo(powerBank, "07", 1, 1, 4, 37);
                setAddPowerBankInfo(powerBank, "07", 2, 2, 4, 38);
                setAddPowerBankInfo(powerBank, "07", 3, 3, 4, 39);
                setAddPowerBankInfo(powerBank, "07", 4, 4, 4, 40);
                setAddPowerBankInfo(powerBank, "07", 5, 5, 4, 41);
                setAddPowerBankInfo(powerBank, "07", 6, 6, 4, 42);
                setAddPowerBankInfo(powerBank, "08", 1, 7, 4, 43);
                setAddPowerBankInfo(powerBank, "08", 2, 8, 4, 44);
                setAddPowerBankInfo(powerBank, "08", 3, 9, 4, 45);
                setAddPowerBankInfo(powerBank, "08", 4, 10, 4, 46);
                setAddPowerBankInfo(powerBank, "08", 5, 11, 4, 47);
                setAddPowerBankInfo(powerBank, "08", 6, 12, 4, 48);
                break;
        }
    }

    private void setAddPowerBankInfo(Powerbank powerBank, String machineId, Integer posId, Integer row, Integer col, Integer uuild) {
        powerBank.setAllPositionUuidRow(row);
        powerBank.setAllPositionUuidCol(col);
        powerBank.setAllPositionUuild(uuild);
        powerBank.setPositionUuid("" + posId);
        powerBank.setMachineUuid(machineId);
        powerbankMapper.insertSelective(powerBank);
    }

    private void addPowerBank(String uuid, Integer deviceModel) {
        Powerbank powerBank = new Powerbank();
        powerBank.setDeviceUuid(uuid);
        List<Powerbank> powerBanks = powerbankMapper.select(powerBank);
        powerBank.setPowerNo("C000");
        powerBank.setState(1);
        powerBank.setCreatedTime(new Date((System.currentTimeMillis())));
        if (null == powerBanks || powerBanks.size() < 1) {
            addPowerBanksByOtherType(deviceModel, powerBank);//添加充电宝
        } else {
            for (Powerbank p : powerBanks) {//删除已存在充电宝
                powerbankMapper.deleteByPrimaryKey(p.getId());
            }
            addPowerBanksByOtherType(deviceModel, powerBank);//添加充电宝
        }

    }
    /**
     * 添加其他机型的数据
     * @param deviceModel
     * @param powerBank
     */
    private void addPowerBanksByOtherType(Integer deviceModel, Powerbank powerBank){
        int size = deviceModel ;
        if (deviceModel==9){
            for (int i=1;i<size+1;i++){
                powerBank.setAllPositionUuidRow(1);
                powerBank.setAllPositionUuidCol(i);
                powerBank.setAllPositionUuild(i);
                powerBank.setPositionUuid("" + i);
                powerBank.setMachineUuid("01");
                System.out.println(powerBank);
                powerbankMapper.insertSelective(powerBank);
            }
        }else {
            for (int i=1;i<size+1;i++){
                int posId = i % 6;
                int machineId = i/6;
                if (posId==0){
                    posId = 6;
                }else {
                    machineId = machineId +1;
                }
                String machine = HexToBinaryUtils.getHex(machineId+"");
                powerBank.setAllPositionUuidRow(1);
                powerBank.setAllPositionUuidCol(1);
                powerBank.setAllPositionUuild(i);
                powerBank.setPositionUuid("" + posId);
                powerBank.setMachineUuid(machine);
                System.out.println(powerBank);
                powerbankMapper.insertSelective(powerBank);
            }
        }

    }

    private String getPowerPosition(String positionUuid, String machineUuid) {
//        log.info("positionUuid:"+positionUuid+",machineUuid:"+machineUuid);
        return String.valueOf(Integer.valueOf(positionUuid) + (Integer.valueOf(machineUuid)-1)*6);
    }

}
