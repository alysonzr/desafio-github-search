package br.com.igorbag.githubsearch.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.databinding.RepositoryItemBinding
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val repositories: List<Repository>
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repositoryItemCompartilharLister: (Repository) -> Unit = {}
    var repositoryItemLister: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        val binding = RepositoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    // Pega a quantidade de repositorios da lista
    override fun getItemCount() = repositories.size

    open inner class ViewHolder(
        private val binding: RepositoryItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repository : Repository) {
            binding.tvNomeRepositorio.text = repository.name

            binding.ivCompartilhar.setOnClickListener {
                repositoryItemCompartilharLister(repository)
            }
            binding.clCardContent.setOnClickListener {
                repositoryItemLister(repository)
            }

        }

    }


}


