package com.example.orderManagement.controller;

import com.example.orderManagement.service.HtmlGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseController.BASE_PATH + "/reports")
public class ReportController {

    @Autowired
    private final HtmlGeneratorService htmlGeneratorService;

    public ReportController(HtmlGeneratorService htmlGeneratorService) {
        this.htmlGeneratorService = htmlGeneratorService;
    }

    @GetMapping("/totalTransByCustomerId/{custId}")
    public String generateTotalTransByCustomerId(@PathVariable int custId) {
        return htmlGeneratorService.renderTotalTransByCustomerId(custId);
    }

    @GetMapping("/totalTransByProductCode/{productCode}")
    public String generateTotalTransByProductCode(@PathVariable String productCode) {
        return htmlGeneratorService.renderTotalTransByProductCode(productCode);
    }

    @GetMapping("/noOfTransByCustLocation/{location}")
    public String generateNumberOfTransByCustLocation(@PathVariable String location) {
        return htmlGeneratorService.renderNumberOfTransByCustomerLocation(location);
    }
}
