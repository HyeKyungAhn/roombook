package store.roombook.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import store.roombook.domain.SpaceDto;

import java.io.IOException;
import java.time.LocalTime;

public class SpaceDeserializer extends JsonDeserializer<SpaceDto> {

    @Override
    public SpaceDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Integer spaceNo = node.has("spaceNo")? node.get("spaceNo").asInt():null;
        String spaceNm = node.has("spaceNm")? node.get("spaceNm").asText():null;
        Integer spaceMaxPsonCnt = node.has("maxCapacity")? node.get("maxCapacity").asInt():null;
        String spaceLocDesc = node.has("spaceLoc")? node.get("spaceLoc").asText():null;
        String spaceAdtnDesc = node.has("spaceDesc")? node.get("spaceDesc").asText():null;
        Integer spaceMaxRsvsTms = node.has("maxRsvsTms")? node.get("maxRsvsTms").asInt():null;
        LocalTime spaceUsgPosblBgnTm = node.has("startTm")? LocalTime.parse(node.get("startTm").asText()):null;
        LocalTime spaceUsgPosblEndTm = node.has("finishTm")? LocalTime.parse(node.get("finishTm").asText()):null;
        Character spaceWkendUsgPosbleYn = node.has("weekend")? node.get("weekend").asText().charAt(0):null;
        Character spaceHideYn = node.has("hide")? node.get("hide").asText().charAt(0):null;
        String fstRegrIdnfNo = node.has("fstRegrIdnfNo")? node.get("fstRegrIdnfNo").asText():null;
        String lastUpdrIdnfNo = node.has("lastUpdrIdnfNo")? node.get("lastUpdrIdnfNo").asText():null;

        SpaceDto.Builder spaceDtoBuilder = new SpaceDto.Builder()
                .spaceNo(spaceNo)
                .spaceNm(spaceNm)
                .spaceMaxPsonCnt(spaceMaxPsonCnt)
                .spaceLocDesc(spaceLocDesc)
                .spaceAdtnDesc(spaceAdtnDesc)
                .spaceMaxRsvdTms(spaceMaxRsvsTms)
                .spaceUsgPosblBgnTm(spaceUsgPosblBgnTm)
                .spaceUsgPosblEndTm(spaceUsgPosblEndTm)
                .spaceWkendUsgPosblYn(spaceWkendUsgPosbleYn)
                .spaceHideYn(spaceHideYn)
                .fstRegrIdnfNo(fstRegrIdnfNo)
                .lastUpdrIdnfNo(lastUpdrIdnfNo);

        return spaceDtoBuilder.build();
    }
}
