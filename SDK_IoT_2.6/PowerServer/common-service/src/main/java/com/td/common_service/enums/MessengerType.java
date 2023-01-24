package com.td.common_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessengerType {
    VK(0), TELEGRAM(1);

    private final int type;
}
