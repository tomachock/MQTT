package com.example.mqtt;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivityFirebase extends AppCompatActivity {

    // Elementos del diseño
    public EditText txtRut, txtNombre, txtDireccion, txtCodigo;
    private Spinner spMedicamentos;
    private ListView lista;

    // Firebase Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firebase);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular elementos del diseño
        txtRut = findViewById(R.id.txtCodigo); // Campo para el RUT
        txtNombre = findViewById(R.id.txtNombre); // Campo para el Nombre
        txtDireccion = findViewById(R.id.txtDireccion); // Campo para la Dirección
        spMedicamentos = findViewById(R.id.spMedicamentos); // Spinner para los Medicamentos
        lista = findViewById(R.id.lista); // ListView para mostrar datos


        // Configurar el Spinner con medicamentos
        configurarSpinner();
    }

    // Método para configurar el Spinner con datos de ejemplo
    private void configurarSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Paracetamol", "Ibuprofeno", "Amoxicilina"} // Datos estáticos
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMedicamentos.setAdapter(spinnerAdapter);
    }

    // Método para enviar datos a Firestore
    public void enviarDatosFirestore(View view) {
        String Rut = txtRut.getText().toString().trim();
        String nombre = txtNombre.getText().toString().trim();
        String medicamento = spMedicamentos.getSelectedItem().toString();
        String direccion = txtDireccion.getText().toString().trim();

        if (Rut.isEmpty() || nombre.isEmpty() ||  direccion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos
        Map<String, Object> farmacia = new HashMap<>();
        farmacia.put("Rut", Rut);
        farmacia.put("nombre", nombre);
        farmacia.put("medicamento", medicamento);
        farmacia.put("direccion", direccion);

        // Guardar los datos en Firestore
        db.collection("farmacias")
                .add(farmacia)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(MainActivityFirebase.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivityFirebase.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Método para cargar datos desde Firestore
    public void cargarLista(View view) {
        CollectionReference farmaciasRef = db.collection("farmacias");

        farmaciasRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> farmaciasList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("nombre");
                                String medicamento = document.getString("medicamento");

                                // Verifica si los datos son nulos
                                if (nombre == null || medicamento == null) {
                                    Log.w("Firestore", "Documento no tiene los campos 'nombre' o 'medicamento'.");
                                } else {
                                    farmaciasList.add(nombre + " - " + medicamento);
                                }
                            }

                            // Mostrar datos en el ListView
                            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                                    MainActivityFirebase.this,
                                    android.R.layout.simple_list_item_1,
                                    farmaciasList
                            );
                            lista.setAdapter(listAdapter);
                        } else {
                            Log.e("Firestore", "Error al cargar los datos", task.getException());
                            Toast.makeText(MainActivityFirebase.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        }

                    }

                });

    }
}