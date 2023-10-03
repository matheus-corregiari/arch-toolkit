package br.com.arch.toolkit.sample.github.ui.xml.list.withoutPagination

import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.delegate.viewModelProvider
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.recycler.adapter.SimpleAdapter
import br.com.arch.toolkit.sample.github.R
import br.com.arch.toolkit.sample.github.data.remote.model.RepoDTO
import br.com.arch.toolkit.sample.github.ui.xml.item.RepositoryItemView
import br.com.arch.toolkit.statemachine.ViewStateMachine
import br.com.arch.toolkit.statemachine.config
import br.com.arch.toolkit.statemachine.setup
import br.com.arch.toolkit.statemachine.state

class RepositoryListActivity : AppCompatActivity(R.layout.activity_repository_list) {

    //region Constants
    private val stateLoading = DataResultStatus.LOADING.ordinal
    private val stateError = DataResultStatus.ERROR.ordinal
    private val stateSuccess = DataResultStatus.SUCCESS.ordinal
    private val stateEmpty = DataResultStatus.SUCCESS.ordinal + 1

    private val saveStateMachine = "SAVE_STATE_MACHINE"
    //endregion

    private val viewModel: RepositoryListViewModel by viewModelProvider()
    private val stateMachine = ViewStateMachine()
    private val adapter = SimpleAdapter(::RepositoryItemView).withListener(::onItemClick)

    //region Views
    private val rootContent: ViewGroup by viewProvider(R.id.root_content)
    private val successView: RecyclerView by viewProvider(R.id.success_view)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStateMachine(savedInstanceState?.getBundle(saveStateMachine))
        successView.adapter = adapter

        viewModel.listLiveData().observe(this) {
            showLoading { stateMachine.changeState(stateLoading) }
            error { _ -> stateMachine.changeState(stateError) }
            data { page ->
                if (page.items.isEmpty()) {
                    throw error("") /* Fazer funcionaaar */
                    stateMachine.changeState(stateEmpty)
                } else {
                    stateMachine.changeState(stateSuccess)
                }
                adapter.setList((page.items))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(saveStateMachine, stateMachine.saveInstanceState())
    }

    private fun onItemClick(model: RepoDTO) = Unit

    private fun setupStateMachine(savedInstanceState: Bundle?) = stateMachine.setup {

        restoreInstanceState(savedInstanceState)

        config {
            setOnChangeState {
                TransitionManager.endTransitions(rootContent)
                TransitionManager.beginDelayedTransition(rootContent)
            }
        }

        state(stateLoading) {
            root(rootContent)
            visibles(R.id.loading_view)
            gones(R.id.success_view, R.id.error_view, R.id.empty_view)
        }
        state(stateError) {
            root(rootContent)
            visibles(R.id.error_view)
            gones(R.id.success_view, R.id.loading_view, R.id.empty_view)
        }
        state(stateSuccess) {
            root(rootContent)
            visibles(R.id.success_view)
            gones(R.id.loading_view, R.id.error_view, R.id.empty_view)
        }
        state(stateEmpty) {
            root(rootContent)
            visibles(R.id.empty_view)
            gones(R.id.success_view, R.id.error_view, R.id.loading_view)
        }
    }

}