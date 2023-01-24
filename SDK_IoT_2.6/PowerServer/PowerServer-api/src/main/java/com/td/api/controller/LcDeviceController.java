package com.td.api.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.common_service.mapper.*;
import com.td.common_service.model.*;
import com.td.common_service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : yangchangsong
 * @version : V1.0
 * @since : 2019/07/01
 */
@Slf4j
@RestController
@RequestMapping("lc_device")
@Api(value = "LcDeviceController", description = "lc_device information and control")
//@SecurityRequirement(name = "javainuseapi")
public class LcDeviceController {

    private static int MIN_DEPOSIT = 200;

    @Autowired
    private PowerbankMapper powerbankMapper;
    @Autowired
    private PowerbankInfoMapper powerbankInfoMapper;
    @Autowired
    private OrderRentPayMapper rentPayMapper;
    @Autowired
    private ServiceUserMapper userMapper;
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private PlaceInfoMapper placeInfoMapper;
    @Autowired
    private ReferralInfoMapper referralInfoMapper;

    @Autowired
    private AccountService accountService;
    @Autowired
    private PlaceInfoService placeInfoService;
    @Autowired
    private QiwiPaymentsService qiwiPaymentsService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private OrderPayService orderPayService;

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/refPb")
    R refPb(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "target_user_token") int targetUserToken) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        R check = checkUser(user);
        if (check.getCode() == 201) {
            return check;
        }

        DeviceInfo deviceParam = new DeviceInfo();
        deviceParam.setDeviceNo("0");
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(deviceParam);
        if (deviceInfo == null) {
            return RUtil.error(201, "err", "Такой станции нет");
        }
        if (deviceInfo.getDeviceState() == 0) {
            return RUtil.error(201, "err", "Станция выключена и недоступна");
        }
        String userId = String.valueOf(user.getId());
        R r = deviceService.startRent(userId, deviceInfo.getDeviceUuid(), "", "0");
        if (r.getCode() != 200) {
            return RUtil.error(201, "err", r.getMsg());
        }
        log.info("startRent====param:userId :{},==== uuid:{}", userId, deviceInfo.getDeviceUuid());


        if (user.getMessengerLevel() == 0) {
            user.setMessengerLevel(1);
            userMapper.updateByPrimaryKey(user);
            return RUtil.success("Вы получили повербанк на условиях 60 руб/час и 100 руб/день!\n" +
                    "Сумма будет списана после возвращения повербанка\n\n" +
                    "Чтобы вернуть повербанк, опускайте его в станцию контактами вниз до одного громкого щелчка");
        }
        return RUtil.success("Вы получили повербанк на условиях 60 руб/час и 100 руб/день!");
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/showStations")
    R showStations(@RequestParam(name = "lc_id") int lcId) {
        DeviceInfo deviceParam = new DeviceInfo();
        //deviceParam.setDeviceState(1);
        String result = "На карте: https://vk.com/im?sel=-214203795&w=address-214203795\n\n";
        result += deviceInfoMapper.select(deviceParam).stream()
                .map(deviceInfo -> {
                    Optional<String> address = Optional.ofNullable(placeInfoMapper.getAddress(deviceInfo.getPlaceUuid()));
                    if (address.orElse("").equals("")) return "";
                    return "[#" + deviceInfo.getDeviceNo() + "] "
                            + (deviceInfo.getDeviceState() == 1 ? "включена, повербанки: "
                            + deviceService.getPowerbanks(deviceInfo.getDeviceUuid())
                            .stream()
                            .filter(powerbank -> !Objects.equals(powerbank.getPowerNo(), "C000"))
                            .map(Powerbank::getState)
                            .filter(state -> state == 0)
                            .count()
                            + "/" + deviceInfo.getSpaceNu() : "выключена") + address.map(s -> ", " + s).orElse("");
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(";\n"));
        return RUtil.success(result);
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/setAddress")
    R setAddress(@RequestParam(name = "station_no") String stationNo, @RequestParam(name = "address") String address) {
        DeviceInfo deviceParam = new DeviceInfo();
        deviceParam.setDeviceNo(stationNo);
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(deviceParam);
        if (deviceInfo == null) {
            return RUtil.error(201, "err", "Такой станции нет");
        }

        PlaceInfo placeInfoParam = new PlaceInfo();
        placeInfoParam.setPlaceUuid(deviceInfo.getPlaceUuid());
        PlaceInfo placeInfo = placeInfoMapper.selectOne(placeInfoParam);
        if (placeInfo == null) {
            return RUtil.error(201, "err", "Ошибка изначального заполнения");
        }
        placeInfo.setPlaceName(address);
        placeInfoMapper.updateByPrimaryKey(placeInfo);
        return RUtil.success("true");
    }

    @PostConstruct
    private void putStationPlaces() {
        List<DeviceInfo> list = deviceInfoMapper.selectAll();
        for (DeviceInfo deviceInfo : list) {
            if (deviceInfo.getPlaceUuid() == null) {
                String uuid = UUID.randomUUID().toString();
                deviceInfo.setPlaceUuid(uuid);
                PlaceInfo placeInfo = new PlaceInfo();
                placeInfo.setPlaceUuid(uuid);
                placeInfo.setPlaceName("");
                placeInfoMapper.insert(placeInfo);
                deviceInfoMapper.updateByDeviceUuidSelective(deviceInfo);
            }
        }

    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/showProfile")
    R showProfile(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "ref") String ref) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        OrderRentPay orderParam = new OrderRentPay();
        orderParam.setUserId(user.getId());
        orderParam.setOrderState(0);
        List<OrderRentPay> rentPays = rentPayMapper.select(orderParam);
        int currentMinDeposit = user.getMinDeposit() + MIN_DEPOSIT;

        ServiceUser referralUserParam = new ServiceUser();
        referralUserParam.setToken(ref);
        Optional<ServiceUser> serviceUserOptional = Optional.ofNullable(userMapper.selectOne(referralUserParam));
        serviceUserOptional.filter(referralUser -> !referralUser.getId().equals(user.getId()))
                .filter(referralUser -> referralUser.getMessengerLevel() <= 0).filter(referralUser -> {
                    ReferralInfo referralInfoParam = new ReferralInfo();
                    referralInfoParam.setReferralUserId(referralUser.getId());
                    return !Optional.ofNullable(referralInfoMapper.selectOne(referralInfoParam)).isPresent();
                }).filter(referralUser -> {//Was this promoter attracted by this referral?
                    ReferralInfo referralInfoParam = new ReferralInfo();
                    referralInfoParam.setReferralUserId(user.getId());
                    Optional<ReferralInfo> referralInfoOptional = Optional.ofNullable(referralInfoMapper.selectOne(referralInfoParam));
                    return !referralInfoOptional.map(ReferralInfo::getPromoterUserId).filter(id -> id.equals(referralUser.getId())).isPresent();
                }).ifPresent(referralUser -> {
                    ReferralInfo referralInfo = new ReferralInfo();
                    referralInfo.setReferralUserId(referralUser.getId());
                    referralInfo.setPromoterUserId(user.getId());
                    referralInfo.setCreatedTime(new Date(System.currentTimeMillis()));
                    referralInfoMapper.insert(referralInfo);

                    user.setMoney(user.getMoney() + 100);
                    userMapper.updateByPrimaryKey(user);
                    referralUser.setMoney(referralUser.getMoney() + 100);
                    userMapper.updateByPrimaryKey(referralUser);
                });
        ReferralInfo referralInfoParam = new ReferralInfo();
        referralInfoParam.setPromoterUserId(user.getId());
        List<ReferralInfo> referralInfoList = referralInfoMapper.select(referralInfoParam);

        return RUtil.success("Вы перешли в профиль. Ваш id: " + lcId + "_" + user.getId() + "\n"
                + "Вы превлекли пользователей: " + referralInfoList.size() + ". "
                + "Приведи друга - 100 рублей каждому! Подробнее - vk.cc/cfIXtG\n"
                + "Баланс аккаунта: " + user.getMoney() + "\n"
                + "Минимальный баланс для взятия повербанка: " + currentMinDeposit + "\n"
                + (rentPays.size() > 0 ? "\nВаши повербанки:\n" : "")
                + rentPays.stream()
                .map(rentPay -> "-PowerBank: "
                        + rentPay.getCreateTime()
                        + ", Будущий платёж: " + orderPayService.deductMoney(rentPay, user.getMessengerLevel() == 0)
                        + ";")
                .collect(Collectors.joining("\n")));
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/getPaymentFormUrl")
    R getPaymentFormUrl(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "value") int value) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        String paymentUrl = qiwiPaymentsService.createPaymentFormIfEmpty(user.getId(), value, "https://qiwi.com");
        return RUtil.success(paymentUrl);
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/checkAddress")
    R checkAddress(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "station_no") String stationNo) {
        DeviceInfo deviceParam = new DeviceInfo();
        deviceParam.setDeviceNo(stationNo);
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(deviceParam);
        if (deviceInfo == null) {
            return RUtil.error(201, "err", "Такой станции нет");
        }
        if (deviceInfo.getDeviceState() != 1) {
            return RUtil.error(201, "err", "Станция выключена и недоступна");
        }
        String address = placeInfoMapper.getAddress(deviceInfo.getPlaceUuid());
        if (address == null || address.equals("")) {
            log.warn(deviceInfo.getDeviceUuid() + " has no address param!");
            return RUtil.success("Отлично, можем продолжать");
        }

        return RUtil.success("Вы получите повербанк по адресу:\n" + address);
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping(path = "getTokenQr/*.png", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage generateQRCodeImage(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "station_no") String stationNo, @RequestParam(name = "platform") String platform) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        ServiceUser user = accountService.createUserIfEmpty(lcId);//https://vk.com/write-214203795?ref_source=promo_rotx
        BitMatrix bitMatrix = qrCodeWriter.encode("vk.com/write-214203795?ref=" + user.getToken(), BarcodeFormat.QR_CODE, 250, 250);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/preliminaryCheck")
    R preliminaryCheck(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "station_no") String stationNo) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);

        DeviceInfo deviceParam = new DeviceInfo();
        deviceParam.setDeviceNo(stationNo);
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(deviceParam);
        if (deviceInfo == null) {
            return RUtil.error(201, "err", "Такой станции нет");
        }
        if (deviceInfo.getDeviceState() != 1) {
            return RUtil.error(201, "err", "Станция выключена и недоступна");
        }
        Map<Integer, Integer> powerbankDonateLevels = new HashMap<>();
        PowerbankInfo powerbankInfoParam = new PowerbankInfo();
        powerbankMapper.selectPowerbankByDeviceUuid(deviceInfo.getDeviceUuid())
                .stream()
                .filter(powerbank -> powerbank.getState() == 0)
                .forEach(powerbank -> {
                    powerbankInfoParam.setPowerNo(powerbank.getPowerNo());
                    Optional<PowerbankInfo> powerbankInfoOptional = Optional.ofNullable(powerbankInfoMapper.selectOne(powerbankInfoParam));
                    int lvl = powerbankInfoOptional.map(PowerbankInfo::getDonateLevel).orElse(1);
                    powerbankDonateLevels.put(lvl,
                            powerbankDonateLevels.getOrDefault(lvl, 0) + 1);
                });

        int currentMinDeposit = Math.max(user.getMinDeposit() + MIN_DEPOSIT
                - (user.getMessengerLevel() == 0 & powerbankDonateLevels.containsKey(0) ? 100 : 0), 0);
        if (user.getMoney() < currentMinDeposit) {
            int needMoney = currentMinDeposit - user.getMoney();
            String paymentUrl = qiwiPaymentsService.createPaymentFormIfEmpty(user.getId(), needMoney, "https://qiwi.com");
            return RUtil.error(201, "err", "Первые сутки аренды будут бесплатными!\n\n" +
                    "Для старта баланс должен быть не менее " + currentMinDeposit + " руб. \n" +
                    "Ваш текущий баланс: " + user.getMoney() + ". \n\n" +
                    "Пополнить счёт на " + needMoney + " можно по ссылке:\n" + paymentUrl);
            //todo упростить
        }

        OrderRentPay orderParam = new OrderRentPay();
        orderParam.setUserId(user.getId());
        orderParam.setOrderState(0);
        List<OrderRentPay> rentPay = rentPayMapper.select(orderParam);
        if (rentPay.size() >= 1) {
            return RUtil.error(201, "err", "Вы превысили лимит на единовременную аренду повербанков! Ваш лимит: 1");
        }
        return RUtil.success("good");
    }

    private R checkUser(ServiceUser user) {
        int currentMinDeposit = user.getMinDeposit() + MIN_DEPOSIT;
        if (user.getMoney() < currentMinDeposit) {
            int needMoney = currentMinDeposit - user.getMoney();
            String paymentUrl = qiwiPaymentsService.createPaymentFormIfEmpty(user.getId(), needMoney, "https://qiwi.com");
            return RUtil.error(201, "err", "Первые сутки аренды будут бесплатными!\n\n" +
                    "Для старта баланс должен быть не менее " + currentMinDeposit + " руб. \n" +
                    "Ваш текущий баланс: " + user.getMoney() + ". \n\n" +
                    "Пополнить счёт на " + needMoney + " можно по ссылке:\n" + paymentUrl);
        }
        return RUtil.success("good");
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/getPbCallback")
    R getPbCallback(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "station_no") String stationNo) {
        R check = preliminaryCheck(lcId, stationNo);
        if (check.getCode() == 201) {
            return check;
        }

        DeviceInfo deviceParam = new DeviceInfo();
        deviceParam.setDeviceNo(stationNo);
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(deviceParam);
        if (deviceInfo == null) {
            return RUtil.error(201, "err", "Такой станции нет");
        }
        if (deviceInfo.getDeviceState() == 0) {
            return RUtil.error(201, "err", "Станция выключена и недоступна");
        }
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        String userId = String.valueOf(user.getId());
        String positionUuid = "";
        Map<Integer, List<Powerbank>> powerbankDonateLevels = new HashMap<>();
        PowerbankInfo powerbankInfoParam = new PowerbankInfo();
        powerbankMapper.selectPowerbankByDeviceUuid(deviceInfo.getDeviceUuid())
                .stream()
                .filter(powerbank -> powerbank.getState() == 0)
                .forEach(powerbank -> {
                    powerbankInfoParam.setPowerNo(powerbank.getPowerNo());
                    Optional<PowerbankInfo> powerbankInfoOptional = Optional.ofNullable(powerbankInfoMapper.selectOne(powerbankInfoParam));
                    int lvl = powerbankInfoOptional.map(PowerbankInfo::getDonateLevel).orElse(1);
                    if (!powerbankDonateLevels.containsKey(lvl)) powerbankDonateLevels.put(lvl, new ArrayList<>());
                    powerbankDonateLevels.get(lvl).add(powerbank);
                });
        if (user.getMessengerLevel() == 0) {
            positionUuid = Optional.ofNullable(powerbankDonateLevels.get(0))
                    .orElse(Optional.ofNullable(powerbankDonateLevels.get(1))
                            .orElse(Collections.emptyList()))
                    .stream().max(Comparator.comparingInt(Powerbank::getPowerAd)).map(Powerbank::getPositionUuid).orElse("");
        } else {
            positionUuid = Optional.ofNullable(powerbankDonateLevels.get(1))
                    .orElse(Optional.ofNullable(powerbankDonateLevels.get(0)).orElse(Collections.emptyList()))
                    .stream().max(Comparator.comparingInt(Powerbank::getPowerAd)).map(Powerbank::getPositionUuid).orElse("");
        }
        R r = deviceService.startRent(userId, deviceInfo.getDeviceUuid(), positionUuid, "0");
        if (r.getCode() != 200) {
            return RUtil.error(201, "err", r.getMsg());
        }
        log.info("startRent====param:userId :{},==== uuid:{}", userId, deviceInfo.getDeviceUuid());


        if (user.getMessengerLevel() == 0) {
            user.setMessengerLevel(1);
            userMapper.updateByPrimaryKey(user);
            return RUtil.success("Вы получили повербанк на условиях 60 руб/час и 100 руб/день!\n" +
                    "Сумма будет списана после возвращения повербанка\n\n" +
                    "Чтобы вернуть повербанк, опускайте его в станцию контактами вниз до одного громкого щелчка");
        }
        return RUtil.success("Вы получили повербанк на условиях 60 руб/час и 100 руб/день!");
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/getLevel")
    R getLevel(@RequestParam(name = "lc_id") int lcId, int clientType) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        switch (clientType) {
            case 0:
                return RUtil.success(user.getMessengerLevel());
            case 1:
            case 2:
            default:
                return RUtil.error(201, "err", "-1");
        }
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/setLevel")
    R setLevel(@RequestParam(name = "lc_id") int lcId, int clientType, int level) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        switch (clientType) {
            case 0:
                user.setMessengerLevel(level);
                break;
            case 1:
            case 2:
            default:
                return RUtil.error(201, "err", "false");
        }
        userMapper.updateByPrimaryKey(user);
        return RUtil.success(true);
    }

    @ApiOperation(value = "")
    @ResponseBody
    @GetMapping("/setBalance")
    R setBalance(@RequestParam(name = "lc_id") int lcId, @RequestParam(name = "money") int money, @RequestParam(name = "min_deposit") int minDeposit) {
        ServiceUser user = accountService.createUserIfEmpty(lcId);
        user.setMoney(money);
        user.setMinDeposit(minDeposit);
        userMapper.updateByPrimaryKey(user);
        return RUtil.success(true);
    }
}
