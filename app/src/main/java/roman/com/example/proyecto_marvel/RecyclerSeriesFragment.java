package roman.com.example.proyecto_marvel;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import roman.com.example.proyecto_marvel.databinding.FragmentRecyclerSeriesBinding;
import roman.com.example.proyecto_marvel.databinding.ViewholderSeriesBinding;

public class RecyclerSeriesFragment extends Fragment {

    // Declara un adaptador para el RecyclerView
    SerieAdapter seriesAdapter;
    private FragmentRecyclerSeriesBinding binding;
    // Declara un ViewModel para las series y un NavController para la navegación
    SeriesViewModel seriesViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyclerSeriesBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = binding.recyclerView;
        // Crea un adaptador para el RecyclerView
        seriesAdapter = new SerieAdapter();
        binding.recyclerView.setAdapter(seriesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtiene el ViewModel de las series desde el ViewModelProvider
        seriesViewModel = new ViewModelProvider(requireActivity()).get(SeriesViewModel.class);
        navController = Navigation.findNavController(view);
        // Ejecuta una tarea asíncrona para obtener los datos de la base de datos MySQL y actualizar el RecyclerView
        new ConexionBaseDatos().execute();
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicion = viewHolder.getAdapterPosition();
                Series elemento = seriesAdapter.obtenerElemento(posicion);
                seriesViewModel.eliminar(elemento);
            }
        }).attachToRecyclerView(binding.recyclerView);
        // Observa los cambios en la lista de series y actualiza el adaptador del RecyclerView
        obtenerElementos().observe(getViewLifecycleOwner(), new Observer<List<Series>>() {
            @Override
            public void onChanged(List<Series> series) {
                seriesAdapter.establecerLista(series);
            }
        });
    }

    // Método para obtener la LiveData de la lista de series
    LiveData<List<Series>> obtenerElementos() {
        return seriesViewModel.obtener();
    }
    class SerieAdapter extends RecyclerView.Adapter<SeriesViewHolder> {
        List<Series> series;
        @NonNull
        @Override
        public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SeriesViewHolder(ViewholderSeriesBinding.inflate(getLayoutInflater(), parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
            Series serie = series.get(position);
            holder.titleTextView.setText(serie.getTitle());
            int radioRedondeo = 20;
            Glide.with(holder.itemView.getContext())
                    .load(serie.getUrlImagen())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(radioRedondeo)))
                    .into(holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seriesViewModel.seleccionar(serie);
                    navController.navigate(R.id.action_mostrarSeriesFragment);
                }
            });
        }
        @Override
        public int getItemCount() {
            return series != null ? series.size() : 0;
        }

        // Método para establecer la lista de series en el adaptador
        public void establecerLista(List<Series> series) {
            this.series = series;
            notifyDataSetChanged();
        }

        // Método para obtener un elemento de la lista de series
        public Series obtenerElemento(int posicion) {
            return series.get(posicion);
        }
    }


    // Clase estática para mantener las referencias a las vistas del elemento del RecyclerView
    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderSeriesBinding binding;
        TextView titleTextView;
        ImageView imageView;
        public SeriesViewHolder(ViewholderSeriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
    // Método para establecer la lista de series en el adaptador
    public void setSeriesList(ArrayList<Series> seriesList) {
        if (seriesAdapter != null) {
            seriesAdapter.establecerLista(seriesList);
        }
    }

    // Método para cargar datos en el adaptador del RecyclerView
    public void cargarDatos(ArrayList<Series> seriesList) {
        // Actualiza el adaptador con la nueva lista de series
        setSeriesList(seriesList);
    }

    // Clase interna para ejecutar una tarea asíncrona para obtener datos de la base de datos MySQL
    private class ConexionBaseDatos extends AsyncTask<Void, Void, ArrayList<Series>> {
        @Override
        protected ArrayList<Series> doInBackground(Void... voids) {
            ConexionMySQL conexionMySQL = new ConexionMySQL();
            conexionMySQL.obtenerConexion();
            return conexionMySQL.mostrarTabla("SELECT * FROM Series");
        }

        @Override
        protected void onPostExecute(ArrayList<Series> seriesList) {
            if (seriesList != null) {
                // Guarda los datos en la base de datos local (Room) y actualiza el adaptador del RecyclerView
                guardarDatosEnBaseDeDatosLocal(seriesList);
                // Actualizar el adaptador con la nueva lista de series
                cargarDatos(seriesList);
            } else {
                // Maneja el caso en que la conexión no se pudo establecer
                Toast.makeText(requireActivity(), "No se pudo conectar a la base de datos", Toast.LENGTH_SHORT).show();
            }
        }

        private void guardarDatosEnBaseDeDatosLocal(ArrayList<Series> seriesList) {
            // Itera sobre la lista de series obtenidas y las inserta en la base de datos local (Room) si no existen ya
            for (Series series : seriesList) {
                seriesViewModel.buscarPorId(series.getId()).observeForever(new Observer<List<Series>>() {
                    @Override
                    public void onChanged(List<Series> seriesEnBaseDeDatos) {
                        if (seriesEnBaseDeDatos == null || seriesEnBaseDeDatos.isEmpty()) {
                            // Insertar la serie solo si no existe
                            seriesViewModel.insertar(series);
                        }
                    }
                });
            }
        }
    }
}
