package kr.hhplus.be.server.support.aop.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockType {

    SPIN_LOCK("스핀 락"),
    PUB_SUB_LOCK("pub/sub 락");

    private final String strategy;
}
