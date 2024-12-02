package com.example.mqtt;

import static android.os.Build.VERSION_CODES.S;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//Librerias de mqtt t Formulario
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    // Variables de la Conexión a MQTT
    private static String mqttHost = "tcp://clearrider488.cloud.shiftr.io:1883"; // IP del Servidor MQTT
    private static String IdUsuario = "AppAndroid"; // Nombre del dispositivo que se conectará
    private static String SubirMensaje = "Mensaje"; // Tópico al que se suscribirá
    private static String User = "clearrider488"; // Usuario
    private static String Pass = "JXnGGU2bxPRcsxs9"; // Contraseña o Token

    // Variable que se utilizará para imprimir los datos del sensor
    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;

    // Librería MQTT
    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enlace de la variable del ID que está en el activity_main donde imprimiremos los datos
        textView = findViewById(R.id.txtView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.Button);

        try {
            // Creación de un Cliente MQTT
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());

            // Conexión al servidor MQTT
            mqttClient.connect(options);

            // Si se conecta, imprimirá un mensaje de MQTT
            Toast.makeText(this, "Aplicación conectada al Servidor MQTT", Toast.LENGTH_SHORT).show();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT","Conexion Perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT","Entrega Completa");

                }
            });

            // Manejo de entrega de datos y pérdida de conexión

        } catch (MqttException e) {
            e.printStackTrace();
        }
// Al dar click en el button enviar el mensaje del topico
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el mensaje ingresado por el usuario
                String mensaje = editTextMessage.getText().toString();
                try {
                    // Verifica si la conexión MQTT está activa
                    if (mqttClient != null && mqttClient.isConnected()) {
                        // Publicar el mensaje en el tópico especificado
                        mqttClient.publish(SubirMensaje, mensaje.getBytes(), 0, false);
                        // Mostrar el mensaje enviado en el TextView
                        textView.append("\n- " + mensaje);
                        Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error: No se pudo enviar el mensaje. La conexión MQTT no está activa.", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        // Configurar botón para navegar a VideoActivity
        findViewById(R.id.btnIrAVideo).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivityFirebase.class);
            startActivity(intent);
        });



    }
        }


