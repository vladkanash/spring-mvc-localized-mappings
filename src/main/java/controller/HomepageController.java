package controller;

import annotation.LocalizedRequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomepageController {

    @LocalizedRequestMapping(code = "homepage", method = RequestMethod.GET)
    public String getHomepage(Model model) {
        model.addAttribute("title", "Homepage Title");
        return "/homepage";
    }
}
