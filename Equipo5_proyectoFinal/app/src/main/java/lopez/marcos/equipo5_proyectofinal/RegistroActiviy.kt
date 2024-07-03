package lopez.marcos.equipo5_proyectofinal

import android.content.Intent
import android.os.Bundle
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
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val name = nameEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && name.isNotEmpty()) {
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
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
