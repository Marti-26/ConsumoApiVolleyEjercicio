package ue.edu.co.consumoapivolley;

import org.json.JSONException;
import org.json.JSONObject;

// Modelo que representa un Post obtenido de la API
public class Post {
    //declaracion de atributos
    private final int userId;
    private final int id;
    private final String title;
    private final String body;

    // Constructor para inicializar los datos del post
    public Post(int userId, int id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    // Convierte los datos recibidos de la API en un objeto Post
    public static Post consumirApiPost(JSONObject json) throws JSONException {
        int userId = json.getInt("userId");
        int id = json.getInt("id");
        String title = json.getString("title");
        String body = json.getString("body");
        return new Post(userId, id, title, body);
    }
    //metodos getter y setter
    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    // Muestra la información del post en formato de texto
    @Override
    public String toString() {
        return "UserId: " + userId
                + "\nID: " + id
                + "\nTitulo: " + title
                + "\nBody: " + body;
    }
}
