package roman.com.example.proyecto_marvel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConexionMySQL {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    // Este método establece la conexión con la base de datos
    public Connection obtenerConexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://monorail.proxy.rlwy.net:34858/railway";
            String user = "root";
            String password = "DbGfcB5c63FF132AaB5af23G5c6gh5ab";
            con = DriverManager.getConnection(url, user, password);
            if (con != null) {
                System.out.println("Conexión exitosa");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return con;
    }
    // Toma una consulta SQL como entrada y devuelve una lista de objetos de tipo Series
    public ArrayList<Series> mostrarTabla(String consulta) {
        ArrayList<Series> seriesList = new ArrayList<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery(consulta);
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String resourceURI = rs.getString("resourceURI");
                int startYear = rs.getInt("startYear");
                int endYear = rs.getInt("endYear");
                String urlImagen = rs.getString("urlImagen");
                int storyAvailable = rs.getInt("storyAvailable");
                int storyReturned = rs.getInt("storyReturned");
                Series serie = new Series(id, title, description, resourceURI, startYear, endYear, urlImagen, storyAvailable, storyReturned);
                seriesList.add(serie);
            }
            cerrarConexion();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return seriesList;
    }

    // Toma una consulta SQL como entrada y la ejecuta
    public void insertarDatos(String consulta) {
        try {
            try (PreparedStatement ps = con.prepareStatement(consulta)) {
                // Ejecutar la consulta
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta: " + e.getMessage());
        }
    }
    //  Cierra todos los recursos utilizados para la conexión a la base de datos
    public void cerrarConexion() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR al cerrar la conexión: " + e.getMessage());
        }
    }
}
