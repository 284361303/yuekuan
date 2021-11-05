package m.fasion.ai.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    private val inflate by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflate.root)

        setTitleBarLayout()

        inflate.mainNavigationView.itemIconTintList = null
        inflate.mainNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.main_fragment -> {
                    showFragment(HomeFragment.TAG)
                }
                R.id.factor_fragment -> {
                    showFragment(HomeFragment.TAG)
                }
                R.id.mine_fragment -> {
                    showFragment(MineFragment.TAG)
                }
            }
            true
        }
        //设置默认显示第一个页面的Fragment
        showFragment(HomeFragment.TAG)
    }

    private fun showFragment(tag: String) {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            when (tag) {
                HomeFragment.TAG -> {
                    fragment = HomeFragment()
                    fragment.arguments = Bundle().also { it.putString("name", "1个fragment") }
                }
                HomeFragment.TAG -> {
                    fragment = HomeFragment()
                    fragment.arguments = Bundle().also { it.putString("name", "2个fragment") }
                }
                HomeFragment.TAG -> {
                    fragment = HomeFragment()
                    fragment.arguments = Bundle().also { it.putString("name", "3个fragment") }
                }
                MineFragment.TAG -> {
                    fragment = MineFragment()
                    fragment.arguments = Bundle().also { it.putString("name", "4个fragment") }
                }
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, fragment!!, tag)
            .commit()
    }

    /**
     * 隐藏状态栏
     */
    private fun setTitleBarLayout() {
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            } else {
                if (!this.isFinishing) {
                    finish()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}