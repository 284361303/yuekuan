package m.fasion.ai.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private val inflate by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val fragmentList = mutableListOf<Fragment>()
    private var homeFragment: HomeFragment? = null
    private var mineFragment: MineFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflate.root)

        setTitleBarLayout()

        inflate.mainNavigationView.itemIconTintList = null
        inflate.mainNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.main_fragment -> {
                    inflate.mainViewPager.currentItem = 0
                }
                R.id.mine_fragment -> {
                    inflate.mainViewPager.currentItem = 1
                }
            }
            true
        }
        //设置默认显示第一个页面的Fragment
        showFragment()
    }

    private fun showFragment() {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        if (mineFragment == null) {
            mineFragment = MineFragment()
        }
        fragmentList.clear()
        fragmentList.add(homeFragment!!)
        fragmentList.add(mineFragment!!)
        inflate.mainViewPager.offscreenPageLimit = fragmentList.size
        inflate.mainViewPager.adapter = ScreenSlidePagerAdapter(fragmentList, this)
        inflate.mainViewPager.isUserInputEnabled = false
        inflate.mainViewPager.currentItem = 0
    }

    /**
     * 隐藏状态栏
     */
    private fun setTitleBarLayout() {
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    private class ScreenSlidePagerAdapter(private val lists: List<Fragment>, fa: FragmentActivity?) :
        FragmentStateAdapter(fa!!) {
        override fun createFragment(position: Int): Fragment {
            return lists[position]
        }

        override fun getItemCount(): Int {
            return lists.size
        }
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