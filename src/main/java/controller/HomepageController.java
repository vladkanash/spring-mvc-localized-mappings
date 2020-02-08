package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import request.annotation.LocalizedRequestMapping;

@Controller
public class HomepageController {

    @LocalizedRequestMapping(code = "homepage", method = RequestMethod.GET)
    public String getHomepage(Model model) {
        model.addAttribute("title", "Homepage Title");
        return "/homepage";
    }
}
