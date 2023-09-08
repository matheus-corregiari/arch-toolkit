package br.com.arch.toolkit.sample.github.ui.xml.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewModelProvider
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.github.R

class RepositoryListActivity : AppCompatActivity(R.layout.activity_repository_list) {

    private val viewModel: RepositoryListViewModel by viewModelProvider()

    //region Views
    private val rootContent: ViewGroup by viewProvider(R.id.root_content)
    private val loadingView: View by viewProvider(R.id.loading_view)
    private val successView: View by viewProvider(R.id.success_view)
    private val errorView: View by viewProvider(R.id.error_view)
    private val emptyView: View by viewProvider(R.id.empty_view)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}