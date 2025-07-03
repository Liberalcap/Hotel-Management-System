import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HotelReservationSystem
{
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = ""; // Remove before pushing to GitHub

    public static void main(String[] args) throws SQLException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("JDBC Driver not found: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Scanner scanner = new Scanner(System.in))
        {

            while (true)
            {
                System.out.println("\nHOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        return;
                    default:
                        System.out.println("Invalid choice, Try again.");
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.nextLine();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('"
                    + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement())
            {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0)
                {
                    System.out.println("Reservation successful!");
                }
                else
                {
                    System.out.println("Reservation failed.");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws SQLException
    {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            System.out.println("Current Reservations:");
            while (resultSet.next())
            {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("ID: %d | Name: %s | Room: %d | Contact: %s | Date: %s%n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            String sql = "SELECT room_number FROM reservations WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql))
            {
                if (resultSet.next())
                {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room Number: " + roomNumber);
                }
                else
                {
                    System.out.println("Reservation not found.");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservationId))
            {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.nextLine();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', room_number = "
                    + newRoomNumber + ", contact_number = '" + newContactNumber + "' WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement())
            {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0)
                {
                    System.out.println("Reservation updated successfully!");
                }
                else
                {
                    System.out.println("Reservation update failed.");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId))
            {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement())
            {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0)
                {
                    System.out.println("Reservation deleted successfully!");
                }
                else
                {
                    System.out.println("Deletion failed.");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId)
    {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            return resultSet.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException
    {
        System.out.print("Exiting System");
        for (int i = 0; i < 5; i++)
        {
            System.out.print(".");
            Thread.sleep(450);
        }
        System.out.println("\nThank you for using Hotel Reservation System!");
    }
}
