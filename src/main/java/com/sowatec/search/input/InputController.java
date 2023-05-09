package com.sowatec.search.input;

import com.sowatec.search.HelloLucene;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class InputController {

    private final InputService inputService;

    @Autowired
    public InputController(InputService inputService) {
        this.inputService = inputService;
    }

    /*
    @GetMapping("/inputs")
    public List<Input> getInputs() {
        return inputService.getInputs();
    }
    */

    //----------------------------------------------

    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs/new",
            params = { "input" }
    )
    @ResponseBody
    public String getInput(@RequestParam("input") String input) throws IOException {
        HelloLucene lucene = new HelloLucene();
        String result = lucene.search(input);
        return result;
    }


    //----------------------------------------------

    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs",
            params = { "post" }
    )
    @ResponseBody
    public String getInputNew(@RequestParam("post") String input) throws IOException {
        HelloLucene lucene = new HelloLucene();
        String result = lucene.search(input);
        return result;
    }

    /*
    @PostMapping("/inputs/add")
    public void registerNewInput(@RequestBody Input input) {
        inputService.addNewInput(input);
    }
    */

}