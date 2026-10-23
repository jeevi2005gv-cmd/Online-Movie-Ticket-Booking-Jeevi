import java.util.ArrayList;
import java.util.List;

/**
 * Holds the "who is logged in / what are they booking right now" state
 * while the app is running. Shared statically across all GUI frames -
 * this is the Java Swing equivalent of a web app's session.
 */
public class SessionData {
    // customer session
    public static int userId = -1;
    public static String username;
    public static String fullName, phone, email, address;

    // admin session
    public static int adminId = -1;
    public static String adminUsername;

    // booking-in-progress
    public static Showtime currentShowtime;
    public static List<String> selectedSeats = new ArrayList<>();
    public static String lastBookingCode;

    public static void clearUser() {
        userId = -1;
        username = fullName = phone = email = address = null;
        currentShowtime = null;
        selectedSeats = new ArrayList<>();
        lastBookingCode = null;
    }

    public static void clearAdmin() {
        adminId = -1;
        adminUsername = null;
    }
}