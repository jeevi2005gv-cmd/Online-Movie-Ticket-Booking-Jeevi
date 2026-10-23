/** A movie + one of its showtimes (joined together for the UI tables). */
public class Showtime {
    public int showtimeId;
    public int movieId;
    public String movieName;
    public String language;
    public String genre;
    public String duration;
    public String showDate;   // "yyyy-MM-dd"
    public String showTime;   // e.g. "10:30 AM"
    public String screen;
    public double ticketPrice;
}