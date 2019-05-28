package lab.zad1;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.util.*;

public class CollaborativeItemSimilarity implements ItemSimilarity {

    private final double[][] matrix;
    private final double[] norms;
    private final Map<Integer, Long> seqIdMap;
    private final Map<Long, Integer> idSeqMap;

    public CollaborativeItemSimilarity(DataModel model) throws TasteException {
        int n = model.getNumItems();
        matrix = new double[n][n];
        norms = new double[n];
        seqIdMap = new HashMap<>();
        idSeqMap = new HashMap<>();

        calculateCollaborativeModelMatrix(model);
    }

    private static void normalizeMatrix1(double[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            double max = Collections.max(Arrays.asList(ArrayUtils.toObject(mat[i])));
            double min = Collections.min(Arrays.asList(ArrayUtils.toObject(mat[i])));
            if (max == min && max == 0) {
                continue;
            }
            for (int j = 0; j < mat.length; j++) {
                mat[i][j] = (mat[i][j] - min) / (max - min);
            }
        }
    }

    @Override
    public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        return matrix[idSeqMap.get(itemID1)][idSeqMap.get(itemID2)];
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        double[] sims = new double[itemID2s.length];
        for (int i = 0; i < itemID2s.length; i++) {
            sims[i] = matrix[idSeqMap.get(itemID1)][idSeqMap.get(itemID2s[i])];
        }

        return sims;
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long[] allSimilarItemIDs(long itemID) throws TasteException {
        int len = matrix[idSeqMap.get(itemID)].length;
        long[] ids = new long[len];
//todo: ovo je komplicirano napravljeno??
        for (int i = 0; i < len; i++) {
            ids[i] = seqIdMap.get(i);
        }

        return ids;
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static void normalizeMatrix2(double[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            double sum = Arrays.stream(mat[i]).sum();
            if (sum == 0) {
                continue;
            }
            for (int j = 0; j < mat.length; j++) {
                mat[i][j] /= sum;
            }
        }
    }

    private void calculateCollaborativeModelMatrix(DataModel model) throws TasteException {

        int counter = 0;
        LongPrimitiveIterator iterator = model.getUserIDs();
        while (iterator.hasNext()) {
            long userId = iterator.nextLong();

            for (long ratedItemId1 : model.getItemIDsFromUser(userId)) {

                //get correct item1 seq num
                Integer seqId1 = idSeqMap.get(ratedItemId1);
                if (seqId1 == null) {
                    seqId1 = counter++;
                    seqIdMap.put(seqId1, ratedItemId1);
                    idSeqMap.put(ratedItemId1, seqId1);
                }

                norms[seqId1] += Math.pow(model.getPreferenceValue(userId, ratedItemId1), 2);

                for (long ratedItemId2 : model.getItemIDsFromUser(userId)) {

                    //get correct item2 seq num
                    Integer seqId2 = idSeqMap.get(ratedItemId2);
                    if (seqId2 == null) {
                        seqId2 = counter++;
                        seqIdMap.put(seqId2, ratedItemId2);
                        idSeqMap.put(ratedItemId2, seqId2);
                    }

                    matrix[seqId1][seqId2] += model.getPreferenceValue(userId, ratedItemId1)
                            * model.getPreferenceValue(userId, ratedItemId2);
                }
            }
        }

        //get cosine similarity from similarity sums
        for (int seqId1 = 0; seqId1 < matrix.length; seqId1++) {
            for (int seqId2 = 0; seqId2 < matrix.length; seqId2++) {
                if (matrix[seqId1][seqId2] != 0) {
                    matrix[seqId1][seqId2] /= Math.sqrt(norms[seqId1]) * Math.sqrt(norms[seqId2]);
                }
            }
        }

        normalizeMatrix1(matrix);
        normalizeMatrix2(matrix);
    }
}
