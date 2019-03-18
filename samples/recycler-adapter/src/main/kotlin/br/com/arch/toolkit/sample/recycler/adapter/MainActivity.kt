package br.com.arch.toolkit.sample.recycler.adapter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_simple_list_example).setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        findViewById<Button>(R.id.bt_multiple_view_types_example).setOnClickListener {
            startActivity(Intent(this, MultipleViewTypesActivity::class.java))
        }

        findViewById<Button>(R.id.bt_changing_list_example).setOnClickListener {
            startActivity(Intent(this, ChangingListActivity::class.java))
        }
    }
}
