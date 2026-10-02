package io.github.yubrajsahoo.smf4j.benchmark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for benchmark UI.
 */
@Controller
public class BenchmarkUIController {

    /**
     * Default constructor for BenchmarkUIController.
     */
    public BenchmarkUIController() {
    }

    /**
     * Index page.
     *
     * @return view name
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Report page.
     *
     * @return view name
     */
    @GetMapping("/report")
    public String report() {
        return "report";
    }
}
