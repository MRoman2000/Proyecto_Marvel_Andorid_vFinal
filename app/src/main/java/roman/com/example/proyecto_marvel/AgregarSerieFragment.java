package roman.com.example.proyecto_marvel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgregarSerieFragment extends Fragment {
    // Se inicializan todos los elementos de la interfaz
    private static final int PICK_IMAGE_REQUEST = 1;

    // Se declaran los elementos de la interfaz de usuario
    private EditText editTextTitle, editTextDescription, editTextResourceURI,
            editTextStartYear, editTextEndYear, editTextStoryAvailable, editTextStoryReturned, editTextId;
    private ImageView imageView;
    private Uri uriSeleccionada;
    private Button buttonSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inicializar vistas
        View view = inflater.inflate(R.layout.fragment_agregar_serie, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextResourceURI = view.findViewById(R.id.editTextResourceURI);
        editTextStartYear = view.findViewById(R.id.editTextStartYear);
        editTextEndYear = view.findViewById(R.id.editTextEndYear);
        editTextStoryAvailable = view.findViewById(R.id.editTextStoryAvailable);
        editTextStoryReturned = view.findViewById(R.id.editTextStoryReturned);
        imageView = view.findViewById(R.id.imageView);
        buttonSave = view.findViewById(R.id.buttonSave);


        // Se permite al usuario seleccionar una imagen de la galería cuando se hace clic en ella.
        imageView.setOnClickListener(new View.OnClickListener() {
            // Se abre la galería para seleccionar una imagen.
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });
        // Acción del botón de guardar
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando se hace clic en el botón de guardar, se inicia el proceso de subir la imagen seleccionada a Firebase Storage y se guardan los datos de la serie en una base de datos MySQL
                subirImagenAFirebaseStorage();
            }
        });
        return view;
    }

    // Se utiliza para abrir la galería de imágenes
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Se llama cuando se selecciona una imagen de la galería. Se obtiene la URI de la imagen seleccionada y se muestra en la imageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uriSeleccionada = data.getData();
            imageView.setImageURI(uriSeleccionada);
        }
    }

    // Se  realiza operaciones de guardado de datos en segundo plano. Se utiliza para guardar los datos de la serie en una base de datos MySQL
    @SuppressLint("StaticFieldLeak")
    private class GuardarDatosTask extends AsyncTask<Void, Void, Boolean> {
        private String imageUrl;

        public GuardarDatosTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Obtener datos de los EditText
            ConexionMySQL conexionMySQL = new ConexionMySQL();
            conexionMySQL.obtenerConexion();
            // Obtener el texto de los EditText
            String idText = editTextId.getText().toString();
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            String resourceURI = editTextResourceURI.getText().toString();
            String startYearText = editTextStartYear.getText().toString();
            String endYearText = editTextEndYear.getText().toString();
            String storyAvailableText = editTextStoryAvailable.getText().toString();
            String storyReturnedText = editTextStoryReturned.getText().toString();

            // Verificar que los campos no estén vacíos
            if (idText.isEmpty() || title.isEmpty() || description.isEmpty() || resourceURI.isEmpty() ||
                    startYearText.isEmpty() || endYearText.isEmpty() || storyAvailableText.isEmpty() || storyReturnedText.isEmpty()) {
                // Si algún campo está vacío, retornar falso para indicar que hay un error
                return false;
            }

            // Convertir los valores de texto a enteros
            int id = Integer.parseInt(idText);
            int startYear = Integer.parseInt(startYearText);
            int endYear = Integer.parseInt(endYearText);
            int storyAvailable = Integer.parseInt(storyAvailableText);
            int storyReturned = Integer.parseInt(storyReturnedText);

            // Realizar la inserción en la base de datos
            String sql = "INSERT INTO Series (id, title, description, resourceURI, startYear, endYear, urlImagen, storyAvailable, storyReturned) "
                    + "VALUES ('" + id + "', '" + title + "', '" + description + "', '" + resourceURI + "', " + startYear
                    + ", " + endYear + ", '" + imageUrl + "', " + storyAvailable + ", " + storyReturned + ")";
            conexionMySQL.insertarDatos(sql);
            conexionMySQL.cerrarConexion();

            // Retornar verdadero para indicar que la operación se completó con éxito
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            // Si success es false, significa que hubo un error y algún campo estaba vacío
            if (!success) {
                // Mostrar un mensaje de error al usuario
                showToast("Todos los campos son obligatorios");
            } else {
                // Mostrar un mensaje de éxito al usuario
                borrarCampos();
                showToast("Datos guardados con éxito");
            }
        }
    }

    // Método para mostrar un Toast con un mensaje
    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Se utiliza para subir la imagen seleccionada a Firebase Storage
    private void subirImagenAFirebaseStorage() {
        // Obtener la referencia de Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // Generar un nombre único para la imagen
        String imageName = UUID.randomUUID().toString();
        // Crear la referencia de la imagen en Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + imageName);
        // Verificar si la URI es válida antes de intentar subir la imagen
        if (uriSeleccionada != null) {
            // Subir la imagen
            imageRef.putFile(uriSeleccionada)
                    .addOnSuccessListener(taskSnapshot -> {
                        // La imagen se subió con éxito
                        // Obtener la URL de la imagen
                        obtenerUrlDeFirebaseStorage(imageRef);
                    })
                    .addOnFailureListener(exception -> {
                        // Manejar errores al subir la imagen
                        Toast.makeText(getActivity().getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Manejar el caso en el que no se seleccionó ninguna imagen
            Toast.makeText(getActivity().getApplicationContext(), "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // Se llama después de que la imagen se sube con éxito a Firebase Storage. Obtiene la URL
    private void obtenerUrlDeFirebaseStorage(StorageReference imageRef) {
        // Obtener la URL de la imagen desde Firebase Storage
        imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d("foto", "Title: " + imageUrl);
                    new GuardarDatosTask(imageUrl).execute();
                    // Guardar la URL en Firebase Firestore o hacer lo que necesites con ella
                    guardarUrlEnFirestore(imageUrl);
                    // Mostrar un Toast de confirmación
                })
                .addOnFailureListener(exception -> {
                    // Manejar errores al obtener la URL de la imagen
                    Toast.makeText(getActivity().getApplicationContext(), "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                });
    }

    // Se utiliza para guardar la URL de la imagen en Firestore
    private void guardarUrlEnFirestore(String imageUrl) {
        // Obtener una instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Crear un nuevo documento en la colección "series"
        Map<String, Object> serie = new HashMap<>();
        serie.put("imageUrl", imageUrl);
        // Obtener una referencia a la colección "series"
        CollectionReference seriesCollection = db.collection("series");
        // Agregar el documento a la colección
        seriesCollection.add(serie)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar en Firestore
                    Log.d("NuevoElementoFragment", "Documento agregado con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Manejar el fallo al guardar en Firestore
                    Log.e("NuevoElementoFragment", "Error al agregar documento", e);
                });
    }

    // Limpia los campos de entrada después de guardar los datos
    public void borrarCampos() {
        editTextId.setText("");
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextResourceURI.setText("");
        editTextStartYear.setText("");
        editTextEndYear.setText("");
        editTextStoryAvailable.setText("");
        editTextStoryReturned.setText("");
        imageView.setImageResource(R.drawable.ic_add_photo);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        uriSeleccionada = null;
    }
}
