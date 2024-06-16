package controller;

// Importaciones necesarias para Firestore y Firebase
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import firebase.FirebaseInitializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/comments") // Define el URL en el cual este servlet estará disponible
public class CommentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            // Inicializa Firebase cuando el servlet es inicializado
            FirebaseInitializer.initialize();
        } catch (IOException e) {
            // Si hay un error al inicializar Firebase, lanza una excepción de servlet
            throw new ServletException("Failed to initialize Firebase", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtiene el cuerpo de la solicitud como un BufferedReader
        BufferedReader reader = request.getReader();
        // Usa Gson para convertir el JSON de la solicitud a un mapa de datos
        Gson gson = new Gson();
        Map<String, Object> requestData = gson.fromJson(reader, HashMap.class);

        // Obtiene una instancia de la base de datos Firestore
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> futureResult;

        // Verifica el tipo de solicitud
        if ("comment".equals(requestData.get("type"))) {
            // Si es un nuevo comentario
            DocumentReference newCommentRef = db.collection("comments").document();
            futureResult = newCommentRef.set(requestData);
        } else if ("reply".equals(requestData.get("type")) && requestData.containsKey("document")) {
            // Si es una respuesta a un comentario existente
            String documentId = (String) requestData.get("document");
            DocumentReference commentDocRef = db.collection("comments").document(documentId);
            DocumentReference newReplyRef = commentDocRef.collection("replies").document();
            futureResult = newReplyRef.set(requestData);
        } else {
            // Si la solicitud no es válida, responde con un error 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        try {
            // Espera a que la operación de escritura en Firestore se complete
            WriteResult writeResult = futureResult.get();
            // Configura la respuesta como JSON y escribe la hora de actualización
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(Map.of("updateTime", writeResult.getUpdateTime().toString())));
        } catch (Exception e) {
            // Si hay un error al escribir en Firestore, lanza una excepción de servlet
            throw new ServletException("Failed to write to Firestore", e);
        }
    }
}
