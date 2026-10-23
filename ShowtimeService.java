import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShowtimeService {

    private static MongoCollection<Document> showtimes() {
        return MongoConnection.getDatabase().getCollection("showtimes");
    }

    /**
     * One showtime per movie (the EARLIEST one) - used on the Movie Selection
     * screen. This is the MongoDB equivalent of the
     * "WHERE id IN (SELECT MIN(id) ... GROUP BY movie_id)" SQL trick.
     */
    public static List<Showtime> getOneShowtimePerMovie() {
        List<Showtime> result = new ArrayList<>();
        Set<Integer> seenMovies = new HashSet<>();

        FindIterable<Document> all = showtimes().find().sort(Sorts.ascending("showtimeId"));
        for (Document s : all) {
            int movieId = s.getInteger("movieId");
            if (seenMovies.contains(movieId)) continue;
            seenMovies.add(movieId);

            Document movie = MovieService.getMovieById(movieId);
            if (movie == null) continue;

            result.add(toShowtime(s, movie));
        }
        return result;
    }

    /** All showtimes (for the admin Show Time Management table). */
    public static List<Showtime> getAllShowtimes() {
        List<Showtime> result = new ArrayList<>();
        FindIterable<Document> all = showtimes().find().sort(Sorts.ascending("showtimeId"));
        for (Document s : all) {
            Document movie = MovieService.getMovieById(s.getInteger("movieId"));
            if (movie == null) continue;
            result.add(toShowtime(s, movie));
        }
        return result;
    }

    public static Showtime getShowtimeById(int showtimeId) {
        Document s = showtimes().find(Filters.eq("showtimeId", showtimeId)).first();
        if (s == null) return null;
        Document movie = MovieService.getMovieById(s.getInteger("movieId"));
        if (movie == null) return null;
        return toShowtime(s, movie);
    }

    public static int insertShowtime(int movieId, String date, String time, String screen, double price) {
        int id = SequenceService.getNextSequence("showtimes");
        Document doc = new Document("showtimeId", id)
                .append("movieId", movieId)
                .append("showDate", date)
                .append("showTime", time)
                .append("screen", screen)
                .append("ticketPrice", price);
        showtimes().insertOne(doc);
        return id;
    }

    public static void updateShowtime(int showtimeId, int movieId, String date, String time, String screen, double price) {
        showtimes().updateOne(Filters.eq("showtimeId", showtimeId),
                Updates.combine(
                        Updates.set("movieId", movieId),
                        Updates.set("showDate", date),
                        Updates.set("showTime", time),
                        Updates.set("screen", screen),
                        Updates.set("ticketPrice", price)
                ));
    }

    /** Blocks deletion if any bookings reference this showtime (no FK in MongoDB, so we check manually). */
    public static boolean deleteShowtime(int showtimeId) {
        MongoCollection<Document> bookings = MongoConnection.getDatabase().getCollection("bookings");
        long linked = bookings.countDocuments(Filters.eq("showtimeId", showtimeId));
        if (linked > 0) return false;
        showtimes().deleteOne(Filters.eq("showtimeId", showtimeId));
        return true;
    }

    private static Showtime toShowtime(Document s, Document movie) {
        Showtime st = new Showtime();
        st.showtimeId = s.getInteger("showtimeId");
        st.movieId = movie.getInteger("movieId");
        st.movieName = movie.getString("movieName");
        st.language = movie.getString("language");
        st.genre = movie.getString("genre");
        st.duration = movie.getString("duration");
        st.showDate = s.getString("showDate");
        st.showTime = s.getString("showTime");
        st.screen = s.getString("screen");
        Object price = s.get("ticketPrice");
        st.ticketPrice = (price instanceof Integer) ? ((Integer) price).doubleValue() : (Double) price;
        return st;
    }
}