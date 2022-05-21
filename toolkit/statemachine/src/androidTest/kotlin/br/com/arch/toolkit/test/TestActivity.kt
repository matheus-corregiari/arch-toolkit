package br.com.arch.toolkit.test

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout

class TestActivity : AppCompatActivity() {

    private lateinit var content: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = FrameLayout(this)
        content.setBackgroundColor(Color.WHITE)
        content.id = android.R.id.content
        setContentView(content)
    }

    fun setFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction().add(android.R.id.content, fragment, "TAG").commit()
    }

    fun setView(view: (Context) -> View) = runOnUiThread {
        content.addView(view.invoke(this))
    }
}
