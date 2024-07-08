package br.com.igorbag.githubsearch.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.databinding.ActivityMainBinding
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gitHubService: GitHubService
    private var API_BASE_URL = "https://api.github.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupListeners()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        binding.btnConfirmar.setOnClickListener {
            saveUserLocal(binding.etNomeUsuario.text.toString())
            getAllReposByUserName()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(nomeUsuario: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putString(getString(R.string.saved_nome_usuario),nomeUsuario)
            apply()
        }
    }

    private fun showUserName() {
        val sharredPref = getPreferences(Context.MODE_PRIVATE)
        val usuarioSalvo = sharredPref.getString(getString(R.string.saved_nome_usuario),"")
        binding.etNomeUsuario.setText(usuarioSalvo)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        if(binding.etNomeUsuario.text != null) {
            gitHubService.getAllRepositoriesByUser(binding.etNomeUsuario.text.toString())
                .enqueue(object : Callback<List<Repository>> {
                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                setupAdapter(it)
                            }
                        } else {
                            Toast.makeText(baseContext, R.string.usuario_nao_localizado, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(p0: Call<List<Repository>>, p1: Throwable) {
                        Toast.makeText(baseContext, R.string.response_error, Toast.LENGTH_LONG)
                            .show()
                    }

                })
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapterRepositorys = RepositoryAdapter(list)
        binding.rvListaRepositories.apply {
            adapter = adapterRepositorys
        }
        adapterRepositorys.repositoryItemCompartilharLister = {
            shareRepositoryLink(it.htmlUrl)
        }
        adapterRepositorys.repositoryItemLister = {
            openBrowser(it.htmlUrl)
        }
    }

    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}