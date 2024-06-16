package co.edu.poli.ces3.universitas.servlet;
import co.edu.poli.ces3.universitas.database.ConexionMySql;
import co.edu.poli.ces3.universitas.dao.*;


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
        enrollmentDao = new EnrollmentDao();
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(splits[1]);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }

        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        Map<String, Object> updates = gson.fromJson(reader, HashMap.class);

        try {
            Enrollment enrollment = enrollmentDao.getEnrollment(userId);
            if (enrollment == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

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

            boolean updated = enrollmentDao.updateEnrollment(enrollment);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(enrollment));
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update user");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}