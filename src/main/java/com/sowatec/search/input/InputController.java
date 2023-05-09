package com.sowatec.search.input;

import com.sowatec.search.LuceneSearch;
import com.sowatec.search.Lucene;
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

        /*
    @PostMapping("/inputs/add")
    public void registerNewInput(@RequestBody Input input) {
        inputService.addNewInput(input);
    }
    */


    //----------------------------------------------
    // POST OR GET for entire result of Lucene
    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs",
            params = { "post" }
    )
    @ResponseBody
    public String getInputNew(@RequestParam("post") String input) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        String result = luceneSearch.search(input, lucene);
        return result;
    }


    //----------------------------------------------
    // GETs Amount of hits that match the search

    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs",
            params = { "int" }
    )
    @ResponseBody
    public int getHitAmount(@RequestParam("int") String input) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        luceneSearch.search(input, lucene);
        return lucene.getHitAmount();
    }

    //----------------------------------------------
    // GETs Paths
    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs",
            params = { "path" }
    )
    @ResponseBody
    public List<String> getPaths(@RequestParam("path") String input) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        luceneSearch.search(input, lucene);
        return lucene.getPath();
    }

}