import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static MongoCollection<Document> users() {
        return MongoConnection.getDatabase().getCollection("users");
    }

    /** Returns the user document if username+password match, else null. */
    public static Document login(String username, String password) {
        Document user = users().find(Filters.eq("username", username)).first();
        if (user != null && password.equals(user.getString("password"))) {
            return user;
        }
        return null;
    }

    /** Only used to tell "username not found" apart from "wrong password" on the login screen. */
    public static boolean usernameExists(String username) {
        return users().find(Filters.eq("username", username)).first() != null;
    }

    public static List<Document> getAllUsers() {
        List<Document> list = new ArrayList<>();
        FindIterable<Document> result = users().find().sort(Sorts.ascending("userId"));
        for (Document d : result) list.add(d);
        return list;
    }

    public static boolean deleteUser(int userId) {
        Document user = users().find(Filters.eq("userId", userId)).first();
        if (user == null) return false;
        String username = user.getString("username");

        MongoCollection<Document> bookings = MongoConnection.getDatabase().getCollection("bookings");
        long linked = bookings.countDocuments(Filters.eq("username", username));
        if (linked > 0) return false; // has bookings, block delete

        users().deleteOne(Filters.eq("userId", userId));
        return true;
    }
}