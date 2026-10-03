package com.example.vehicleinformation.config;

import com.example.vehicleinformation.util.BlockOptions;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("blockOptions")
    public List<String> blockOptions() {
        return BlockOptions.VALUES;
    }
}
