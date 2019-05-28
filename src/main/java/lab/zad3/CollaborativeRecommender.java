package lab.zad3;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CollaborativeRecommender {
    private static final Path SIMILARITY_PATH = Paths.get("./item_similarity-lab.csv");
    private static final Path RATINGS_PATH = Paths.get("./jester/jester_ratings.dat");

    public static void main(String[] args) throws IOException, TasteException {
        DataModel model = new FileDataModel(RATINGS_PATH.toFile(), "\t\t");
        ItemSimilarity similarity = new FileItemSimilarity(SIMILARITY_PATH.toFile());


        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new GenericItemBasedRecommender(dataModel, similarity);
            }
        };

        RecommenderEvaluator recEvaluator = new RMSRecommenderEvaluator();
        double score = recEvaluator.evaluate(builder, null, model, 0.5, 0.5);
        System.out.println(score);
    }

    private static class MyItem implements RecommendedItem {
        private long itemID;
        private double value;

        MyItem(long itemID, double value) {
            this.itemID = itemID;
            this.value = value;
        }

        @Override
        public long getItemID() {
            return itemID;
        }

        @Override
        public float getValue() {
            return (float) value;
        }
    }
}
