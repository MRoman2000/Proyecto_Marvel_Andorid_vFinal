package roman.com.example.proyecto_marvel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import roman.com.example.proyecto_marvel.databinding.FragmentMostrarSeriesBinding;

public class MostrarSeriesFragment extends Fragment {
    // Se declara un objeto binding para acceder a las vistas del fragmento
    private FragmentMostrarSeriesBinding binding;
    // Se declara un objeto NavController para navegar entre destinos de la app
    NavController navController;

    // Método llamado cuando se crea la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentMostrarSeriesBinding.inflate(inflater, container, false)).getRoot();
    }

    // Método llamado cuando la vista del fragmento ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        // Se obtiene el ViewModel de las series desde el ViewModelProvider
        SeriesViewModel seriesViewModel = new ViewModelProvider(requireActivity()).get(SeriesViewModel.class);
        // Se observa el LiveData del ViewModel para actualizar las vistas cuando cambian los datos de la serie seleccionada
        seriesViewModel.seleccionado().observe(getViewLifecycleOwner(), new Observer<Series>() {
            @Override
            public void onChanged(Series series) {
                // Se actualizan las vistas con los datos de la serie
                binding.idSerie.setText(String.valueOf(series.id));
                binding.descripcion.setText(String.valueOf(series.description));
                binding.starYear.setText(String.valueOf(series.startYear));
                binding.endYear.setText(String.valueOf(series.endYear));
                binding.title.setText(String.valueOf(series.title));
                binding.resourceURI.setText(String.valueOf(series.resourceURI));
                binding.available.setText(String.valueOf(series.storyAvailable));
                binding.returned.setText(String.valueOf(series.storyReturned));
                int radioRedondeo = 25;
                // Se carga la imagen utilizando Glide y se aplica un redondeo a las esquinas
                Glide.with(requireContext())
                        .load(series.urlImagen)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(radioRedondeo)))
                        .into(binding.image);
            }
        });
    }
}