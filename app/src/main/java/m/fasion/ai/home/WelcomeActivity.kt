package m.fasion.ai.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import m.fasion.ai.databinding.ActivityWelcomeBinding

/**
 * 启动页
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            runBlocking {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}