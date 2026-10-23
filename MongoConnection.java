import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Single shared connection to the LOCAL MongoDB server.
 * MongoDB must already be running on your machine (mongod) before you
 * start this app - see README.txt for how to install and start it.
 *
 * No username/password is used here (default local MongoDB has no
 * authentication enabled), which keeps this simple for a college project.
 */
public class MongoConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE_NAME = "theatre_booking";

    private static MongoClient client;
    private static MongoDatabase database;

    /** Returns the shared theatre_booking database, connecting on first use. */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            client = new MongoClient(HOST, PORT);
            database = client.getDatabase(DATABASE_NAME);
        }
        return database;
    }

    public static void close() {
        if (client != null) {
            client.close();
            client = null;
            database = null;
        }
    }
}