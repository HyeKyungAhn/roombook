package store.roombook.domain;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "Builder")
public class MyBookDto {
    private List<SpaceBookDto> bookList;
    private Boolean hasNext;
}
