package com.hpk.dataexpoter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {
    @GetMapping("cpu")
    public int getCpuProcesses(){
        return Runtime.getRuntime().availableProcessors();
    }
}
