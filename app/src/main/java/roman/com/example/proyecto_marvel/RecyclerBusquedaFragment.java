package roman.com.example.proyecto_marvel;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecyclerBusquedaFragment extends RecyclerSeriesFragment {
    // Se sobrescribe el método para obtener los elementos a mostrar en el RecyclerView
    @Override
    LiveData<List<Series>> obtenerElementos() {
        // Se llama al método buscar() del ViewModel para obtener una LiveData de la lista de series
        return seriesViewModel.buscar();
    }
}

