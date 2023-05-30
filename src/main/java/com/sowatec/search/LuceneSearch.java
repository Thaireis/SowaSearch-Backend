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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
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

    public static void main(String[] args) throws IOException, InvalidFormatException {

        Lucene lucene = new Lucene();
        lucene.setFilterPath("D:\\DATA\\Lucene_test\\");
        lucene.setFilterFileName("");
        lucene.setFilterDataType("");

        List<String> ignoreList = new LinkedList<>();
        ignoreList.add("D:\\DATA\\Lucene_test\\test2.txt");
        ignoreList.add("D:\\DATA\\Lucene_test\\test4.txt");
        lucene.setIgnoreList(ignoreList);
        System.out.println(lucene.getIgnoreList());

        System.out.println(search("arcana", lucene));

    }

    public static String search(String searchWord, Lucene lucene) throws IOException, InvalidFormatException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        StringBuilder sb = new StringBuilder();

        Path desktopDir = Paths.get(lucene.getFilterPath());
        List<Path> desktopFiles = Files.list(desktopDir).collect(Collectors.toList());

        for (Path path : desktopFiles) {

            if (lucene.getFilterPath() != null || lucene.getFilterFileName() != null || lucene.getFilterDataType() != null) {

                if (path.toFile().getAbsolutePath().endsWith(lucene.getFilterFileName() + lucene.getFilterDataType())) {
                    getIndexWriter(w, sb, path, lucene);
                }
                else if (path.toFile().getName().startsWith(lucene.getFilterFileName()) && lucene.getFilterDataType().isBlank()) {
                    getIndexWriter(w, sb, path, lucene);
                }
                else if (lucene.getFilterFileName().isBlank() && path.toFile().getAbsolutePath().endsWith(lucene.getFilterDataType())) {
                    getIndexWriter(w, sb, path, lucene);
                }
            }
        }
        w.close();
        IndexReader reader = getIndexReader(searchWord, lucene, analyzer, index, sb);
        reader.close();
        return sb.toString();
    }

    private static void getIndexWriter(IndexWriter w, StringBuilder sb, Path path, Lucene lucene) throws IOException, InvalidFormatException {

        boolean found = false;

        for (String s : lucene.getIgnoreList()) {
            if (path.toFile().getAbsolutePath().equals(s)) {
                System.out.println(s + " found in IgnoreList");
                found = true;
            }
        }

        if (!found) {

            if (path.toFile().getAbsolutePath().endsWith(".docx")) {
                String text = word(path);
                sb.append("\n### " + text.length() + " - " + path.getFileName());
                addDoc(w, text, path.toFile().getAbsolutePath());
            }
            else if (path.toFile().getAbsolutePath().endsWith(".pdf")) {
                String text = pdf(path);
                sb.append("\n### " + text.length() + " - " + path.getFileName());
                addDoc(w, text, path.toFile().getAbsolutePath());
            }
            else if (path.toFile().getAbsolutePath().endsWith(".xlsx")) {
                List<String> list = excel(path);
            /*ListIterator<String> listIterator = list.listIterator();

            System.out.println(list);
            while (listIterator.hasNext()) {
                if (listIterator.next().equals("arcana")) {
                    System.out.println("BINGO");
                }
            }*/
                sb.append("\n### " + list.size() + " - " + path.getFileName());
                addDoc(w, String.valueOf(list), path.toFile().getAbsolutePath());
            }
            else {
                String contents = readTextContents(path);
                sb.append("\n### " + contents.length() + " - " + path.getFileName());
                addDoc(w, contents, path.toFile().getAbsolutePath());
            }
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

    private static String pdf(Path path) throws IOException {

        File file = new File(String.valueOf(path));
        FileInputStream fis = new FileInputStream(file);

        PDDocument document = PDDocument.load(fis);

        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(document);

        document.close();
        fis.close();

        return text;
    }

    public static String word(Path path) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(String.valueOf(path));
        XWPFDocument docx = new XWPFDocument(OPCPackage.open(fis));
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        return extractor.getText();
    }

    public static List<String> excel(Path path) throws IOException {
        FileInputStream file = new FileInputStream(String.valueOf(path));
        Workbook workbook = new XSSFWorkbook(file);
        DataFormatter formatter = new DataFormatter();
        List<String> contents = new LinkedList<>();

        for (Iterator<Sheet> it = workbook.sheetIterator(); it.hasNext(); ) {
            Sheet sheet = it.next();
            for (Row row: sheet) {
                for (Cell cell: row) {
                    String formattedNum = formatter.formatCellValue(cell);
                    contents.add(formattedNum);
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
