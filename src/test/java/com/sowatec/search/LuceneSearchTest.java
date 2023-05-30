package com.sowatec.search;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LuceneSearchTest {

    @Test
    void onlyPath() throws IOException, InvalidFormatException {

        Lucene lucene = new Lucene();

        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("");
        lucene.setFilterDataType("");

        String searchWord = "arcana";
        System.out.println(LuceneSearch.search(searchWord, lucene));
    }

    @Test
    void withName() throws IOException, InvalidFormatException {

        Lucene lucene = new Lucene();

        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("test2");
        lucene.setFilterDataType("");

        String searchWord = "arcana";
        System.out.println(LuceneSearch.search(searchWord, lucene));
    }

    @Test
    void withType() throws IOException, InvalidFormatException {

        Lucene lucene = new Lucene();

        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("");
        lucene.setFilterDataType(".txt");

        String searchWord = "arcana";
        System.out.println(LuceneSearch.search(searchWord, lucene));
    }

    @Test
    void test2txt() throws IOException, InvalidFormatException {

        Lucene lucene = new Lucene();

        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("test2");
        lucene.setFilterDataType(".txt");

        String searchWord = "arcana";
        System.out.println(LuceneSearch.search(searchWord, lucene));
    }
}