package kr.hhplus.be.server.support.aop.lock;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LockStrategyFactory {

    private final Map<LockType, LockStrategy> strategies;

    public LockStrategyFactory(List<LockStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(LockStrategy::getLockType, strategy -> strategy));
    }

    public LockStrategy getLockStrategy(LockType type) {
        return strategies.get(type);
    }
}
