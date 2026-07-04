package com.shaurya.spring.timecomplexityanalyzer.controller;

import com.shaurya.spring.timecomplexityanalyzer.dto.AnalysisRequest;
import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;
import com.shaurya.spring.timecomplexityanalyzer.model.Submission;
import com.shaurya.spring.timecomplexityanalyzer.repository.SubmissionRepository;
import com.shaurya.spring.timecomplexityanalyzer.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalysisController {
    private final AnalysisService analysisService;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public ComplexityResult analyze(@RequestBody AnalysisRequest analysisRequest){
        if(analysisRequest.code() == null || analysisRequest.code().isBlank()){
            return new ComplexityResult("EMPTY INPUT", "-1");
        }
        return analysisService.analyze(analysisRequest.code());
    }
    @CrossOrigin(origins = "*")
    @GetMapping("/submissions")
    public List<Submission> submissions(){
        return submissionRepository.findAll();
    }
}
