package store.roombook.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import store.roombook.domain.SignupInput;

import java.io.IOException;

public class SignupInputDeserializer extends JsonDeserializer<SignupInput> {
    @Override
    public SignupInput deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String name = node.has("name") ? node.get("name").asText() : null;
        String id = node.has("id") ? node.get("id").asText() : null;
        String pwd = node.has("pwd") ? node.get("pwd").asText() : null;
        String email = node.has("email") ? node.get("email").asText() : null;
        String verificationCode = node.has("verificationCode") ? node.get("verificationCode").asText() : null;
        String emplno = node.has("emplno") ? node.get("emplno").asText() : null;


        return SignupInput.Builder().name(name).id(id).pwd(pwd).email(email).verificationCode(verificationCode).emplno(emplno).build();
    }
}
