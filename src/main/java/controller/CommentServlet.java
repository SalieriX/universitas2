package controller;

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

@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            FirebaseInitializer.initialize();
        } catch (IOException e) {
            throw new ServletException("Failed to initialize Firebase", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        Map<String, Object> requestData = gson.fromJson(reader, HashMap.class);

        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> futureResult;

        if ("comment".equals(requestData.get("type"))) {
            // Es un nuevo comentario
            DocumentReference newCommentRef = db.collection("comments").document();
            futureResult = newCommentRef.set(requestData);
        } else if ("reply".equals(requestData.get("type")) && requestData.containsKey("document")) {
            // Es una respuesta a un comentario existente
            String documentId = (String) requestData.get("document");
            DocumentReference commentDocRef = db.collection("comments").document(documentId);
            DocumentReference newReplyRef = commentDocRef.collection("replies").document();
            futureResult = newReplyRef.set(requestData);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        try {
            WriteResult writeResult = futureResult.get();
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(Map.of("updateTime", writeResult.getUpdateTime().toString())));
        } catch (Exception e) {
            throw new ServletException("Failed to write to Firestore", e);
        }
    }
}
