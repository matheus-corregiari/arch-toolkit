package br.com.arch.toolkit.sample.github

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.ui.xml.list.withoutPagination.RepositoryListActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region Views
    private val buttonXmlWithoutPagination: View by viewProvider(R.id.button_xml_without_pagination)
    private val buttonCompose: View by viewProvider(R.id.button_compose)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Lumber.plant(DebugTree())
        buttonCompose.setOnClickListener { onComposeFlowClick() }
        buttonXmlWithoutPagination.setOnClickListener { onXmlFlowClick() }
    }

    private fun onXmlFlowClick() = startActivity(Intent(this, RepositoryListActivity::class.java))

    private fun onComposeFlowClick() = Toast.makeText(this, "Not Yet!", Toast.LENGTH_SHORT).show()
}
