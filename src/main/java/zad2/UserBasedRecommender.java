package zad2;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;

public class UserBasedRecommender {
    private static final Path RATINGS_PATH = Paths.get("./jester/jester_ratings.dat");
    private static final Path OUTPUT_PATH = Paths.get("./user-based-recommendations.dat");

    public static void main(String[] args) throws IOException, TasteException {
        //todo:evaluacija daje 0.0, nezz zašto
        DataModel model = new FileDataModel(RATINGS_PATH.toFile(), "\t\t");

        RecommenderBuilder builder = dataModel -> {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.9, similarity, model);

            return new GenericUserBasedRecommender(model, neighborhood, similarity);
        };

        Recommender recommender = builder.buildRecommender(model);
        LongPrimitiveIterator iterator = recommender.getDataModel().getUserIDs();
        StringJoiner joiner = new StringJoiner("\n", "", "\n");
        for (int i = 0; i < 100 && iterator.hasNext(); i++) {
            long id = iterator.nextLong();
            List<RecommendedItem> items = recommender.recommend(id, 10);
            StringJoiner joiner1 = new StringJoiner(",");
            items.forEach(it -> joiner1.add(Long.toString(it.getItemID())));
            joiner.add(id + ":" + joiner1.toString());
        }
        Files.write(OUTPUT_PATH,
                joiner.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        RecommenderEvaluator recEvaluator = new RMSRecommenderEvaluator();
        double score = recEvaluator.evaluate(builder, null, model, 0.5, 0.5);
        System.out.println(score);
    }
}
