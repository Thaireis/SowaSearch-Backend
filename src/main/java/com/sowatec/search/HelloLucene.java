package com.sowatec.search;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class HelloLucene {



    public static void main(String[] args) throws IOException {


        /**
         * - Von Lucene auf Datenbank zugreifen
         * - Search Input erhalten
         * - Search Input einsetzen können
         * - Resultate von Lucene in eine Neue Tabelle registrieren
         * - GET Mapping auf Lucene Resultate
         * - Frontend erhält das Resultat von Lucene
         * **/

        search("arcana");

    }

    public static String search(String searchWord) throws IOException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        StringBuilder sb = new StringBuilder();

        Path desktopDir = Paths.get(LuceneConstants.FILE_PATH);

        List<Path> desktopFiles = Files.list(desktopDir).collect(Collectors.toList());

        for (Path path : desktopFiles) {
            if (path.toFile().getAbsolutePath().endsWith(LuceneConstants.FILE_NAME) &&
                    !path.toFile().getAbsolutePath().endsWith("test1.txt")) {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());
            } else {
                sb.append("-- ignore " + path.getFileName() + "\n");
            }
        }
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
        //System.out.println("Found " + hits.length + " hits.");
        sb.append("\nFound " + hits.length + " hits.\n");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            //System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
            sb.append((i + 1) + ". " + d.get("isbn") + "\n" /*+ "\t" + d.get("title")*/);
        }
        //System.out.println("querystring: " + querystring);
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
