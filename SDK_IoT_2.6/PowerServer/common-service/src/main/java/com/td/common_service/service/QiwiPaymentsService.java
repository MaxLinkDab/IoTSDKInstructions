package com.td.common_service.service;

public interface QiwiPaymentsService {

    void qiwiPaymentHandler();

    String createPaymentFormIfEmpty(int serviceUserId, int amount, String successUrl);

}
