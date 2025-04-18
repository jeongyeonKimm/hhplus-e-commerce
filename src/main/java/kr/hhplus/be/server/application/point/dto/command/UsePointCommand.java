package kr.hhplus.be.server.application.point.dto.command;

import lombok.Getter;

@Getter
public class UsePointCommand {

    private Long userId;
    private Long useAmount;

    private UsePointCommand(Long userId, Long useAmount) {
        this.userId = userId;
        this.useAmount = useAmount;
    }

    public static UsePointCommand of(Long userId, Long useAmount) {
        return new UsePointCommand(userId, useAmount);
    }
}
