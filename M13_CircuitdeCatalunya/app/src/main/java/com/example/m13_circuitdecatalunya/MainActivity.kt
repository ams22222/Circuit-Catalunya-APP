package com.example.m13_circuitdecatalunya

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.m13_circuitdecatalunya.databinding.ActivityMainBinding
import com.example.m13_circuitdecatalunya.fragments.MyViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Para trabajar con ViewBindig
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val btnHome= binding.btnIni

        /*val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
        finish()
        */
        btnHome.setOnClickListener{
            val email = binding.edtMail.text.toString()

            if (email.isEmpty()) {
                // Mostrar un mensaje de error al usuario
                Toast.makeText(this, "El campo de email no puede estar vacío", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                // Mostrar un mensaje de error al usuario
                Toast.makeText(this, "El formato del correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            } else {
                val editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit()
                editor.putString("email", email)
                editor.apply()

                val i = Intent(this, HomeActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
