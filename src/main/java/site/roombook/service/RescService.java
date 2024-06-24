package site.roombook.service;

import site.roombook.domain.RescDto;

import java.util.List;

public interface RescService {
    List<RescDto> getRescsSuggestions(String keyword);

    void saveRescs(List<RescDto> rescs, int spaceNo, String fstRegrIdnfNo);

    List<RescDto> getRescs(int spaceNo);

    void updateRescs(int spaceNo, String idnfNo, List<RescDto> rescs);
}
