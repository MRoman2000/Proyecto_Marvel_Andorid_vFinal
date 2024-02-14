package roman.com.example.proyecto_marvel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SeriesRepositorio {

    // Executor para ejecutar operaciones en un subproceso único
    Executor executor = Executors.newSingleThreadExecutor();
    // DAO (Data Access Object) para las series
    SeriesBaseDeDatos.SeriesDao seriesDao;

    // Constructor que inicializa el DAO utilizando la instancia de la base de datos proporcionada por Room
    SeriesRepositorio(Application application) {
        seriesDao = SeriesBaseDeDatos.obtenerInstancia(application).obtenerSeriesDao();
    }

    // Retorna una lista observable de Series que coinciden con el término de búsqueda proporcionado.
    LiveData<List<Series>> buscar(String t) {
        return seriesDao.buscar(t);
    }

    // Retorna una lista observable de todas las series en la base de datos
    LiveData<List<Series>> obtener() {
        return seriesDao.obtener();
    }

    // Inserta una serie en la base de datos. Antes de la inserción, verifica si ya existe una serie con el mismo ID
    void insertar(Series elemento) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Verificar si ya existe un objeto con el mismo ID
                LiveData<List<Series>> seriesExistente = seriesDao.buscarPorId(elemento.getId());
                if (seriesExistente.getValue() != null && !seriesExistente.getValue().isEmpty()) {
                    // Objeto ya existe, entonces actualiza en lugar de insertar
                    seriesDao.actualizar(elemento);
                } else {
                    // Objeto no existe, realiza la inserción
                    seriesDao.insertar(elemento);
                }
            }
        });
    }
    //  Elimina una serie de la base de datos
    void eliminar(Series elemento) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                seriesDao.eliminar(elemento);
            }
        });
    }
    // Actualiza una serie en la base de datos
    public void actualizar(Series series) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                seriesDao.actualizar(series);
            }
        });
    }

    public LiveData<List<Series>> buscarPorId(int serieId) {
        return seriesDao.buscarPorId(serieId);
    }

}
