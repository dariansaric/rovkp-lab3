package zad2;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;

public class ItemBasedRecommend {
    private static final Path SIMILARITY_PATH = Paths.get("./item_similarity.csv");
    private static final Path RATINGS_PATH = Paths.get("./jester/jester_ratings.dat");
    private static final Path OUTPUT_PATH = Paths.get("./item-based-recommendations.dat");

    public static void main(String[] args) throws IOException, TasteException {
        DataModel model = new FileDataModel(RATINGS_PATH.toFile(), "\t\t");

        ItemSimilarity similarity = new FileItemSimilarity(SIMILARITY_PATH.toFile());

        ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);

        LongPrimitiveIterator iterator = recommender.getDataModel().getUserIDs();
        StringJoiner joiner = new StringJoiner("\n", "", "\n");
        for (int i = 0; i < 100 && iterator.hasNext(); i++) {
            long id = iterator.nextLong();
//            System.out.println(i + " - Preporuka za " + id);
            List<RecommendedItem> items = recommender.recommend(id, 10);
            StringJoiner joiner1 = new StringJoiner(",");
            items.forEach(it -> joiner1.add(Long.toString(it.getItemID())));
            joiner.add(id + ":" + joiner1.toString());
        }
        Files.write(OUTPUT_PATH,
                joiner.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        evaluateRecommender(model, similarity);
    }

    private static void evaluateRecommender(DataModel model, ItemSimilarity similarity) throws TasteException {
        RandomUtils.useTestSeed();

        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new GenericItemBasedRecommender(dataModel, similarity);
            }
        };

        RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();

        double score = evaluator.evaluate(builder, null, model, 0.3, 0.7);
        System.out.println("Evaluacija: " + score);
    }
}
