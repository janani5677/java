
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Bill {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurants?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "jan@#$54";

    // Format for printing the bill header
    public static void format(String date, String name) {
        System.out.println(" ");
        System.out.println("\t    GREAT OBSERVER RESTAURANT CLUB ");
        System.out.println("======================================================");
        System.out.println(" Date :" + date);
        System.out.print(" Invoice To :" + name);
        System.out.println(" ");
        System.out.println("---------------------------------------------------------------");
        System.out.print(" Items ");
        System.out.print(" \t\t\t QTY ");
        System.out.println(" \t\t\t Total ");
        System.out.println("---------------------------------------------------------------");
    }

    // Format for printing each bill item
    public static void printBill(String item, int qty, float price) {
        System.out.print("  " + item);
        System.out.print(" \t\t\t " + qty);
        System.out.println(" \t\t\t " + qty * price);
        System.out.println(" ");
    }

    // Calculate and print the final bill
    public static void calculateBill(float[] prices, int[] quantities) {
        float total = 0;
        for (int i = 0; i < prices.length; i++) {
            total += prices[i] * quantities[i];
        }
        float discount = 0.1f * total;
        float netTotal = total - discount;
        System.out.println("\n -------------------------------------------------------------------------\n");
        System.out.println(" Discount:                         10% \t\t\t " + discount);
        System.out.println("\n -------------------------------------------------------------------------\n");
        System.out.println(" Total Amount : \t\t\t\t\t\t " + netTotal);
        System.out.println("\n -------------------------------------------------------------------------\n");
        System.out.println(" ");
        System.out.println(" \t\t Thank you and visit again!!! \n\n ");
    }

    // Insert customer into the database
    public static int insertCustomer(String name) throws SQLException {
        String query = "INSERT INTO customer1 (name, date) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // return the generated customer ID
            }
        }
        return -1; // return -1 if insertion failed
    }

    // Insert an order into the database
    public static void insertOrder(int customerId, String item, int quantity, float price) throws SQLException {
        String query = "INSERT INTO order3 (customer_id, item_name, quantity, price, total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ps.setString(2, item);
            ps.setInt(3, quantity);
            ps.setFloat(4, price);
            ps.setFloat(5, quantity * price);
            ps.executeUpdate();
        }
    }

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }

        System.out.println("\t\n Welcome to the restaurant billing code using Java\n");
        Scanner sc = new Scanner(System.in);

        String inputName = "";
        while (inputName.isEmpty()) {
            System.out.print("ENTER CUSTOMER NAME: ");
            inputName = sc.nextLine().trim();
            if (inputName.isEmpty()) {
                System.out.println("Please enter a valid name.");
            }
        }

        System.out.print("Enter the Number Of orders requested by the customer: ");
        int inputNumber = sc.nextInt();

        String[] items = new String[inputNumber];
        int[] quantities = new int[inputNumber];
        float[] prices = new float[inputNumber];

        for (int i = 0; i < inputNumber; i++) {
            System.out.println(" ");
            System.out.println("Enter Item " + (i + 1) + ": ");
            System.out.print("Please Enter The Item Name: ");
            sc.nextLine(); // Consume newline from the previous input
            items[i] = sc.nextLine();
            System.out.print("Enter Quantity of Items: ");
            quantities[i] = sc.nextInt();
            System.out.print("Enter the Per Unit Charge of Item: ");
            prices[i] = sc.nextFloat();
            System.out.println(" ");
        }

        // Insert customer data
        try {
            int customerId = insertCustomer(inputName);
            if (customerId == -1) {
                System.out.println("Error inserting customer data.");
                return;
            }
            format("1/1/2024", inputName);

            // Insert orders and print bill
            for (int i = 0; i < inputNumber; i++) {
                insertOrder(customerId, items[i], quantities[i], prices[i]);
                printBill(items[i], quantities[i], prices[i]);
            }
            calculateBill(prices, quantities);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error connecting to the database.");
        } finally {
            sc.close(); // Close scanner to avoid resource leak
        }
    }
}
