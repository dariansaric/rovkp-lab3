package lab.zad2;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class Main {
    private static final Path INPUT_PATH = Paths.get("./collaborative_similarity.csv");
    private static final Path OUTPUT_PATH = Paths.get("./item_similarity-lab.csv");
    private static int MAT_SIZE;


    /**
     * Pokreće se uz dva argumenta:
     * <ul>
     * <li>težinski faktor sličnosti po sadržaju</li>
     * <li>težinski faktor sličnosti po ocjenama korisnika</li>
     * </ul>
     *
     * @param args vidi gore
     *
     * @throws IOException ako dođe do pogreške čitanja ulazne datoteke
     */
    public static void main(String[] args) throws IOException {
        int w1 = Integer.parseInt(args[0]);
        int w2 = Integer.parseInt(args[1]);
//        List<String> lines = Files.readAllLines(INPUT_PATH, StandardCharsets.UTF_8);
//        MAT_SIZE = lines.size();
        MAT_SIZE = 150;
        double[][] mat1 = new double[MAT_SIZE][MAT_SIZE];
        double[][] mat2 = new double[MAT_SIZE][MAT_SIZE];
        Files.lines(INPUT_PATH).forEach(l -> {
            String[] fields = l.split(",");
            int x = Integer.parseInt(fields[0]);
            int y = Integer.parseInt(fields[1]);
            mat1[x - 1][y - 1] = Double.parseDouble(fields[2]);
            mat2[x - 1][y - 1] = Double.parseDouble(fields[3]);
        });

//        populateMatrixes(lines, mat1, mat2);
        normalizeMatrix2(mat1);
        double sum = 0;
        for (int i = 0; i < 149; i++) {
            sum += mat1[149][i];
        }

        normalizeMatrix2(mat2);

        double[][] hybrid = calculateHybridMatrix(mat1, mat2, w1, w2);

        writeSims(hybrid);

    }

    private static void writeSims(double[][] mat) throws IOException {
        StringJoiner j = new StringJoiner("\n", "", "\n");
//        sims.forEach((k, v) -> {
//            if (v > 0) {
//                j.add(k + "," + v);
//            }
//        });

        for (int i = 0; i < MAT_SIZE; i++) {
            for (int k = 0; k < MAT_SIZE; k++) {
                j.add((i + 1) + "," + (k + 1) + "," + mat[i][k]);
            }
        }

        Files.write(OUTPUT_PATH,
                j.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static double[][] calculateHybridMatrix(double[][] mat1, double[][] mat2, int w1, int w2) {
        double[][] mat = new double[MAT_SIZE][MAT_SIZE];
        double combined = w1 + w2;
        for (int i = 0; i < MAT_SIZE; i++) {
            for (int j = 0; j < MAT_SIZE; j++) {
                mat[i][j] = w1 / combined * mat1[i][j] + w2 / combined * mat2[i][j];
            }
        }

        return mat;
    }

    private static void normalizeMatrix1(double[][] mat) {
        for (int i = 0; i < MAT_SIZE; i++) {
            double max = Collections.max(Arrays.asList(ArrayUtils.toObject(mat[i])));
            double min = Collections.min(Arrays.asList(ArrayUtils.toObject(mat[i])));
            for (int j = 0; j < MAT_SIZE; j++) {
                mat[i][j] = (mat[i][j] - min) / (max - min);
            }
        }
    }

    private static void normalizeMatrix2(double[][] mat) {
        for (int i = 0; i < MAT_SIZE; i++) {
            double sum = Arrays.stream(mat[i]).sum();
            for (int j = 0; j < MAT_SIZE; j++) {
                mat[i][j] /= sum;
            }
        }
    }

    private static void populateMatrixes(List<String> lines, double[][] mat1, double[][] mat2) {
        lines.forEach(l -> {
            String[] fields = l.split(",");
            int x = Integer.parseInt(fields[0]);
            int y = Integer.parseInt(fields[1]);
            mat1[x][y] = Double.parseDouble(fields[2]);
            mat2[x][y] = Double.parseDouble(fields[3]);
        });
    }
}
