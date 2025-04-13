package kr.hhplus.be.server.application.point.dto.command;

import lombok.Getter;

@Getter
public class UsePointCommand {

    private Long userId;
    private Integer useAmount;

    private UsePointCommand(Long userId, Integer useAmount) {
        this.userId = userId;
        this.useAmount = useAmount;
    }

    public static UsePointCommand of(long userId, int useAmount) {
        return new UsePointCommand(userId, useAmount);
    }
}
