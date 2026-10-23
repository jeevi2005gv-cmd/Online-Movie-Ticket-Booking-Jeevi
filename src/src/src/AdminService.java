import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class AdminService {

    private static MongoCollection<Document> admins() {
        return MongoConnection.getDatabase().getCollection("admin");
    }

    public static Document login(String username, String password) {
        Document admin = admins().find(Filters.eq("username", username)).first();
        if (admin != null && password.equals(admin.getString("password"))) {
            return admin;
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        return admins().find(Filters.eq("username", username)).first() != null;
    }

    /** Returns true only if currentPassword matches, then updates to newPassword. */
    public static boolean changePassword(int adminId, String currentPassword, String newPassword) {
        Document admin = admins().find(Filters.eq("adminId", adminId)).first();
        if (admin == null || !currentPassword.equals(admin.getString("password"))) {
            return false;
        }
        admins().updateOne(Filters.eq("adminId", adminId), Updates.set("password", newPassword));
        return true;
    }

    public static long totalMovies() {
        return MongoConnection.getDatabase().getCollection("movies").countDocuments();
    }

    public static long totalShowtimes() {
        return MongoConnection.getDatabase().getCollection("showtimes").countDocuments();
    }
}