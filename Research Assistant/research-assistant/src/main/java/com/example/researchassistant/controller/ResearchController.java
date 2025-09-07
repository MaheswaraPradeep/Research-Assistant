package com.example.researchassistant.controller;

import com.example.researchassistant.ResearchRequest;
import com.example.researchassistant.service.ResearchService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ResearchController {

    private final ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest researchRequest) {
        String result=researchService.processContent(researchRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
