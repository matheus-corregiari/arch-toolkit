package br.com.arch.toolkit.sample.playground

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.playground.recyclerAdapter.ChangingListActivity
import br.com.arch.toolkit.sample.playground.recyclerAdapter.MultipleViewTypesActivity
import br.com.arch.toolkit.sample.playground.recyclerAdapter.SimpleListActivity
import br.com.arch.toolkit.sample.playground.recyclerAdapter.StickyHeadersActivity
import br.com.arch.toolkit.sample.playground.statemachine.SceneStateMachineExampleActivity
import br.com.arch.toolkit.sample.playground.statemachine.ViewStateMachineExampleActivity
import br.com.arch.toolkit.sample.playground.storage.StorageSampleActivity
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    // Recycler Adapter
    private val simpleListButton: Button by viewProvider(R.id.bt_simple_list_example)
    private val multipleViewButton: Button by viewProvider(R.id.bt_multiple_view_types_example)
    private val stickyHeadersButton: Button by viewProvider(R.id.bt_sticky_headers_example)
    private val changingListButton: Button by viewProvider(R.id.bt_changing_list_example)

    // State Machine
    private val viewStateMachineExampleButton: Button by viewProvider(R.id.bt_view_state_machine_example)
    private val sceneStateMachineExampleButton: Button by viewProvider(R.id.bt_scene_state_machine_example)

    // State Machine
    private val viewStorageExampleButton: Button by viewProvider(R.id.bt_view_storage_example)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        // Recycler Adapter
        setupRecyclerAdapterSamples()

        // State Machine
        setupStateMachineSamples()

        // Storage
        setupStorageSamples()
    }

    private fun setupRecyclerAdapterSamples() {
        simpleListButton.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        multipleViewButton.setOnClickListener {
            startActivity(Intent(this, MultipleViewTypesActivity::class.java))
        }

        stickyHeadersButton.setOnClickListener {
            startActivity(Intent(this, StickyHeadersActivity::class.java))
        }

        changingListButton.setOnClickListener {
            startActivity(Intent(this, ChangingListActivity::class.java))
        }
    }

    private fun setupStateMachineSamples() {
        viewStateMachineExampleButton.setOnClickListener {
            startActivity(Intent(this, ViewStateMachineExampleActivity::class.java))
        }

        sceneStateMachineExampleButton.setOnClickListener {
            startActivity(Intent(this, SceneStateMachineExampleActivity::class.java))
        }
    }

    private fun setupStorageSamples() {
        viewStorageExampleButton.setOnClickListener {
            startActivity(Intent(this, StorageSampleActivity::class.java))
        }
    }
}
