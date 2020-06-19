package br.com.arch.toolkit.sample.statemachine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.ExtraType
import br.com.arch.toolkit.delegate.extraProvider
import br.com.arch.toolkit.delegate.viewProvider

class MainActivity : AppCompatActivity() {

    private val viewStateMachineExampleButton: Button by viewProvider(R.id.bt_view_state_machine_example)
    private val sceneStateMachineExampleButton: Button by viewProvider(R.id.bt_scene_state_machine_example)

    private val extra: String by extraProvider("teste", default = "padraozaoo", type = ExtraType.QUERY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewStateMachineExampleButton.setOnClickListener {
            Toast.makeText(this, "Test argument: $extra", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ViewStateMachineExampleActivity::class.java))
        }

        sceneStateMachineExampleButton.setOnClickListener {
            startActivity(Intent(this, SceneStateMachineExampleActivity::class.java))
        }
    }
}
