package lab.zad3;

import lab.zad1.CollaborativeItemSimilarity;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class CollaborativeRecommender {
    private static final Path SIMILARITY_PATH = Paths.get("./item_similarity-lab.csv");
    private static final Path RATINGS_PATH = Paths.get("./jester/jester_ratings.dat");
    private static final Path OUTPUT_PATH = Paths.get("./collaborative-recommendations.dat");

    public static void main(String[] args) throws IOException, TasteException {
        DataModel model = new FileDataModel(RATINGS_PATH.toFile());
        ItemSimilarity similarity = new CollaborativeItemSimilarity(model);

        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new Recommender() {
                    @Override
                    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
                        return null;
                    }

                    @Override
                    public List<RecommendedItem> recommend(long userID, int howMany, boolean includeKnownItems) throws TasteException {
                        return null;
                    }

                    @Override
                    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
                        return null;
                    }

                    @Override
                    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer, boolean includeKnownItems) throws TasteException {
                        return null;
                    }

                    @Override
                    public float estimatePreference(long userID, long itemID) throws TasteException {
                        return 0;
                    }

                    @Override
                    public void setPreference(long userID, long itemID, float value) throws TasteException {

                    }

                    @Override
                    public void removePreference(long userID, long itemID) throws TasteException {

                    }

                    @Override
                    public DataModel getDataModel() {
                        return null;
                    }

                    @Override
                    public void refresh(Collection<Refreshable> alreadyRefreshed) {

                    }
                };
            }
        };


    }
}
