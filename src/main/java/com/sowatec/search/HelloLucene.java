package com.sowatec.search;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sowatec.search.input.InputController;
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

public class HelloLucene {
    public static void main(String[] args) throws IOException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);

        Path desktopDir = Paths.get(LuceneConstants.FILE_PATH);
        List<Path> desktopFiles = Files.list(desktopDir).collect(Collectors.toList());
        for (Path path : desktopFiles) {
  			if (path.toFile().getAbsolutePath().endsWith(LuceneConstants.FILE_NAME) &&
					!path.toFile().getAbsolutePath().endsWith("test1.txt")) {
				String contents = readTextContents(path);
                //String contents = LuceneConstants.CONTENTS;
				System.out.println("### " + contents.length() + " - " + path.getFileName());
				addDoc(w, contents, path.toFile().getAbsolutePath());
			} else {
				System.out.println("-- ignore " + path.getFileName());
			}
		}
        w.close();


        // 2. query
        String querystring = args.length > 0 ? args[0] : "arcana";

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
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
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
        }
        System.out.println("querystring: " + querystring);
        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private static  String readTextContents(Path filePath) {
    	StringBuilder contentBuilder = new StringBuilder();

    	try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {

    	  stream.forEach(s -> contentBuilder.append(s).append("\n"));
    	} catch (IOException e) {
    	  //handle exception
    	}

    	return contentBuilder.toString();
    }
}
