package co.edu.poli.ces3.universitas.servlet;

import co.edu.poli.ces3.universitas.dao.User;
import co.edu.poli.ces3.universitas.database.ConexionMySql;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "userServlet", value = "/user")
public class UserServlet extends MyServlet {
    private ConexionMySql cnn;
    private GsonBuilder gsonBuilder;
    private Gson gson;
    public void init() {
        cnn = new ConexionMySql();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>PUT</h1>");
        out.println("</body></html>");
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            if(request.getParameter("id") == null) {
                ArrayList<User> listUsers = (ArrayList<User>) cnn.getUsers();
                out.print(gson.toJson(listUsers));
            }else{
                User user = cnn.getUser(request.getParameter("id"));
                out.print(gson.toJson(user));
            }
            out.flush();
        } catch (SQLException e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }


    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //establece tipo de contendio a json
        resp.setContentType("application/json");
        //lector para leer solicitud con BufferedReader (se hace nuevo import)
        BufferedReader lector = req.getReader();
        StringBuilder cuerpoSolicitud = new StringBuilder();
        String linea;
        //lee solicitud y contruye cadena
        while ((linea = lector.readLine()) != null) {
            cuerpoSolicitud.append(linea);
        }
        //convierte json a gson
        User nuevoUsuario = gson.fromJson(cuerpoSolicitud.toString(), User.class);

        try {
            //busca si usuario ya existe en bd con el correo
            User usuarioExistente = cnn.findUserForEmail(nuevoUsuario.getEmail());
            if (usuarioExistente != null) {
                //si usuario existe en bd responde status 200
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.print(gson.toJson(usuarioExistente));
                out.flush();
            } else {
                //si no existe se inserta a bd
                cnn.insertNewUser(nuevoUsuario);
                //obtiene id del usuario recien insertado
                int idUsuarioInsertado = cnn.getUltimateId();
                //asigna id al nuevo usuario
                nuevoUsuario.setId(idUsuarioInsertado);
                //devuelve nuevo usuario y status 201
                resp.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = resp.getWriter();
                out.print(gson.toJson(nuevoUsuario));
                out.flush();
            }
        } catch (SQLException e) {
            System.out.println("algo salio mal: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = resp.getWriter();
            out.print("error de servidor");
            out.flush();
        }
    }

    public void destroy() {
    }

    @Override
    void saludar() {

    }
}