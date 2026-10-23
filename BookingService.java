import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private static MongoCollection<Document> bookings() {
        return MongoConnection.getDatabase().getCollection("bookings");
    }

    /** All seat numbers currently booked (status = "Booked") for a showtime. */
    public static List<String> getBookedSeats(int showtimeId) {
        List<String> booked = new ArrayList<>();
        FindIterable<Document> result = bookings().find(
                Filters.and(Filters.eq("showtimeId", showtimeId), Filters.eq("status", "Booked")));
        for (Document d : result) {
            for (String s : d.getString("seatNumbers").split(",")) booked.add(s.trim());
        }
        return booked;
    }

    /** Saves a new booking and returns the generated booking code, e.g. "BK1001". */
    public static String insertBooking(String username, int showtimeId, List<String> seats,
                                        double ticketPrice, double totalAmount, String paymentMethod) {
        int seq = SequenceService.getNextSequence("bookings");
        String bookingCode = "BK" + (1000 + seq);

        Document doc = new Document("id", seq)
                .append("bookingCode", bookingCode)
                .append("username", username)
                .append("showtimeId", showtimeId)
                .append("seatNumbers", String.join(",", seats))
                .append("totalSeats", seats.size())
                .append("ticketPrice", ticketPrice)
                .append("totalAmount", totalAmount)
                .append("paymentMethod", paymentMethod)
                .append("status", "Booked")
                .append("createdAt", new java.util.Date());
        bookings().insertOne(doc);
        return bookingCode;
    }

    public static BookingRecord getBookingByCode(String code) {
        Document b = bookings().find(Filters.eq("bookingCode", code)).first();
        if (b == null) return null;
        return toBookingRecord(b);
    }

    public static List<BookingRecord> getAllBookings() {
        List<BookingRecord> result = new ArrayList<>();
        FindIterable<Document> all = bookings().find().sort(Sorts.descending("id"));
        for (Document b : all) result.add(toBookingRecord(b));
        return result;
    }

    public static List<BookingRecord> getBookingsByStatus(String status) {
        List<BookingRecord> result = new ArrayList<>();
        FindIterable<Document> all = bookings().find(Filters.eq("status", status)).sort(Sorts.descending("id"));
        for (Document b : all) result.add(toBookingRecord(b));
        return result;
    }

    public static void updateStatus(int id, String newStatus) {
        bookings().updateOne(Filters.eq("id", id), Updates.set("status", newStatus));
    }

    /** Booking report filtered by date range and (optionally) a specific movie. */
    public static List<BookingRecord> getReport(String fromDate, String toDate, Integer movieId) {
        List<BookingRecord> result = new ArrayList<>();
        FindIterable<Document> all = bookings().find().sort(Sorts.descending("id"));
        for (Document b : all) {
            BookingRecord rec = toBookingRecord(b);
            if (rec == null) continue;
            if (rec.showDate.compareTo(fromDate) < 0 || rec.showDate.compareTo(toDate) > 0) continue;
            if (movieId != null) {
                Document st = MongoConnection.getDatabase().getCollection("showtimes")
                        .find(Filters.eq("showtimeId", rec.showtimeId)).first();
                if (st == null || st.getInteger("movieId") != movieId) continue;
            }
            result.add(rec);
        }
        return result;
    }

    /** Dashboard stats: total bookings (status Booked) and total money collected. */
    public static long countBooked() {
        return bookings().countDocuments(Filters.eq("status", "Booked"));
    }

    public static double totalCollected() {
        double total = 0;
        FindIterable<Document> result = bookings().find(Filters.eq("status", "Booked"));
        for (Document d : result) total += toDouble(d.get("totalAmount"));
        return total;
    }

    private static BookingRecord toBookingRecord(Document b) {
        Showtime st = ShowtimeService.getShowtimeById(b.getInteger("showtimeId"));
        if (st == null) return null;

        BookingRecord rec = new BookingRecord();
        rec.id = b.getInteger("id");
        rec.bookingCode = b.getString("bookingCode");
        rec.showtimeId = st.showtimeId;
        rec.movieName = st.movieName;
        rec.showDate = st.showDate;
        rec.showTime = st.showTime;
        rec.screen = st.screen;
        rec.seatNumbers = b.getString("seatNumbers");
        rec.totalSeats = b.getInteger("totalSeats");
        rec.ticketPrice = toDouble(b.get("ticketPrice"));
        rec.totalAmount = toDouble(b.get("totalAmount"));
        rec.paymentMethod = b.getString("paymentMethod");
        rec.status = b.getString("status");
        return rec;
    }

    private static double toDouble(Object o) {
        if (o instanceof Integer) return ((Integer) o).doubleValue();
        if (o instanceof Double) return (Double) o;
        return Double.parseDouble(String.valueOf(o));
    }
}