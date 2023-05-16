package com.sowatec.search;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class LuceneSearch {

    public static void main(String[] args) throws IOException {
        /*
        Lucene lucene = new Lucene();
        lucene.setFilterPath("D:\\DATA\\Lucene_test");
        lucene.setFilterFileName("test2");
        lucene.setFilterDataType(".txt");
        System.out.println(search("arcana", lucene));
        */
    }

    public static String search(String searchWord, Lucene lucene) throws IOException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        StringBuilder sb = new StringBuilder();

        //System.out.println(lucene.getFilterPath());
        //System.out.println(lucene.getFilterFileName());
        //System.out.println(lucene.getFilterDataType());

        /**
         * Filter Path here
         */
        Path desktopDir = Paths.get(lucene.getFilterPath());

        List<Path> desktopFiles = Files.list(desktopDir).collect(Collectors.toList());

        for (Path path : desktopFiles) {

            if (!lucene.getFilterPath().isBlank()
                    && !lucene.getFilterFileName().isBlank()
                    && !lucene.getFilterDataType().isBlank()
                    && path.toFile().getAbsolutePath().endsWith(lucene.getFilterFileName() + lucene.getFilterDataType())
            ) {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());
            }

            /**
            if (lucene.getFilterDataType().isBlank() && lucene.getFilterFileName().isBlank()) {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());

            } else if (path.toFile().getAbsolutePath().startsWith(lucene.getFilterFileName())) {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());

            } else if (path.toFile().getAbsolutePath().endsWith(lucene.getFilterFileName() + lucene.getFilterDataType())) {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());
            }
             */
        }

        /*
               for (Path path : desktopFiles) {
            if (
                    path.toFile().getAbsolutePath().endsWith(lucene.getFilterDataType()) && !path.toFile().getAbsolutePath().endsWith("test1.txt")) {



                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());



            } else {


                sb.append("-- ignore " + path.getFileName() + "\n");


            }
        }
        */



        w.close();


        // 2. query
        String[] args = new String[0];
        String querystring = args.length > 0 ? args[0] : searchWord;

        Query q = null;
        try {
            q = new QueryParser("title", analyzer).parse(querystring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        /**
         * Hit Amounts here after Search
         */
        lucene.setHitAmount(hits.length);
        sb.append("\nFound " + hits.length + " hits.\n");
        /**
         * 1/3 List for Paths after Search
         */
        List<String> paths = new ArrayList<>();

        for(int i=0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            /**
             * 2/3 Added Paths to List after Search
             */
            paths.add((i + 1) + ". " + d.get("isbn"));
            sb.append((i + 1) + ". " + d.get("isbn") + "\n" /*+ "\t" + d.get("title")*/);
        }
        /**
         * 3/3 Paths here after Search
         */
        lucene.setPath(paths);
        /**
         * Input here after Search
         */
        lucene.setInput(querystring);
        sb.append("\nquerystring: " + querystring);
        reader.close();
        return sb.toString();
    }



    public static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    public static String readTextContents(Path filePath) {
    	StringBuilder contentBuilder = new StringBuilder();

    	try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {

    	  stream.forEach(s -> contentBuilder.append(s).append("\n"));
    	} catch (IOException e) {
    	}
    	return contentBuilder.toString();
    }
}
