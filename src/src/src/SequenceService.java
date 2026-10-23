import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

/**
 * MongoDB has no built-in auto-increment (unlike MySQL's AUTO_INCREMENT).
 * This is the standard MongoDB "counters collection" pattern: one document
 * per counter name, incremented atomically with $inc every time a new id
 * is needed. Used for movies, showtimes, bookings and users.
 */
public class SequenceService {

    public static int getNextSequence(String counterName) {
        MongoCollection<Document> counters = MongoConnection.getDatabase().getCollection("counters");

        Document result = counters.findOneAndUpdate(
                Filters.eq("_id", counterName),
                Updates.inc("seq", 1),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
        );
        return result.getInteger("seq");
    }
}