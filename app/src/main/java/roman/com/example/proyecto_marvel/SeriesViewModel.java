package roman.com.example.proyecto_marvel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class SeriesViewModel extends AndroidViewModel {
    SeriesRepositorio seriesRepositorio;
    MutableLiveData<Series> seriesSeleccionado = new MutableLiveData<>();

    public SeriesViewModel(@NonNull Application application) {
        super(application);
        seriesRepositorio = new SeriesRepositorio(application);
    }

    MutableLiveData<String> terminoBusqueda = new MutableLiveData<>();

    LiveData<List<Series>> resultadoBusqueda = Transformations.switchMap(terminoBusqueda, input ->
            seriesRepositorio.buscar(input)
    );

    //  Retorna LiveData para buscar una serie por su ID
    public LiveData<List<Series>> buscarPorId(int serieId) {
        return seriesRepositorio.buscarPorId(serieId);
    }

    //  Retorna LiveData para obtener el resultado de la búsqueda de acuerdo con el término de búsqueda actual
    LiveData<List<Series>> buscar() {
        return resultadoBusqueda;
    }

    // Retorna LiveData para obtener todas las series
    LiveData<List<Series>> obtener() {
        return seriesRepositorio.obtener();
    }

    // Inserta una serie en el repositorio
    void insertar(Series elemento) {
        seriesRepositorio.insertar(elemento);
    }

    // Elimina una serie del repositorio
    void eliminar(Series elemento) {
        seriesRepositorio.eliminar(elemento);
    }

    // Establece el término de búsqueda para la búsqueda de series
    void establecerTerminoBusqueda(String t) {
        terminoBusqueda.setValue(t);
    }

    //  Establece la serie seleccionada por el usuario.
    void seleccionar(Series series) {
        seriesSeleccionado.setValue(series);
    }

    // Retorna LiveData para obtener la serie seleccionada.
    MutableLiveData<Series> seleccionado() {
        return seriesSeleccionado;
    }
}

