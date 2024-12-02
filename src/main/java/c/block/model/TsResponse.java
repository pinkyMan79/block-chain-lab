package c.block.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class TsResponse {

    @JsonProperty("status")
    Integer status;
    @JsonProperty("statusString")
    String statusString;
    @JsonProperty("timeStampToken")
    TsResponseBody timeStampToken;

}

