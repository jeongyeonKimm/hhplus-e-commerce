package kr.hhplus.be.server.support.event;

public interface DomainEvent {

    Long aggregateId();

    default String eventType() {
        Class<?> clazz = this.getClass();
        if (clazz.getEnclosingClass() != null) {
            return clazz.getEnclosingClass().getSimpleName() + "." + clazz.getSimpleName();
        }
        return clazz.getSimpleName();
    }
}
