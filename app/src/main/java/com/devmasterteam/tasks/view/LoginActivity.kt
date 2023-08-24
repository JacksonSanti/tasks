package com.devmasterteam.tasks.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityLoginBinding
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Layout
        setContentView(binding.root)
        supportActionBar?.hide()

        // Eventos
        binding.buttonLogin.setOnClickListener(this)
        binding.textRegister.setOnClickListener(this)

        viewModel.verifyAuthentication()

        // Observadores
        observe()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View) {
        if(v.id == R.id.button_login) {
            handleLogin()
        } else if (v.id == R.id.text_register) {
            startActivity(Intent(this, RegisterActivity::class.java ))
        }

    }

    private fun observe() {
        viewModel.login.observe(this) {
            if(it.status()){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, it.message(), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loggedUser.observe(this){
            if (it) {
                biometricAuthentication()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun biometricAuthentication() {

        val executor = ContextCompat.getMainExecutor(this)
        val bio = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Titulo")
            .setSubtitle("Sub Titulo")
            .setDescription("descrição")
            .setNegativeButtonText("Cancelar")
            .build()

        bio.authenticate(info)


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleLogin() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        viewModel.doLogin(email, password)

    }

}