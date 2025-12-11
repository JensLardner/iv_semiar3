public class BasicJDBC {
    private void accessDB(){
        public static Connection accessDB() {
            String url = "jdbc:postgresql://localhost:5432/uni";

            try {
                Class.forName("org.postgresql.Driver");

                Connection conn = DriverManager.getConnection(url);
                System.out.println("Successfully connected to database!");
                return conn;

            } catch (ClassNotFoundException e) {
                System.err.println("PostgreSQL JDBC Driver not found: " + e.getMessage());
                e.printStackTrace();
                return null;

            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }

    }
}
