package com.sowatec.search;

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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LuceneSearch {

    public static void main(String[] args) throws IOException {


/*        Lucene lucene = new Lucene();
        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("test7");
        lucene.setFilterDataType("");
        System.out.println(search("5029", lucene));*/

    }

    public static String search(String searchWord, Lucene lucene) throws IOException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        StringBuilder sb = new StringBuilder();

        System.out.println("FilterPath: " + lucene.getFilterPath());
        System.out.println("FilterName: " + lucene.getFilterFileName());
        System.out.println("FilterDataType: " + lucene.getFilterDataType());

        Path desktopDir = Paths.get(lucene.getFilterPath());
        List<Path> desktopFiles = Files.list(desktopDir).collect(Collectors.toList());

        for (Path path : desktopFiles) {

            if (lucene.getFilterPath() != null || lucene.getFilterFileName() != null || lucene.getFilterDataType() != null) {

                if (path.toFile().getAbsolutePath().endsWith(lucene.getFilterFileName() + lucene.getFilterDataType())) {
                    getIndexWriter(w, sb, path);
                }
                else if (path.toFile().getName().startsWith(lucene.getFilterFileName()) && lucene.getFilterDataType().isBlank()) {
                    getIndexWriter(w, sb, path);
                }
                else if (lucene.getFilterFileName().isBlank() && path.toFile().getAbsolutePath().endsWith(lucene.getFilterDataType())) {
                    getIndexWriter(w, sb, path);
                }
            }
        }
        w.close();
        IndexReader reader = getIndexReader(searchWord, lucene, analyzer, index, sb);
        reader.close();
        return sb.toString();
    }

    private static void getIndexWriter(IndexWriter w, StringBuilder sb, Path path) throws IOException {

        if (path.toFile().getAbsolutePath().endsWith(".xlsx")) {

            List<String> list = excel(path);

/*            ListIterator<String> listIterator = list.listIterator();

            System.out.println(list);
            while (listIterator.hasNext()) {
                if (listIterator.next().equals("arcana")) {
                    System.out.println("BINGO");
                }
            }*/

            sb.append("\n### " + list.size() + " - " + path.getFileName());
            addDoc(w, String.valueOf(list), path.toFile().getAbsolutePath());

        } else {
            String contents = readTextContents(path);
            sb.append("\n### " + contents.length() + " - " + path.getFileName());
            addDoc(w, contents, path.toFile().getAbsolutePath());
        }
    }

    private static IndexReader getIndexReader(String searchWord, Lucene lucene, StandardAnalyzer analyzer, Directory index, StringBuilder sb) throws IOException {

        // 2. query
        Query q = null;
        try {
            q = new QueryParser("title", analyzer).parse(searchWord);
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
        lucene.setHitAmount(hits.length);
        sb.append("\nFound " + hits.length + " hits.\n");
        List<String> paths = new ArrayList<>();

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            paths.add((i + 1) + ". " + d.get("isbn"));
            sb.append((i + 1) + ". " + d.get("isbn") + "\n" /*+ "\t" + d.get("title")*/);
        }
        lucene.setPath(paths);
        lucene.setInput(searchWord);
        sb.append("\nquerystring: " + searchWord);
        return reader;
    }

    public static List<String> excel(Path path) throws IOException {
        FileInputStream file = new FileInputStream(String.valueOf(path));
        Workbook workbook = new XSSFWorkbook(file);
        List<String> contents = new LinkedList<>();

        for (Iterator<Sheet> it = workbook.sheetIterator(); it.hasNext(); ) {
            Sheet sheet = it.next();
            for (Row row: sheet) {
                for (Cell cell: row) {
                    switch(cell.getCellType()) {
                        case STRING:
                            System.out.println(cell.getStringCellValue());
                            contents.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            String number = String.valueOf(cell.getNumericCellValue());
                            number = number.replaceAll("[\\.]0", "");
                            System.out.println(number);
                            contents.add(number);
                            break;
                        default:
                            System.out.println(cell);
                            contents.add(cell.toString());
                            break;
                    }
                }
            }
        }
        return contents;
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
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
