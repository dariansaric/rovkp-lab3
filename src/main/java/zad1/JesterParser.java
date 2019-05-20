//package zad1;
//
//import org.apache.commons.text.StringEscapeUtils;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.FieldType;
//import org.apache.lucene.index.IndexOptions;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.store.RAMDirectory;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//
//import static zad1.Utilites.ID_FIELD_NAME;
//import static zad1.Utilites.TEXT_FIELD_NAME;
//
//public class JesterParser {
//
//    private Path filePath;
//    private Map<Integer, String> documentMap = new LinkedHashMap<>();
//
//    public JesterParser(Path filePath) throws IOException {
//        if (!Files.exists(Objects.requireNonNull(filePath))) {
//            throw new IllegalArgumentException("Provided path doesn't exist: " + filePath);
//        }
//        this.filePath = filePath;
//        parse();
//    }
//
//    private static FieldType createTextFieldType() {
//        FieldType textFieldType = new FieldType();
//        textFieldType.setStored(false);
//        textFieldType.setTokenized(true);
//        textFieldType.setIndexOptions(IndexOptions.NONE);
//
//        return textFieldType;
//    }
//
//    private static FieldType createIDFieldType() {
//        FieldType idFieldType = new FieldType();
//        idFieldType.setStored(true);
//        idFieldType.setTokenized(false);
//        idFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
//
//        return idFieldType;
//    }
//
////    private void parse() throws IOException {
////        List<String> lines = Files.readAllLines(filePath);
////        for (int i = 0; i < lines.size(); i++) {
////            String l = lines.get(i);
////            if (l.isEmpty()) {
////                continue;
////            }
////            if (l.matches("\\d:")) {
////                int id = Integer.parseInt(l.split(":")[0]);
////                StringBuilder joiner = new StringBuilder();
////                for (++i; !lines.get(i).isEmpty(); i++) {
////                    joiner.append(lines.get(i));
////                }
////                documentMap.put(id,
////                        StringEscapeUtils.unescapeHtml4(
////                                joiner.toString().toLowerCase().
////                                        replaceAll("<.*?>", "")));
////            }
////        }
////    }
//
//    public Path getFilePath() {
//        return filePath;
//    }
//
//    public Map<Integer, String> getDocumentMap() {
//        return documentMap;
//    }
//
//    public void getDocuments() {
//        if (documents != null) {
//            return documents;
//        }
//        documents = new ArrayList<>(documentMap.size());
//
//        for (Map.Entry<Integer, String> e : documentMap.entrySet()) {
//            Document d = new Document();
//            d.add(new Field(ID_FIELD_NAME, e.getKey().toString(), ID_FIELD_TYPE));
//            d.add(new Field(TEXT_FIELD_NAME, e.getValue(), TEXT_FIELD_TYPE));
//            documents.add(d);
//        }
//
//        return documents;
//    }
//}
