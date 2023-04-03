package com.bsj0420.tpkaosearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bsj0420.tpkaosearchapp.R
import com.bsj0420.tpkaosearchapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바로 제목줄로 대체 옵션메뉴 가지려고
        setSupportActionBar(binding.toobar)


    }//onCreate


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.aaa -> Toast.makeText(this, "aaaa", Toast.LENGTH_SHORT).show()
            R.id.bbb -> Toast.makeText(this, "bbbb", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

}