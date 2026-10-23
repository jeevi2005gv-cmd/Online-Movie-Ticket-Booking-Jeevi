import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MovieService {

    private static MongoCollection<Document> movies() {
        return MongoConnection.getDatabase().getCollection("movies");
    }

    /** Plain list of all movies, e.g. for a dropdown. */
    public static List<Document> getAllMovies() {
        List<Document> list = new ArrayList<>();
        FindIterable<Document> result = movies().find().sort(Sorts.ascending("movieId"));
        for (Document d : result) list.add(d);
        return list;
    }

    public static Document getMovieById(int movieId) {
        return movies().find(Filters.eq("movieId", movieId)).first();
    }

    public static int insertMovie(String name, String language, String genre, String duration) {
        int id = SequenceService.getNextSequence("movies");
        Document doc = new Document("movieId", id)
                .append("movieName", name)
                .append("language", language)
                .append("genre", genre)
                .append("duration", duration);
        movies().insertOne(doc);
        return id;
    }

    public static void updateMovie(int movieId, String name, String language, String genre, String duration) {
        movies().updateOne(Filters.eq("movieId", movieId),
                Updates.combine(
                        Updates.set("movieName", name),
                        Updates.set("language", language),
                        Updates.set("genre", genre),
                        Updates.set("duration", duration)
                ));
    }

    /**
     * Deletes a movie. MongoDB has no foreign-key constraints like MySQL,
     * so we manually check for linked showtimes first and block the delete
     * if any exist (same safety behaviour the MySQL version had automatically).
     */
    public static boolean deleteMovie(int movieId) {
        MongoCollection<Document> showtimes = MongoConnection.getDatabase().getCollection("showtimes");
        long linked = showtimes.countDocuments(Filters.eq("movieId", movieId));
        if (linked > 0) {
            return false; // caller shows "cannot delete, showtimes exist" message
        }
        movies().deleteOne(Filters.eq("movieId", movieId));
        return true;
    }
}