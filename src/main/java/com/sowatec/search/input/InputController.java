package com.sowatec.search.input;

import com.sowatec.search.LuceneSearch;
import com.sowatec.search.Lucene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class InputController {

    private final InputService inputService;

    @Autowired
    public InputController(InputService inputService) {
        this.inputService = inputService;
    }


    //----------------------------------------------
    // GETs Amount of hits that match the search

    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs/filter/int",
            params = { "post", "filterPath", "filterName", "filterType" }
    )
    @ResponseBody
    public int getHitAmount(
            @RequestParam("post") String input,
            @RequestParam("filterPath") String filterPath,
            @RequestParam("filterName") String filterName,
            @RequestParam("filterType") String filterType
    ) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        lucene.setFilterPath(filterPath);
        lucene.setFilterFileName(filterName);
        lucene.setFilterDataType(filterType);
        luceneSearch.search(input, lucene);
        return lucene.getHitAmount();
    }

    //----------------------------------------------
    // GETs Paths
    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs/filter/paths",
            params = { "post", "filterPath", "filterName", "filterType" }
    )
    @ResponseBody
    public List<String> getPaths(
            @RequestParam("post") String input,
            @RequestParam("filterPath") String filterPath,
            @RequestParam("filterName") String filterName,
            @RequestParam("filterType") String filterType
    ) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        lucene.setFilterPath(filterPath);
        lucene.setFilterFileName(filterName);
        lucene.setFilterDataType(filterType);
        luceneSearch.search(input, lucene);
        return lucene.getPath();
    }

    //----------------------------------------------
    // POST OR GET for entire result of Lucene
    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs/filter/result",
            params = { "post", "filterPath", "filterName", "filterType" }
    )
    @ResponseBody
    public String getInputNew(
            @RequestParam("post") String input,
            @RequestParam("filterPath") String filterPath,
            @RequestParam("filterName") String filterName,
            @RequestParam("filterType") String filterType
    ) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        lucene.setFilterPath(filterPath);
        lucene.setFilterFileName(filterName);
        lucene.setFilterDataType(filterType);
        String result = luceneSearch.search(input, lucene);
        return result;
    }

    //----------------------------------------------
    //Filter
    //----------------------------------------------
    /*
    @CrossOrigin()
    @RequestMapping(
            method = {RequestMethod.POST, RequestMethod.GET},
            value = "/inputs/filter",
            params = { "post", "filterPath", "filterName", "filterType" }
    )
    @ResponseBody
    public String setFilter(
            @RequestParam("post") String input,
            @RequestParam("filterPath") String filterPath,
            @RequestParam("filterName") String filterName,
            @RequestParam("filterType") String filterType

    ) throws IOException {
        Lucene lucene = new Lucene();
        LuceneSearch luceneSearch = new LuceneSearch();
        lucene.setFilterPath(filterPath);
        lucene.setFilterFileName(filterName);
        lucene.setFilterDataType(filterType);
        String result = luceneSearch.search(input, lucene);
        return result;
    }
    */
}