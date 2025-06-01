package kr.hhplus.be.server.support.event;

import lombok.Getter;

@Getter
public enum EventStatus {

    INIT,
    SEND_SUCCESS,
    SEND_FAIL;
}
