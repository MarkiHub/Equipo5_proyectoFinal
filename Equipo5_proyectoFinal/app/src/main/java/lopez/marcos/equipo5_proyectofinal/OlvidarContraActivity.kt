package lopez.marcos.equipo5_proyectofinal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class OlvidarContraActivity : AppCompatActivity() {

    private lateinit var correoEditText: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvidar)

        correoEditText = findViewById(R.id.correo)
        val reestablecerButton: Button = findViewById(R.id.reestablecer)

        auth = FirebaseAuth.getInstance()

        reestablecerButton.setOnClickListener {
            val correo = correoEditText.text.toString().trim()

            if (correo.isEmpty()) {
                correoEditText.error = "Ingrese su correo electrónico"
                return@setOnClickListener
            }
            
            enviarCorreoRestablecimiento(correo)
        }
    }

    private fun enviarCorreoRestablecimiento(correo: String) {
        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de restablecimiento enviado a $correo", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
    }
}
