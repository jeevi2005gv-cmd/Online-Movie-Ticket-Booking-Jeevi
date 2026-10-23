/** A saved booking, read back from MongoDB for the confirmation/report screens. */
public class BookingRecord {
    public int id;
    public String bookingCode;
    public int showtimeId;
    public String movieName;
    public String showDate;
    public String showTime;
    public String screen;
    public String seatNumbers;
    public int totalSeats;
    public double ticketPrice;
    public double totalAmount;
    public String paymentMethod;
    public String status;
}