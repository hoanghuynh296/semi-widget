package vn.devfun.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import vn.semicolon.base.widget.spinner.PopUpModel
import vn.semicolon.base.widget.spinner.TUModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tmp = mutableListOf<TUModel>()
        for (i in 0 until 30) {
            tmp.add(TUModel("Title $i"))
        }
        spinnerTest.setItems(tmp.toMutableList())
        spinnerTest.setOnClickListener {
            Toast.makeText(applicationContext, "Test", Toast.LENGTH_SHORT).show()
            spinnerTest1.isEnabled = !spinnerTest1.isEnabled
        }
    }
}

