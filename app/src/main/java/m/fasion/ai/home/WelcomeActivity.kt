package m.fasion.ai.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import m.fasion.ai.databinding.ActivityWelcomeBinding
import m.fasion.ai.util.customize.PrivacyDialog
import m.fasion.core.base.ConstantsKey
import m.fasion.core.util.SPUtil

/**
 * 启动页
 */
class WelcomeActivity : AppCompatActivity() {

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && index == 0) {
            index += 1
            runBlocking {
                //判断是否第一次安装
                val string = SPUtil.getString(ConstantsKey.PRIVACY_FLAG)
                if (string.isNullOrEmpty()) {
                    PrivacyDialog(object : PrivacyDialog.Callback {
                        override fun agreeClickListener() {
                            SPUtil.put(ConstantsKey.PRIVACY_FLAG, "in")
                            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun noAgreeCliListener() {
                            finishAndRemoveTask()
                        }
                    }).show(supportFragmentManager, ConstantsKey.PRIVACY_FLAG)
                } else {
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}