package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static User of() {
        return new User();
    }
}
