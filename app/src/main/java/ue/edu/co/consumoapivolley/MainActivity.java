package ue.edu.co.consumoapivolley;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// Componentes de la librería Volley, para hacer peticiones HTTP
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Declaramos de atributos
    private static final String URL_API = "https://jsonplaceholder.typicode.com/posts"; // Endpoint  a consumir
    private static final String REQUEST_TAG = "GET_POSTS"; // "Etiqueta" para identificar esta petición dentro de la cola
    private ListView lvTodos;
    private ProgressBar progressBar; // Círculo de carga
    private TextView tvEstado;
    private TextView tvContador;
    private RequestQueue requestQueue; // Cola de peticiones de Volley (se necesita para poder disparar cualquier request)
    // Lista donde vamos a guardar los objetos Post ya construidos a partir del JSON.
    private final List<Post> listaPosts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarVistas();
        requestQueue = Volley.newRequestQueue(getApplicationContext()); // Crea la cola de peticiones de Volley
        consumirApi(); // Dispara la petición HTTP
    }

    // el Metodo recibe un boolean y decide si mostrar el ProgressBar
    // (mientras carga) o el ListView (cuando ya hay datos).
    private void mostrarCargando(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE); // Si eta cargando muestra el circulo si no lo oculta
        lvTodos.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }

    //metodo para manejar errores en el llamado
    private void mostrarError(String mensaje) {
        mostrarCargando(false); // Deja de mostrar el círculo de carga
        tvEstado.setText(mensaje);
        tvEstado.setVisibility(View.VISIBLE);
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    // Se llama  cuando Volley falla en la comunicación con el servidor
    private void procesarError(VolleyError error) {
        String mensaje = error.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "Verifique la conexcion a internet.";
        }
        mostrarError("Error en la solicitud: " + mensaje);
    }

    // arma y dispara la petición GET hacia JSONPlaceholder.
    private void consumirApi() {
        mostrarCargando(true); // Muestra el círculo de carga mientras esperamos la respuesta
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null, // Cuerpo de la petición: null porque un GET no envía body
                //caundo la api rsponde...
                response -> {
                    listaPosts.clear();
                    try {
                        // Recorremos cada elemento del JSONArray
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            Post post = Post.consumirApiPost(item);
                            listaPosts.add(post); // Agregamos el objeto ya construido a la lista
                        }
                        // SE CREA el adaptador que conecta la lista de objetos Post con el ListView.
                        ArrayAdapter<Post> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                listaPosts
                        );
                        lvTodos.setAdapter(adapter);
                        mostrarCargando(false);
                        tvEstado.setVisibility(View.GONE);

                        // Actualiza y muestra el contador de registros recibidos
                        tvContador.setText("Se recibieron " + listaPosts.size() + " registros.");
                        tvContador.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        mostrarError("No fue posible procesar la respuesta");
                    }
                },
                this::procesarError
        );
        request.setTag(REQUEST_TAG); // Etiqueta la petición (permite identificarla/cancelarla si fuera necesario)
        requestQueue.add(request); // Agrega la petición a la cola
    }


    private void inicializarVistas() {
        lvTodos = findViewById(R.id.lvTodos);
        progressBar = findViewById(R.id.progressBar);
        tvEstado = findViewById(R.id.tvEstado);
        tvContador = findViewById(R.id.tvContador);
    }
}