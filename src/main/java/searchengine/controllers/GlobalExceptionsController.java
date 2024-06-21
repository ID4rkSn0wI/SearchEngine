package searchengine.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import searchengine.dto.exceptions.InvalidSearchSiteUrlParamException;

@ControllerAdvice
public class GlobalExceptionsController {
    @ExceptionHandler(InvalidSearchSiteUrlParamException.class)
    public String handlerEmptySearchException(InvalidSearchSiteUrlParamException e, RedirectAttributes redirectAttributes, Model model) {
        return "404";
    }
}