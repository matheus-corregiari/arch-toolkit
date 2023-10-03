package br.com.arch.toolkit.sample.github.ui.xml.item

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import br.com.arch.toolkit.recycler.adapter.ViewBinder
import br.com.arch.toolkit.sample.github.data.remote.model.RepoDTO

class RepositoryItemView(context: Context) : AppCompatTextView(context), ViewBinder<RepoDTO> {
    override fun bind(model: RepoDTO) {
        text = model.name
    }
}