package roman.com.example.proyecto_marvel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import roman.com.example.proyecto_marvel.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private SeriesViewModel elementosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());
        elementosViewModel = new ViewModelProvider(this).get(SeriesViewModel.class);
        // Se configura la navegación con el controlador NavController y se vincula con el BottomNavigationView
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.recyclerBusquedaFragment) {
                    binding.searchView.setVisibility(View.VISIBLE);
                    //  binding.searchView.setIconified(false);
                    binding.searchView.requestFocusFromTouch();
                    // Oculta la barra de navegación cuando el teclado está abierto
                    binding.bottomNavView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            binding.bottomNavView.getWindowVisibleDisplayFrame(r);
                            int screenHeight = binding.bottomNavView.getRootView().getHeight();
                            int keypadHeight = screenHeight - r.bottom;
                            // Si la altura del teclado es mayor que el 15% de la pantalla, se considera abierto
                            if (keypadHeight > screenHeight * 0.15) {
                                binding.bottomNavView.setVisibility(View.GONE);
                            } else {
                                binding.bottomNavView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    binding.searchView.setVisibility(View.GONE);
                }

                if (destination.getId() == R.id.agregarSerieFragment) {
                    // Oculta la barra de navegación cuando el teclado está abierto
                    binding.bottomNavView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            binding.bottomNavView.getWindowVisibleDisplayFrame(r);
                            int screenHeight = binding.bottomNavView.getRootView().getHeight();
                            // r.bottom es la posición por encima del teclado
                            int keypadHeight = screenHeight - r.bottom;
                            // Si la altura del teclado es mayor que el 15% de la pantalla, se considera abierto
                            if (keypadHeight > screenHeight * 0.15) {
                                binding.bottomNavView.setVisibility(View.GONE);
                            } else {
                                binding.bottomNavView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                if (destination.getId() == R.id.recyclerBusquedaFragment) {
                    binding.searchView.setVisibility(View.VISIBLE);
                    //      binding.searchView.setIconified(false);
                    binding.searchView.requestFocusFromTouch();
                    binding.bottomNavView.setVisibility(View.VISIBLE);
                } else {
                    binding.searchView.setVisibility(View.GONE);
                }
            }
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                elementosViewModel.establecerTerminoBusqueda(newText);
                return false;
            }
        });
    }
}
