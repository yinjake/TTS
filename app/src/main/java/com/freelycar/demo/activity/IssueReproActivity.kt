package com.freelycar.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.freelycar.demo.databinding.IssueReproActivityBinding

class IssueReproActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = IssueReproActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Reproduce any issues here.
    }
}
