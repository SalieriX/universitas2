package dao;

import model.Enrollment;

import java.sql.*;

public class EnrollmentDao {

    private String jdbcURL = "jdbc:mysql://localhost:3306/Universitas";
    private String jdbcUsername = "root";
    private String jdbcPassword = "";
    private Connection jdbcConnection;

    protected void connect() throws SQLException {
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        }
    }

    protected void disconnect() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }

    public Enrollment getEnrollment(Long userId) throws SQLException {
        Enrollment enrollment = null;
        String sql = "SELECT * FROM enrollments WHERE user_id = ?";

        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setLong(1, userId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String courseName = resultSet.getString("course_name");
            String semester = resultSet.getString("semester");
            String status = resultSet.getString("status");

            enrollment = new Enrollment();
            enrollment.setId(id);
            enrollment.setUserId(userId);
            enrollment.setCourseName(courseName);
            enrollment.setSemester(semester);
            enrollment.setStatus(status);
        }

        resultSet.close();
        statement.close();

        disconnect();

        return enrollment;
    }

    public boolean updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET course_name = ?, semester = ?, status = ? WHERE user_id = ?";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, enrollment.getCourseName());
        statement.setString(2, enrollment.getSemester());
        statement.setString(3, enrollment.getStatus());
        statement.setLong(4, enrollment.getUserId());

        boolean rowUpdated = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowUpdated;
    }
}