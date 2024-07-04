package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActiviy : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.correo)
        passwordEditText = findViewById(R.id.contrasena)
        confirmPasswordEditText = findViewById(R.id.rcontrasena)
        nameEditText = findViewById(R.id.nombre)
        registerButton = findViewById(R.id.registrar)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

            if (validateInputs(email, password, confirmPassword, name)) {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = hashMapOf(
                                    "nombre" to name,
                                    "correo" to email
                                )

                                firestore.collection("usuarios").document(auth.currentUser?.uid!!)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(baseContext, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, NavActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(baseContext, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(baseContext, "Error en la autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String, name: String): Boolean {
        if (name.isEmpty()) {
            nameEditText.error = "El nombre es obligatorio"
            return false
        }

        if (email.isEmpty()) {
            emailEditText.error = "El correo es obligatorio"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Correo no válido"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "La contraseña es obligatoria"
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirmar contraseña es obligatorio"
            return false
        }

        return true
    }
}
