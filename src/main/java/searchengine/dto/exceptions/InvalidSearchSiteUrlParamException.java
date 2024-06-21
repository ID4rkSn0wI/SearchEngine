package searchengine.dto.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidSearchSiteUrlParamException extends Exception {
    private String message;
    public InvalidSearchSiteUrlParamException(String message) {
        this.message = message;
    }
}
