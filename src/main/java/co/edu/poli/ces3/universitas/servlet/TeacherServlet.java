package co.edu.poli.ces3.universitas.servlet;

import co.edu.poli.ces3.universitas.database.ConexionMySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "teacherServlet", value = "/teacher")
public class TeacherServlet extends HttpServlet {

    private ConexionMySql cnn;

    public void init(){
        cnn = new ConexionMySql();
        System.out.println("Inicia servlet de docente!!!!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Llega hasta el metodo GET");
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.print("Hola desde el servlet de docente!!!<br>");
        writer.print("<b>DNI: </b>" + req.getParameter("dni"));
        writer.flush();
        super.doGet(req, resp);
    }
}
