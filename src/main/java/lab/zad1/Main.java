package lab.zad1;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class Main {

    private static final Path OUTPUT_PATH = Paths.get("./collaborative_similarity.csv");

    public static void main(String[] args) throws IOException, TasteException {
        DataModel model = new FileDataModel(
                new File("./jester/jester_ratings.dat"), "\t\t");
        ItemSimilarity collaborativeItemSimilarity = new CollaborativeItemSimilarity(model);
        ItemSimilarity contentItemSimilarity = new FileItemSimilarity(new File("item_similarity.csv"));
        List<String> list = new LinkedList<>();
        LongPrimitiveIterator iterator = model.getItemIDs();
        while (iterator.hasNext()) {
            long itemId = iterator.nextLong();

            for (long simItem : collaborativeItemSimilarity.allSimilarItemIDs(itemId)) {
                list.add(itemId + "," + simItem + "," + collaborativeItemSimilarity.itemSimilarity(itemId, simItem) + "," + contentItemSimilarity.itemSimilarity(itemId, simItem));
            }
        }

        writeSims(list);
    }

    private static void writeSims(List<String> l) throws IOException {
        StringJoiner j = new StringJoiner("\n", "", "\n");

        l.forEach(j::add);


        Files.write(OUTPUT_PATH,
                j.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

}