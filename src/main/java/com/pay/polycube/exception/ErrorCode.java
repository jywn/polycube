package com.pay.polycube.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 결제 예외 **/
    PAY_TWICE("P001", "중복 결제를 시도하였습니다."),
    NO_PRICE("P002", "결제 금액이 잘못되었습니다.");

    private final String code;
    private final String message;
}
