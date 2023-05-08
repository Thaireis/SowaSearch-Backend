package com.sowatec.search.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InputService {

    private final InputRepository inputRepository;

    @Autowired
    public InputService(InputRepository inputRepository) {
        this.inputRepository = inputRepository;
    }

    public List<Input> getInputs() {
        return inputRepository.findAll();
    }

    public void addNewInput(Input input) {
        Optional<Input> inputByUserInput = inputRepository.findInputByUserInput(input.getUserInput());
        if(inputByUserInput.isPresent()) {
            throw new IllegalStateException("Name taken");
        }
        inputRepository.save(input);
    }
}
