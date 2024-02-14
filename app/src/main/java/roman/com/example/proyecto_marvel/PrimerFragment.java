package roman.com.example.proyecto_marvel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrimerFragment extends Fragment {
    // Se declara un botón y un NavController para la navegación
    Button botonSiguiente;
    NavController navController;

    // Declara un objeto SharedPreferences para almacenar preferencias compartidas entre fragmentos
    SharedPreferences sharedPreferences;

    // Declara un objeto SharedPreferences para almacenar preferencias compartidas entre fragmentos
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtiene las preferencias compartidas de la actividad asociada al fragmento
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            // Si no es la primera vez que se ejecuta, navega directamente al siguiente fragmento
            NavHostFragment.findNavController(this).navigate(R.id.action_primerFragment_to_recyclerSeriesFragment);
            return null;
        }
        // Si es la primera vez que se ejecuta la aplicación, infla el diseño del fragmento
        return inflater.inflate(R.layout.fragment_primer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Oculta la barra de navegación inferior
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);

        navController = Navigation.findNavController(view);

        botonSiguiente = view.findViewById(R.id.botonSiguiente);

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_primerFragment_to_recyclerSeriesFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Vuelve a mostrar la barra de navegación inferior
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.VISIBLE);
        // Guarda el valor en las preferencias compartidas cuando el fragmento se destruye
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstRun", false);
        editor.apply();
    }
}
