package com.shaurya.spring.timecomplexityanalyzer.controller;

import com.shaurya.spring.timecomplexityanalyzer.dto.AnalysisRequest;
import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;
import com.shaurya.spring.timecomplexityanalyzer.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {
    private final AnalysisService analysisService;
    @Autowired
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    @RequestMapping("/analyze")
    public ComplexityResult analyze(@RequestBody AnalysisRequest analysisRequest){
        return analysisService.Analyze(analysisRequest.code());
    }

}
