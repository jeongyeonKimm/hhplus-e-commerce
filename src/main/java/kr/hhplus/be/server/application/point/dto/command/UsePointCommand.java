package kr.hhplus.be.server.application.point.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UsePointCommand {

    private Long userId;
    private Integer useAmount;

    @Builder
    public UsePointCommand(Long userId, Integer useAmount) {
        this.userId = userId;
        this.useAmount = useAmount;
    }
}
