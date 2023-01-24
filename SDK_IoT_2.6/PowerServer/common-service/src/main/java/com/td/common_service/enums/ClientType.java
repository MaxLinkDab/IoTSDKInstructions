package com.td.common_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientType {
    MESSENGER(0),WEBSITE(1),APPLICATION(2);

    private final int type;
}
