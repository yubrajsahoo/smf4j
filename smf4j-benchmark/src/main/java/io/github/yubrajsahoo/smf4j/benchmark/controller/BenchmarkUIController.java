package io.github.yubrajsahoo.smf4j.benchmark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BenchmarkUIController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/report")
    public String report() {
        return "report";
    }
}
