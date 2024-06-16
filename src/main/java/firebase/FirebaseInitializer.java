package firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    /**
     * Inicializa Firebase utilizando un archivo de clave de cuenta de servicio JSON.
     * Este método debe ser llamado al inicio de la aplicación.
     *
     * @throws IOException si ocurre un error al leer el archivo de clave de cuenta de servicio.
     */
    public static void initialize() throws IOException {
        // Lee el archivo de clave de cuenta de servicio desde la ruta especificada
        FileInputStream serviceAccount =
                new FileInputStream("path/to/serviceAccountKey.json");

        // Configura las opciones de Firebase utilizando las credenciales obtenidas del archivo
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        // Inicializa Firebase con las opciones configuradas
        FirebaseApp.initializeApp(options);
    }

    /**
     * Obtiene una instancia de Firestore para interactuar con la base de datos Firestore de Firebase.
     *
     * @return instancia de Firestore
     */
    public static Firestore getFirestore() {
        // Devuelve la instancia de Firestore utilizando FirestoreClient de Firebase
        return FirestoreClient.getFirestore();
    }
}
