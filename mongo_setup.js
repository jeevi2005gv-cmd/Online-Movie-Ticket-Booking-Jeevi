// =========================================================
// Theatre Seat Booking System - MongoDB setup
// =========================================================

// Select the database
db = db.getSiblingDB("theatre_booking");

// ---- start clean (safe to re-run this script) ----
db.movies.drop();
db.showtimes.drop();
db.users.drop();
db.admin.drop();
db.bookings.drop();
db.counters.drop();

// ---- movies ----
db.movies.insertMany([
    { movieId: 1, movieName: "Kalki 2898 AD", language: "Tamil", genre: "Action", duration: "2D" },
    { movieId: 2, movieName: "Vikram", language: "Tamil", genre: "Action", duration: "2D" },
    { movieId: 3, movieName: "Leo", language: "Tamil", genre: "Action", duration: "2D" },
    { movieId: 4, movieName: "PS-2", language: "Tamil", genre: "Period", duration: "2D" },
    { movieId: 5, movieName: "Varisu", language: "Tamil", genre: "Drama", duration: "2D" }
]);

// ---- showtimes ----
db.showtimes.insertMany([
    { showtimeId: 1, movieId: 1, showDate: "2026-05-25", showTime: "10:30 AM", screen: "Screen 1", ticketPrice: 150.00 },
    { showtimeId: 2, movieId: 2, showDate: "2026-05-25", showTime: "02:00 PM", screen: "Screen 1", ticketPrice: 150.00 },
    { showtimeId: 3, movieId: 3, showDate: "2026-05-25", showTime: "06:30 PM", screen: "Screen 2", ticketPrice: 150.00 },
    { showtimeId: 4, movieId: 4, showDate: "2026-05-26", showTime: "09:30 AM", screen: "Screen 2", ticketPrice: 150.00 },
    { showtimeId: 5, movieId: 5, showDate: "2026-05-26", showTime: "06:30 PM", screen: "Screen 1", ticketPrice: 150.00 }
]);

// ---- users ----
db.users.insertMany([
    {
        userId: 1,
        username: "jeevitha",
        password: "1234",
        fullName: "Jeevitha S",
        phone: "9876543210",
        email: "jeevitha@email.com",
        address: "No.12, Anna Nagar, Trichy - 620002"
    }
]);

// ---- admin ----
db.admin.insertMany([
    {
        adminId: 1,
        username: "admin",
        password: "admin123"
    }
]);

// ---- sample bookings ----
db.bookings.insertMany([
    {
        id: 1,
        bookingCode: "BK1001",
        username: "jeevitha",
        showtimeId: 1,
        seatNumbers: "B4,D3,D4",
        totalSeats: 3,
        ticketPrice: 150.00,
        totalAmount: 450.00,
        paymentMethod: "UPI",
        status: "Booked",
        createdAt: new Date()
    },
    {
        id: 2,
        bookingCode: "BK1002",
        username: "jeevitha",
        showtimeId: 2,
        seatNumbers: "A5,A6",
        totalSeats: 2,
        ticketPrice: 150.00,
        totalAmount: 300.00,
        paymentMethod: "UPI",
        status: "Booked",
        createdAt: new Date()
    }
]);

// ---- counters ----
db.counters.insertMany([
    { _id: "movies", seq: 5 },
    { _id: "showtimes", seq: 5 },
    { _id: "users", seq: 1 },
    { _id: "bookings", seq: 2 }
]);

print("theatre_booking database created with sample data.");
print("Collections: " + db.getCollectionNames());