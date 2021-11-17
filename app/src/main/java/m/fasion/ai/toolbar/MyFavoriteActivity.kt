package m.fasion.ai.toolbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityMyFavoriteBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener

/**
 * 我的喜欢页面
 */
class MyFavoriteActivity : BaseActivity() {

    private val binding by lazy { ActivityMyFavoriteBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.myFavoriteTitle.inCludeTitleIvBack.setOnClickListener { finish() }
        binding.myFavoriteTitle.inCludeTitleTvCenterTitle.text = "我的喜欢"

        val adapter = MyFavoriteAdapter(listOf())
        binding.myFavoriteRV.adapter = adapter

        binding.myFavoriteRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.myFavoriteRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                ToastUtils.show(position.toString())
            }
        }))
    }
}