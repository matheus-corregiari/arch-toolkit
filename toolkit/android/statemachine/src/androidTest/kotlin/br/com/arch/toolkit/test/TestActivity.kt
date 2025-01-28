package br.com.arch.toolkit.test

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class TestActivity : AppCompatActivity() {

    private lateinit var content: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = FrameLayout(this)
        content.setBackgroundColor(Color.WHITE)
        content.id = android.R.id.content
        setContentView(content)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(android.R.id.content, fragment, "TAG")
            .commit()
    }

    fun setView(view: (Context) -> View) = runOnUiThread {
        content.addView(view.invoke(this))
    }
}
