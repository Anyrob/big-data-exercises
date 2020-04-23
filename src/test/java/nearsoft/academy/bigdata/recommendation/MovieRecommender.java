package nearsoft.academy.bigdata.recommendation;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import java.io.*;
import java.util.*;


public class MovieRecommender {
    private final String TEMP_FILE = "/Users/anarobles/Desktop/dataset.csv";
    private int usersCount = 0, productsCount = 0, reviewsCount = 0, u=0, n=0;
    private HashMap<String, Integer> users = new HashMap();
            HashMap<String, Integer> products = new HashMap();
            HashMap<Integer, String> inverseHash = new HashMap();
    //private Set<String> users = new HashSet<>(); Set<String> products = new HashSet<>();

    public MovieRecommender(String sourcePath) throws IOException { /////


            String userId = "", productId = "", score = "";
            String line;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(sourcePath));
            File outputFileStream = new File(TEMP_FILE);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileStream));
            System.out.println("Buffers inicializados");

            while ((line = bufferedReader.readLine()) != null) {

                if (line.contains("product/productId")) {
                    productId = line.substring(19, 29);
                    if (!products.containsKey(productId)) {
                        productsCount++;
                        products.put(productId, productsCount);
                        inverseHash.put(productsCount, productId);
                        n = productsCount;
                    } else{
                        n = products.get(productId);
                    }
                }

                if (line.contains("review/userId")) {
                    userId = line.substring(15);
                    if (!users.containsKey(userId)) {
                        usersCount++;
                        users.put(userId, usersCount);
                        u=usersCount;
                    }else{
                        u=users.get(userId);
                    }

                }
                if (line.contains("review/score")) {
                    score = line.substring(14);
                    reviewsCount++;
                    bufferedWriter.write(u + "," + n + "," + score + "\n");
                }
            }
            bufferedWriter.close();
            bufferedReader.close();
            System.out.println("Archivo creado");

    }

    List<String> getRecommendationsForUser(String UserId) throws IOException, TasteException {
        DataModel model = new FileDataModel(new File(TEMP_FILE));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        //System.out.println(Integer.parseInt(userId));
        List<RecommendedItem> recommendations = recommender.recommend(users.get(UserId),3);
        List<String> recommendationsForUser = new ArrayList<String>();
        for (RecommendedItem recommendation : recommendations) {
            recommendationsForUser.add(inverseHash.get((int)recommendation.getItemID()));
        }
        return recommendationsForUser;
    }

    public int getTotalUsers(){
        return usersCount;
    }
    public int getTotalProducts(){
        return productsCount;
    }
    public int getTotalReviews(){
        return reviewsCount;
    }

}