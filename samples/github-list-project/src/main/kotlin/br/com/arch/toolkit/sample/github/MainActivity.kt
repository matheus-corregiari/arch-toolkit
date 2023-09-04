package br.com.arch.toolkit.sample.github

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewModelProvider
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.github.ui.xml.list.RepositoryListActivity
import br.com.arch.toolkit.sample.github.ui.xml.list.RepositoryListViewModel

/**
 * TODO Move to compose?
 * TODO Try the new splash?
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region Views
    private val xmlButton: View by viewProvider(R.id.xml_button)
    private val composeButton: View by viewProvider(R.id.compose_button)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xmlButton.setOnClickListener {
            startActivity(Intent(this, RepositoryListActivity::class.java))
        }
        composeButton.setOnClickListener {
            Toast.makeText(this, "Not Yet!", Toast.LENGTH_SHORT).show()
        }
    }
}