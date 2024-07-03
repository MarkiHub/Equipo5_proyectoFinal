package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import lopez.marcos.equipo5_proyectofinal.NavActivity
import lopez.marcos.equipo5_proyectofinal.OlvidarContraActivity
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.RegistroActiviy

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var button_is: Button
    private lateinit var button_registrar: Button
    private lateinit var button_olvide: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()

        button_is = findViewById(R.id.iniciar_sesion)
        button_registrar = findViewById(R.id.registrar_is)
        button_olvide = findViewById(R.id.olvidecontrasena)

        emailEditText = findViewById(R.id.correo)
        passwordEditText = findViewById(R.id.contrasena)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, NavActivity::class.java))
            finish()
        }

        button_is.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, NavActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(baseContext, "Nombre y correo no encontrado.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(baseContext, "Campos vacios.",
                    Toast.LENGTH_SHORT).show()
            }
        }



        button_registrar.setOnClickListener {
            val intento = Intent(this, RegistroActiviy::class.java)
            startActivity(intento)
        }

        button_olvide.setOnClickListener {
            val intento = Intent(this, OlvidarContraActivity::class.java)
            startActivity(intento)
        }
    }

    private fun comprobarSesion(){
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, NavActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        comprobarSesion()
        super.onStart()
    }
}
