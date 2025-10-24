package kr.kakao_tech_bootcamp.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ex")
public class TextExampleController {

    @GetMapping("/text-example")
    public String textExample(Model model) {
        model.addAttribute("data", "Hello <b>world!</b>");
        return "text";
    }
}
