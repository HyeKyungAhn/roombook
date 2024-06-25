package site.roombook.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import site.roombook.domain.RescDto;

import java.io.IOException;

public class RescDeserializer extends JsonDeserializer<RescDto> {
    @Override
    public RescDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Integer rescNo = node.has("rescNo")?node.get("rescNo").asInt():null;
        String rescNm = node.get("value").asText();
        Integer spaceNo = node.has("spaceNo")?node.get("spaceNo").asInt():null;
        String fstRegrEmplNo = node.has("fstRegrIdnfNo")?node.get("fstRegrIdnfNo").asText():null;
        String lastUpdrIdnfNo = node.has("lastUpdrIdnfNo")?node.get("lastUpdrIdnfNo").asText():null;

        RescDto.RescDtoBuilder build = RescDto.builder(rescNm)
                .rescNo(rescNo)
                .spaceNo(spaceNo)
                .fstRegrIdnfNo(fstRegrEmplNo)
                .lastUpdrIdnfNo(lastUpdrIdnfNo);

        return build.build();
    }
}
