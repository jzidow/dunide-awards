package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    private final IndexService indexService;
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping()
    public String getIndex(Model model) {
        logger.info("Handling GET request for / - getIndex(model)");
        indexService.populateIndex(model);
        logger.info("Returning view: index with populated model");
        return "index";
    }
}