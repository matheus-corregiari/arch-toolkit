package br.com.arch.toolkit.sample.github

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.github.ui.xml.list.withoutPagination.RepositoryListActivity
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region Views
    private val buttonXmlWithoutPagination: View by viewProvider(R.id.button_xml_without_pagination)
    private val buttonCompose: View by viewProvider(R.id.button_compose)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        buttonCompose.setOnClickListener { onComposeFlowClick() }
        buttonXmlWithoutPagination.setOnClickListener { onXmlFlowClick() }
    }

    private fun onXmlFlowClick() = startActivity(Intent(this, RepositoryListActivity::class.java))

    private fun onComposeFlowClick() = Toast.makeText(this, "Not Yet!", Toast.LENGTH_SHORT).show()

}
