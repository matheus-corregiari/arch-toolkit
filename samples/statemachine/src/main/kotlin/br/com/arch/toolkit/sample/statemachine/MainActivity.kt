package br.com.arch.toolkit.sample.statemachine

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_view_state_machine_example).setOnClickListener {
            startActivity(Intent(this, ViewStateMachineExampleActivity::class.java))
        }

        findViewById<Button>(R.id.bt_scene_state_machine_example).setOnClickListener {
            startActivity(Intent(this, SceneStateMachineExampleActivity::class.java))
        }
    }
}
