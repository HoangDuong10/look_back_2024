package com.example.loadimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loadimage.databinding.TestConstranBinding

class TestActivity : AppCompatActivity() {
    lateinit var binding: TestConstranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestConstranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị LookBackFragment
        binding.test.setOnClickListener {
            val lookBackFragment = LookBackFragment()
            lookBackFragment.show(supportFragmentManager, "LookBackFragment")  // Dùng supportFragmentManager
        }
    }
}
