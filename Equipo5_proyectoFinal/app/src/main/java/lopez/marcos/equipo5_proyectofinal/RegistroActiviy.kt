package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class RegistroActiviy : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val button_re: Button = findViewById(R.id.registrar)

        button_re.setOnClickListener {
            var intento: Intent = Intent(this,MainActivity::class.java)
            startActivity(intento)
        }
    }
}
