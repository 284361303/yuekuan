package m.fasion.ai.toolbar

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityMyFavoriteBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.base.BaseViewModel

/**
 * 我的喜欢页面
 */
class MyFavoriteActivity : BaseActivity() {

    private val binding by lazy { ActivityMyFavoriteBinding.inflate(layoutInflater) }

    private val viewModel: MyFavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.myFavoriteTitle.inCludeTitleIvBack.setOnClickListener { finish() }
        binding.myFavoriteTitle.inCludeTitleTvCenterTitle.text = "我的喜欢"

        val adapter = MyFavoriteAdapter(listOf("", "", "", "", "", ""))
        binding.myFavoriteRV.adapter = adapter

        viewModel.getClothesList()

        binding.myFavoriteRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.myFavoriteRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                ToastUtils.show(position.toString())
            }
        }))
    }
}

class MyFavoriteViewModel : BaseViewModel() {

    private var launch: Job? = null

    /**
     * 收藏列表
     */
    fun getClothesList() {
        launch = viewModelScope.launch {
            repository.getClothesList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}