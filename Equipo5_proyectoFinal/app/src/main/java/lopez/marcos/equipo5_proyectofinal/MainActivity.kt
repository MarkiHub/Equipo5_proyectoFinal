package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import lopez.marcos.equipo5_proyectofinal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val button_is: Button = findViewById(R.id.iniciar_sesion)
        val button_registrar: Button = findViewById(R.id.registrar_is)
        val button_olvide: TextView = findViewById(R.id.olvidecontrasena)

        button_is.setOnClickListener {
            var intento: Intent = Intent(this, NavActivity::class.java)
            this.startActivity(intento)
        }

    }
}