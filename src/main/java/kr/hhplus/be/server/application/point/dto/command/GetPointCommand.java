package kr.hhplus.be.server.application.point.dto.command;

import lombok.Getter;

@Getter
public class GetPointCommand {

    private Long userId;

    public GetPointCommand(Long userId) {
        this.userId = userId;
    }

    public static GetPointCommand of(Long userId) {
        return new GetPointCommand(userId);
    }
}
