package common.coverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AllureResult(
        String status,
        @JsonProperty("labels") List<Label> labels
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Label(String name, String value) {
    }
}
