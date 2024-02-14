package roman.com.example.proyecto_marvel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

@Database(entities = {Series.class}, version = 1, exportSchema = false)
public abstract class SeriesBaseDeDatos extends RoomDatabase {

    // Instancia única de la base de datos
    private static volatile SeriesBaseDeDatos INSTANCIA;

    static SeriesBaseDeDatos obtenerInstancia(final Context context) {
        if (INSTANCIA == null) {
            synchronized (SeriesBaseDeDatos.class) {
                // Se crea la instancia de la base de datos utilizando Room.databaseBuilder()
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(context,
                                    SeriesBaseDeDatos.class, "series.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCIA;
    }
    /**
     * Método abstracto para obtener el DAO (Data Access Object) de las series.
     * Room utilizará este método para proporcionar una implementación del DAO.
     */
    public abstract SeriesDao obtenerSeriesDao();

    @Dao
    interface SeriesDao {
        @Query("SELECT * FROM Series")
        LiveData<List<Series>> obtener();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertar(Series elemento);

        @Query("SELECT * FROM Series WHERE id = :serieId")
        LiveData<List<Series>> buscarPorId(int serieId);

        @Update
        void actualizar(Series elemento);

        @Delete
        void eliminar(Series elemento);

        @Query("SELECT * FROM Series WHERE id LIKE '%' || :t || '%' OR title LIKE '%' || :t || '%'")
        LiveData<List<Series>> buscar(String t);

    }
}