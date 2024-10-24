package store.roombook.domain;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "Builder")
public class ServerState {
    private String result;
    private String errorMessage;
}
