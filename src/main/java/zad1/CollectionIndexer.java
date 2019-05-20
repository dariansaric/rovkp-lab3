package zad1;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CollectionIndexer {
    private static final String PATH_TO_ITEMS = "./jester/jester_items.dat";
    private static final String ID_FIELD_NAME = "ID";
    private static final String TEXT_FIELD_NAME = "TEXT";
    private static final Path OUTPUT_PATH = Paths.get("./item_similarity.csv");
    private static final FieldType ID_FIELD_TYPE = createIDFieldType();
    private static final FieldType TEXT_FIELD_TYPE = createTextFieldType();
    private static Map<Integer, String> documentMap = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException, ParseException {

        //parsiranje ulaza
        //stvaranje liste dokumenata
        // parsiranje upita
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new RAMDirectory();
        getDocuments(Paths.get(args.length == 1 ? args[0] : PATH_TO_ITEMS), index);
        double[][] coMatrix = initMatrix();

        for (Map.Entry<Integer, String> e : documentMap.entrySet()) {
            Query query = new QueryParser(TEXT_FIELD_NAME, analyzer).parse(QueryParser.escape(e.getValue()));
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query, documentMap.size());
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc d : hits) {
                int docId = Integer.parseInt(reader.document(d.doc).getField(ID_FIELD_NAME).stringValue());
                coMatrix[e.getKey() - 1][docId - 1] = d.score;
            }
        }


        Map<String, Double> sims = calculateSimilarity(coMatrix);
        writeSims(sims);
    }

    private static double[][] initMatrix() {
        double[][] m = new double[documentMap.size()][documentMap.size()];
        for (double[] doubles : m) {
            Arrays.fill(doubles, 0.);
        }
        return m;
    }

    private static void writeSims(Map<String, Double> sims) throws IOException {
        StringJoiner j = new StringJoiner("\n");
        sims.forEach((k, v) -> {
            if (v > 0) {
                j.add(k + "," + v);
            }
        });
        Files.write(OUTPUT_PATH,
                j.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static Map<String, Double> calculateSimilarity(double[][] coMatrix) {
        Map<String, Double> coMap = new LinkedHashMap<>();
        normalizeMatrix(coMatrix);
        for (int i = 0; i < documentMap.size(); i++) {
            for (int j = i + 1; j < documentMap.size(); j++) {
                coMap.put((i + 1) + "," + (j + 1), (coMatrix[i][j] + coMatrix[j][i]) / 2);
            }
        }

        return coMap;
    }

    private static void normalizeMatrix(double[][] coMatrix) {
        for (int i = 0; i < documentMap.size(); i++) {
            double max = Collections.max(Arrays.asList(ArrayUtils.toObject(coMatrix[i])));
            for (int j = 0; j < documentMap.size(); j++) {
                coMatrix[i][j] /= max;
            }
        }
    }

    private static FieldType createTextFieldType() {
        FieldType textFieldType = new FieldType();
        textFieldType.setStored(false);
        textFieldType.setTokenized(true);
        textFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);

        return textFieldType;
    }

    private static FieldType createIDFieldType() {
        FieldType idFieldType = new FieldType();
        idFieldType.setStored(true);
        idFieldType.setTokenized(false);
        idFieldType.setIndexOptions(IndexOptions.NONE);

        return idFieldType;
    }

    private static void parse(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.isEmpty()) {
                continue;
            }
            if (l.matches("\\d+:")) {
                int id = Integer.parseInt(l.split(":")[0]);
                StringBuilder joiner = new StringBuilder();
                for (++i; !lines.get(i).isEmpty(); i++) {
                    joiner.append(lines.get(i));
                }
                documentMap.put(id,
                        StringEscapeUtils.unescapeHtml4(
                                joiner.toString().toLowerCase().
                                        replaceAll("<.*?>", "")));
            }
        }
    }

    private static void getDocuments(Path filePath, Directory index) throws IOException {
        parse(filePath);

        IndexWriter writer = new IndexWriter(index, new IndexWriterConfig());
        for (Map.Entry<Integer, String> e : documentMap.entrySet()) {
            Document d = new Document();
            d.add(new Field(ID_FIELD_NAME, e.getKey().toString(), ID_FIELD_TYPE));
            d.add(new Field(TEXT_FIELD_NAME, e.getValue(), TEXT_FIELD_TYPE));
            writer.addDocument(d);
        }

        writer.close();
    }
}
