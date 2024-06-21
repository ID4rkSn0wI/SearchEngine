package searchengine.dto.answers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class DefaultAnswer {
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
}
