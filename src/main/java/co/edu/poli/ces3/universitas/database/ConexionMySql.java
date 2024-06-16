package co.edu.poli.ces3.universitas.database;

import co.edu.poli.ces3.universitas.dao.User;

import java.sql.*;
import java.util.*;

public class ConexionMySql {

    private String user;
    private String password;
    private int port;
    private String host;
    private String nameDatabase;
    private Connection cnn;

    public ConexionMySql(){ON
        this.user = "root";
        password = "";
        port = 3306;
        host = "localhost";
        nameDatabase = "ces3-universitas";
    }

    private void createConexion(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cnn = DriverManager.getConnection(
                    "jdbc:mysql://" +host+":"+port+"/"+nameDatabase, user, password
            );
            System.out.println("Successful connection");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("An error occurred during the connection");
            throw new RuntimeException(e);
        }
    }

    //consultas enrollment del taller 5

//consulta para obtener los enrrollment

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

//consulta para actualizar los enrollment

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

    public List<User> getUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> list = new ArrayList<>();
        try {
            createConexion();
            Statement stmt = cnn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                list.add(new User(result.getInt("id"),
                        result.getString("name"),
                        result.getString("lastName"),
                        result.getString("mail"),
                        result.getString("password"),
                        result.getDate("createdAt"),
                        result.getDate("updatedAt"),
                        result.getDate("deletedAt")
                ));
            }
            stmt.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(cnn != null)
                cnn.close();
        }
    }

    public static void main(String[] args) {
        ConexionMySql conection = new ConexionMySql();
        try {
            conection.getUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            createConexion();
            PreparedStatement stm = cnn.prepareStatement(sql);
            stm.setInt(1, Integer.parseInt(id));
            ResultSet result = stm.executeQuery();
            if(result.next())
            return new User(result.getString("name"), result.getString("lastName"));
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (cnn != null)
                cnn.close();
        }
        return null;
    }

    //metodo para buscar si email existe en la tabla de usuarios
    public User findUserForEmail(String email) throws SQLException {
        User usuario = null;
        createConexion();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String consulta = "SELECT * FROM users WHERE mail=?";
            statement = cnn.prepareStatement(consulta);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                usuario = new User();
                usuario.setId(resultSet.getInt("id"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setName(resultSet.getString("nombre"));
                usuario.setLastName(resultSet.getString("apellido"));

            }
        } finally {
            cerrarRecursos(statement, resultSet);
        }
        return usuario;
    }
//meto para insertar nuevo usuario en caso de que el email no exista

    public void insertNewUser(User usuario) throws SQLException {
        createConexion();
        PreparedStatement statement = null;
        try {
            String consulta = "INSERT INTO users (mail, name, lastName) VALUES (?, ?, ?)";
            statement = cnn.prepareStatement(consulta);
            statement.setString(1, usuario.getEmail());
            statement.setString(2, usuario.getName());
            statement.setString(3, usuario.getLastName());
            // ¡Agregar otras propiedades del usuario según la estructura de la tabla!
            statement.executeUpdate();
        } finally {
            cerrarRecursos(statement, null);
        }
    }
//metodo para obtener el id del ultimo usuario ingresado en el sistema
    public int getUltimateId() throws SQLException {
        int ultimoIdInsertado = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = cnn.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            if (resultSet.next()) {
                ultimoIdInsertado = resultSet.getInt(1);
            }
        } finally {
            cerrarRecursos(statement, resultSet);
        }
        return ultimoIdInsertado;
    }

    private void cerrarRecursos(Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (cnn != null) {
                cnn.close();
            }
        } catch (SQLException e) {
            System.out.println("No se puede cerrar recursos: " + e.getMessage());
        }
    }
}



