package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agree")
public class TermsController {
    @GetMapping("/terms")
    @Operation(summary = "약관 동의")
    public String terms(Model model) {
        model.addAttribute("companyName", "NodeUp");
        return "agree-terms";
    }

    @GetMapping("/privacy")
    @Operation(summary = "개인정보동의")
    public String privacy(Model model) {
        model.addAttribute("companyName", "NodeUp");
        return "agree-privacy";
    }
}
