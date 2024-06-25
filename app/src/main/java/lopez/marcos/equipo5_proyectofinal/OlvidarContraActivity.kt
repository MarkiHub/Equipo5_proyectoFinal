package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import lopez.marcos.equipo5_proyectofinal.ui.theme.Equipo5_proyectoFinalTheme

class OlvidarContraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvidar)

        val button_reestablecer: Button = findViewById(R.id.reestablecer)

        button_reestablecer.setOnClickListener {
            var intento: Intent = Intent(this, MainActivity::class.java)
            startActivity(intento)
        }
    }
}