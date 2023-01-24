package com.td.common_service.service.impl;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.BillStatus;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.PaymentInfo;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import com.qiwi.billpayments.sdk.model.out.ResponseStatus;
import com.td.common_service.mapper.QiwiOrderPayMapper;
import com.td.common_service.mapper.ServiceUserMapper;
import com.td.common_service.model.QiwiOrderPay;
import com.td.common_service.model.ServiceUser;
import com.td.common_service.service.LogService;
import com.td.common_service.service.QiwiPaymentsService;
import com.td.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class QiwiPaymentsServiceImpl implements QiwiPaymentsService {

    private static final Logger log = LoggerFactory.getLogger(QiwiPaymentsServiceImpl.class);

    @Autowired
    private LogService logService;

    @Autowired
    private QiwiOrderPayMapper qiwiMapper;
    @Autowired
    private ServiceUserMapper userMapper;

    private ExecutorService executorService;

    private static final String PUBLIC_KEY = "";
    private static final String SECRET_KEY = "";

    BillPaymentClient client;

    @PostConstruct
    public void qiwi() {
        client = BillPaymentClientFactory.createDefault(SECRET_KEY);
    }

    public void qiwiPaymentHandler() {
        QiwiOrderPay qiwiOrderPayP = new QiwiOrderPay();
        qiwiOrderPayP.setState(0);
        List<QiwiOrderPay> l = qiwiMapper.select(qiwiOrderPayP);
        log.info("list: " + l.stream().map(QiwiOrderPay::toString).collect(Collectors.joining(",")));
        executorService = ThreadPoolServiceImpl.getInstance();
        client = BillPaymentClientFactory.createDefault(SECRET_KEY);
        executorService.execute(() -> {
            while (true) {
                try {
                    List<QiwiOrderPay> qiwiOrderPayList = qiwiMapper.selectByState(0);
                    log.info("qiwiOrderPayList start " + qiwiOrderPayList.size());
                    for (QiwiOrderPay qiwiOrderPay : qiwiOrderPayList) {
                        BillResponse response = null;
                        try {
                            response = CompletableFuture
                                    .supplyAsync(() -> client.getBillInfo(qiwiOrderPay.getBillId()))
                                    .get(10, TimeUnit.SECONDS);//todo
                        } catch (Exception ignored) {
                            //log.info("response: "+ignored);
                        }
                        qiwiOrderPay.setUpdateTime(new Date(System.currentTimeMillis()));
                        switch (Optional.ofNullable(response).map(BillResponse::getStatus).map(ResponseStatus::getValue).map(BillStatus::getValue).orElse("WAITING")) {
                            case "PAID":
                                qiwiOrderPay.setState(1);
                                if (response.getAmount().getCurrency().getDisplayName().equals("Russian Ruble"))
                                    qiwiOrderPay.setAmount(response.getAmount().getValue().intValue());
                                else qiwiOrderPay.setAmount(0);
                                qiwiOrderPay.setCloseTime(new Date(System.currentTimeMillis()));

                                ServiceUser userParam = new ServiceUser();
                                userParam.setId(qiwiOrderPay.getServiceUserId());
                                Optional.ofNullable(userMapper.selectByPrimaryKey(userParam)).ifPresent(serviceUser -> {
                                    log.info("#1 add money user " + serviceUser.getId() + "; " + serviceUser.getMoney() + "; " + qiwiOrderPay.getAmount());
                                    serviceUser.setMoney(serviceUser.getMoney() + qiwiOrderPay.getAmount());
                                    log.info("#2 add money user " + serviceUser.getId() + "; " + serviceUser.getMoney() + "; " + qiwiOrderPay.getAmount());
                                    userMapper.updateByPrimaryKey(serviceUser);
                                });
                                break;
                            case "EXPIRED":
                                qiwiOrderPay.setState(2);
                                break;
                            case "WAITING":
                                if (System.currentTimeMillis() - qiwiOrderPay.getCreateTime().getTime() > 1000 * 60 * 60 * 24 * 3/*24h*3=72h*/) {
                                    qiwiOrderPay.setState(2);
                                }
                                break;
                        }
                        qiwiMapper.updateByPrimaryKey(qiwiOrderPay);
                        Thread.sleep(100);
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    private void OLD_qiwiPaymentHandler() {
        executorService = ThreadPoolServiceImpl.getInstance();
        client = BillPaymentClientFactory.createDefault(SECRET_KEY);
        executorService.execute(() -> {
            while (true) {
                try {
                    List<QiwiOrderPay> qiwiOrderPayList = qiwiMapper.selectByState(0);
                    for (QiwiOrderPay qiwiOrderPay : qiwiOrderPayList) {
                        BillResponse response = null;
                        try {
                            response = client.getBillInfo(qiwiOrderPay.getBillId());
                        } catch (Exception ignored) {
                            //log.info("response: "+ignored);
                        }
                        qiwiOrderPay.setUpdateTime(new Date(System.currentTimeMillis()));
                        switch (Optional.ofNullable(response).map(BillResponse::getStatus).map(ResponseStatus::getValue).map(BillStatus::getValue).orElse("WAIT")) {
                            case "PAID":
                                qiwiOrderPay.setState(1);
                                if (response.getAmount().getCurrency().getDisplayName().equals("Russian Ruble"))
                                    qiwiOrderPay.setAmount(response.getAmount().getValue().intValue());
                                else qiwiOrderPay.setAmount(0);
                                qiwiOrderPay.setCloseTime(new Date(System.currentTimeMillis()));

                                ServiceUser userParam = new ServiceUser();
                                userParam.setId(qiwiOrderPay.getServiceUserId());
                                Optional.ofNullable(userMapper.selectByPrimaryKey(userParam)).ifPresent(serviceUser -> {
                                    log.info("#1 add money user " + serviceUser.getId() + "; " + serviceUser.getMoney() + "; " + qiwiOrderPay.getAmount());
                                    serviceUser.setMoney(serviceUser.getMoney() + qiwiOrderPay.getAmount());
                                    log.info("#2 add money user " + serviceUser.getId() + "; " + serviceUser.getMoney() + "; " + qiwiOrderPay.getAmount());
                                    userMapper.updateByPrimaryKey(serviceUser);
                                });
                                break;
                            case "EXPIRED":
                                qiwiOrderPay.setState(2);
                                break;
                        }
                        qiwiMapper.updateByPrimaryKey(qiwiOrderPay);
                        Thread.sleep(100);
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    //@Override
    public String createPaymentFormIfEmptyd(int serviceUserId, int amount, String successUrl) {
        QiwiOrderPay qiwiOrderParam = new QiwiOrderPay();
        qiwiOrderParam.setServiceUserId(serviceUserId);
        qiwiOrderParam.setState(0);
        QiwiOrderPay qiwiOrderPay = qiwiMapper.select(qiwiOrderParam).stream().findFirst().orElse(null);
        if (qiwiOrderPay != null) {
            if (qiwiOrderPay.getAmount() == amount && System.currentTimeMillis() - qiwiOrderPay.getCreateTime().getTime() < 1000 * 60 * 5) {
                return qiwiOrderPay.getPaymentUrl();
            }/* else {
                qiwiOrderPay.setState(2);
                qiwiMapper.updateByPrimaryKey(qiwiOrderPay);
                try {
                    client.cancelBill(qiwiOrderParam.getBillId());
                } catch (Exception ex) {
                    log.warn(ex.toString());
                }
            }*/
        }

        String billId = UUID.randomUUID().toString();

        qiwiOrderPay = new QiwiOrderPay();
        qiwiOrderPay.setServiceUserId(serviceUserId);
        qiwiOrderPay.setBillId(billId);
        qiwiOrderPay.setAmount(Math.max(amount, 0));
        qiwiOrderPay.setState(0);
        qiwiOrderPay.setCreateTime(new Date(System.currentTimeMillis()));
        qiwiOrderPay.setUpdateTime(new Date(System.currentTimeMillis()));
        MoneyAmount moneyAmount = new MoneyAmount(
                BigDecimal.valueOf(Math.max(amount, 0)),
                Currency.getInstance("RUB")
        );
        String paymentUrl = "error";

        BillPaymentClient client = BillPaymentClientFactory.createDefault(SECRET_KEY);
        CreateBillInfo billInfo = new CreateBillInfo(
                billId,
                new MoneyAmount(
                        BigDecimal.valueOf(Math.max(amount, 0)),
                        Currency.getInstance("RUB")
                ),
                null,
                ZonedDateTime.now().plusMinutes(20),
                null,
                "https://vk.com/write-214203795"
        );
        try {
            BillResponse response = client.createBill(billInfo);
            paymentUrl = "> " + response.getPayUrl();
        } catch (Exception e) {
            //JsonUtil.convertString2Obj(e.getMessage(), ResponseData.class);
            Map<String, String> map = JsonUtil.convertString2Obj(e.getMessage().split("\'")[1], Map.class);
            if (map != null || map.containsKey("payUrl")) {
                System.out.println(map);
                paymentUrl = ">> " + map.get("payUrl");
            }
        }

        qiwiOrderPay.setPaymentUrl(paymentUrl);
        log.info("New payment: " + qiwiOrderPay.toString());
        qiwiMapper.insert(qiwiOrderPay);
        return paymentUrl;
    }

    public String createPaymentFormIfEmpty(int serviceUserId, int amount, String successUrl) {
        QiwiOrderPay qiwiOrderParam = new QiwiOrderPay();
        qiwiOrderParam.setServiceUserId(serviceUserId);
        qiwiOrderParam.setState(0);
        QiwiOrderPay qiwiOrderPay = qiwiMapper.select(qiwiOrderParam).stream().findFirst().orElse(null);
        if (qiwiOrderPay != null) {
            if (qiwiOrderPay.getAmount() == amount && System.currentTimeMillis() - qiwiOrderPay.getCreateTime().getTime() < 1000 * 10) {
                return qiwiOrderPay.getPaymentUrl();
            }/* else {
                qiwiOrderPay.setState(2);
                qiwiMapper.updateByPrimaryKey(qiwiOrderPay);
                try {
                    client.cancelBill(qiwiOrderParam.getBillId());
                } catch (Exception ex) {
                    log.warn(ex.toString());
                }
            }*/
        }

        String billId = UUID.randomUUID().toString();

        qiwiOrderPay = new QiwiOrderPay();
        qiwiOrderPay.setServiceUserId(serviceUserId);
        qiwiOrderPay.setBillId(billId);
        qiwiOrderPay.setAmount(Math.max(amount, 0));
        qiwiOrderPay.setState(0);
        qiwiOrderPay.setCreateTime(new Date(System.currentTimeMillis()));
        qiwiOrderPay.setUpdateTime(new Date(System.currentTimeMillis()));
        MoneyAmount moneyAmount = new MoneyAmount(
                BigDecimal.valueOf(Math.max(amount, 0)),
                Currency.getInstance("RUB")
        );
        String paymentUrl = client.createPaymentForm(new PaymentInfo(PUBLIC_KEY, moneyAmount, billId, successUrl));

        qiwiOrderPay.setPaymentUrl(paymentUrl);
        log.info("New payment: " + qiwiOrderPay.toString());
        qiwiMapper.insert(qiwiOrderPay);
        return paymentUrl;
    }
}
