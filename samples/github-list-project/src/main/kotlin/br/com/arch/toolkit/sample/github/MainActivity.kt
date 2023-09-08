package br.com.arch.toolkit.sample.github

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.arch.toolkit.sample.github.ui.xml.list.RepositoryListActivity
import br.com.arch.toolkit.sample.github.util.DefaultComposePreview
import br.com.arch.toolkit.sample.github.util.composeContent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeContent {
            MainContent(
                xmlAction = ::onXmlFlowClick, composeAction = ::onComposeFlowClick
            )
        }
    }

    private fun onXmlFlowClick() = startActivity(Intent(this, RepositoryListActivity::class.java))

    private fun onComposeFlowClick() = Toast.makeText(this, "Not Yet!", Toast.LENGTH_SHORT).show()

}

@Composable
private fun MainContent(
    xmlAction: () -> Unit,
    composeAction: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
    ) {

        /*  */
        FlowCard(
            buttonLabel = R.string.main_button_xml, action = xmlAction
        )

        /*  */
        Spacer(modifier = Modifier.height(8.dp))

        /*  */
        FlowCard(
            buttonLabel = R.string.main_button_compose, action = composeAction
        )
    }
}

@Composable
private fun FlowCard(
    @StringRes buttonLabel: Int,
    action: () -> Unit,
) {

    /*  */
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.DarkGray)
    ) {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp, vertical = 12.dp
                )
        ) {
            val (label, button) = createRefs()

            val labelModifier = Modifier.constrainAs(label) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }

            val buttonModifier = Modifier.constrainAs(button) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(label.end)
                end.linkTo(parent.end)
            }

            Text(text = "Access Flow", modifier = labelModifier)
            ElevatedButton(onClick = action, modifier = buttonModifier) {
                Text(text = stringResource(buttonLabel))
            }
        }
    }

}


@DefaultComposePreview
@Composable
private fun `Main Preview`() {
    MainContent({}, {})
}