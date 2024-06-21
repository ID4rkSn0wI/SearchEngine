package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import searchengine.config.Config;
import searchengine.config.Site;

import java.util.List;


@Slf4j
@Controller
public class DefaultController {
    private final Config config;

    public DefaultController(Config config) {
        this.config = config;
    }

    /**
     * Метод формирует страницу из HTML-файла index.html,
     * который находится в папке resources/templates.
     * Это делает библиотека Thymeleaf.
     */
    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @ModelAttribute("siteList")
    public List<Site> getSiteList(Model model) {
        return config.getSites();
    }
}
