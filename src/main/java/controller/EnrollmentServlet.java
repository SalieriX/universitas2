package controller;

import model.Enrollment;
import dao.EnrollmentDao;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/enrollments/*")
public class EnrollmentServlet extends HttpServlet {
    private EnrollmentDao enrollmentDao;

    @Override
    public void init() {
        // Inicializa el DAO de Enrollment en el método init del servlet
        enrollmentDao = new EnrollmentDao();
    }

    /**
     * Método para manejar solicitudes PATCH para actualizar datos de inscripción de usuarios.
     *
     * @param request  La solicitud HTTP que contiene los datos de actualización.
     * @param response La respuesta HTTP que se enviará al cliente.
     * @throws ServletException Si ocurre un error en la operación del servlet.
     * @throws IOException      Si ocurre un error de entrada o salida al leer los datos de la solicitud o escribir en la respuesta.
     */
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener la parte de la URL después de /enrollments/
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // Si no se especifica un ID de usuario en la URL, enviar error 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
            return;
        }

        // Separar la parte de la URL en partes usando /
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            // Si el formato de la URL no es válido, enviar error 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }

        Long userId;
        try {
            // Intentar convertir la segunda parte de la URL en un Long (ID de usuario)
            userId = Long.parseLong(splits[1]);
        } catch (NumberFormatException e) {
            // Si no se puede convertir a Long, enviar error 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }

        // Obtener el cuerpo de la solicitud como un BufferedReader
        BufferedReader reader = request.getReader();
        // Utilizar Gson para convertir el JSON del cuerpo de la solicitud a un mapa de actualizaciones
        Gson gson = new Gson();
        Map<String, Object> updates = gson.fromJson(reader, HashMap.class);

        try {
            // Obtener la inscripción actual del usuario desde el DAO
            Enrollment enrollment = enrollmentDao.getEnrollment(userId);
            if (enrollment == null) {
                // Si no se encuentra la inscripción, enviar error 404 (Not Found)
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            // Aplicar las actualizaciones al objeto de inscripción según las claves en el mapa de actualizaciones
            updates.forEach((key, value) -> {
                switch (key) {
                    case "course_name":
                        enrollment.setCourseName((String) value);
                        break;
                    case "semester":
                        enrollment.setSemester((String) value);
                        break;
                    case "status":
                        enrollment.setStatus((String) value);
                        break;
                }
            });

            // Actualizar la inscripción en la base de datos a través del DAO
            boolean updated = enrollmentDao.updateEnrollment(enrollment);
            if (updated) {
                // Si la actualización fue exitosa, enviar código de estado 200 (OK) y devolver la inscripción actualizada como JSON
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(enrollment));
            } else {
                // Si falla la actualización, enviar error 500 (Internal Server Error)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update user");
            }
        } catch (SQLException e) {
            // En caso de excepción SQL, lanzar una ServletException para manejar el error
            throw new ServletException(e);
        }
    }
}
