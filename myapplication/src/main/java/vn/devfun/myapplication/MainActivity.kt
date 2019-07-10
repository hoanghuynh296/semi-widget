package vn.devfun.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import vn.semicolon.base.widget.spinner.SemiSpinnerAdapter
import vn.semicolon.base.widget.spinner.SimpleAdapter
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
        iconState.isSelected = false

        val adapter = SimpleAdapter()
        adapter.addAll(listOf("huuynh", "dqwdqwe", "qweqweqwe", "qweqweqwe", "eqweq"))
        spinner2.adapter = adapter

        adapter.addOnItemSelectedListener(object : SemiSpinnerAdapter.OnItemSelectedListenter {
            override fun onItemSelected(s: String, pos: Int, v: View) {
                adapter.clear()
                adapter.addAll(listOf("12312", "12312412432"))
            }

        })
    }
}

