package store.roombook.domain;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "SpaceListDto")
@Getter
@ToString
public class SpaceListDto {
    private Set<SpaceDto> spaces;
    private Set<RescDto> resources;
    private Set<FileDto> files;
    private Set<SpaceBookDto> bookings;
    private String thumbnailPath;
    private String noImgPath;
    private PageHandler pageHandler;

    private ServerState serverState;
}
