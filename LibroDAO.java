package _Bibliotecapp.db;

import _Bibliotecapp.modelo.Libro;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class LibroDAO {

    private MongoCollection<Document> coleccion;

    public LibroDAO() {
    	 String uri = "mongodb+srv://adminBiblioteca11:<Password>@bibliotecapp.e8itjpk.mongodb.net/?appName=Bibliotecapp";
        
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("bibliotecapp_db");
            this.coleccion = database.getCollection("libros");
            System.out.println("Conectado a MongoDB Atlas");
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    // 1. INSERTAR: Guarda todos los campos, incluidos los nuevos
    public void insertar(Libro libro) {
        Document doc = new Document("referencia", libro.getReferencia()) 
                .append("titulo", libro.getTitulo())
                .append("autor", libro.getAutor())
                .append("isbn", libro.getIsbn())
                .append("prestado", libro.isPrestado())
                .append("usuarioPrestamo", libro.getUsuarioPrestamo())
                .append("telefonoPrestamo", libro.getTelefonoPrestamo()) 
                .append("fechaPrestamo", libro.getFechaPrestamo())
                .append("vecesPrestado", libro.getVecesPrestado());
        		coleccion.insertOne(doc);
    }

    // 2. OBTENER TODOS: Lee los datos de la nube y los convierte en objetos Libro
    public List<Libro> obtenerTodos() {
        List<Libro> lista = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            Libro l = new Libro();
            l.setId(doc.getObjectId("_id"));
            l.setReferencia(doc.getString("referencia")); 
            l.setTitulo(doc.getString("titulo"));
            l.setAutor(doc.getString("autor"));
            l.setIsbn(doc.getString("isbn"));
            l.setPrestado(doc.getBoolean("prestado", false));
            l.setUsuarioPrestamo(doc.getString("usuarioPrestamo"));
            l.setTelefonoPrestamo(doc.getString("telefonoPrestamo")); 
            l.setFechaPrestamo(doc.getString("fechaPrestamo"));
            l.setVecesPrestado(doc.getInteger("vecesPrestado", 0));
            lista.add(l);
        }
        return lista;
    }

    // 3. ACTUALIZAR: El método que usa handleCambiarEstado para guardar los cambios
    public void actualizar(Libro libro) {
        Bson filtro = eq("_id", libro.getId());
        Bson cambios = Updates.combine(
                Updates.set("prestado", libro.isPrestado()),
                Updates.set("usuarioPrestamo", libro.getUsuarioPrestamo()),
                Updates.set("telefonoPrestamo", libro.getTelefonoPrestamo()), 
                Updates.set("fechaPrestamo", libro.getFechaPrestamo()),
                Updates.set("vecesPrestado", libro.getVecesPrestado())
        );
        coleccion.updateOne(filtro, cambios);
    }

    // 4. BORRAR: Función de eliminar un libro
    public void eliminar(ObjectId id) {
        coleccion.deleteOne(eq("_id", id));
        System.out.println("Libro eliminado de la nube.");
    }
}
