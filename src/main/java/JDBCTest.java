import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuriy on 14.03.16.
 */
public class JDBCTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = null;

        try {

            Properties properties = new Properties();
            JDBCTest jdbcTest = new JDBCTest();
            jdbcTest.loadProperties(properties);

            String dbUrl = properties.getProperty("database");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            connection =
                    DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connection has been established");

//            jdbcTest.addStudent(connection);
            jdbcTest.addBatchExample(connection);

            List<Student> students = jdbcTest.getStudents(connection);
            printStudents(students);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private static void printStudents(List<Student> students) {
        for (Student student : students) {
            System.out.print(student.getFirstname() + " | ");
            System.out.print(student.getLastname() + " | ");
            System.out.println(student.getAge() + " | ");
        }
    }

    private void loadProperties(Properties properties) throws IOException {
        InputStream stream = getClass().getResourceAsStream("jdbc.properties");
        properties.load(stream);
    }

    private List<Student> getStudents(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("select * from students");
        ResultSet set = statement.getResultSet();

        List<Student> list = new ArrayList<Student>();
        while (set.next()) {
            Student student = new Student();
            student.setFirstname(set.getString(2));
            student.setLastname(set.getString(3));
            student.setAge(set.getInt(4));
            list.add(student);
        }
        return list;
    }

    private void addStudent(Connection connection) throws SQLException {
        String sql = "insert into students (firstname, lastname, age) values (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "John");
        statement.setString(2, "Smith");
        statement.setInt(3, 25);
        statement.execute();
    }

    private void addBatchExample(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        String sql = "insert into students (firstname, lastname, age) values (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 1; i < 10; i++) {
            statement.setString(1, "John" + i);
            statement.setString(2, "Smith" + i);
            statement.setInt(3, 25);
            statement.addBatch();
        }

        statement.executeBatch();
        connection.setAutoCommit(true);
    }
}
