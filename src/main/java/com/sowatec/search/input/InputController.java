package com.sowatec.search.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InputController {

    private final InputService inputService;

    @Autowired
    public InputController(InputService inputService) {
        this.inputService = inputService;
    }


    @GetMapping("/inputs")
    public List<Input> getInputs() {
        return inputService.getInputs();
    }

    @PostMapping("/inputs/add")
    public void registerNewInput(@RequestBody Input input) {
        inputService.addNewInput(input);
    }

}