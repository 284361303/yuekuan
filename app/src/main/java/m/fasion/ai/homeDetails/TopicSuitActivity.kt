package m.fasion.ai.homeDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityTopicSuitBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.util.CoreUtil

/**
 * 高级西装搭配页面
 */
class TopicSuitActivity : BaseActivity() {

    private val binding by lazy { ActivityTopicSuitBinding.inflate(layoutInflater) }
    private val viewModel: TopicSuitViewHolder by viewModels()

    companion object {
        const val mId: String = "id"
        fun startActivity(context: Context, id: String) {
            val intent = Intent(context, TopicSuitActivity::class.java)
            val bundle = Bundle()
            if (id.isNotEmpty()) {
                bundle.putString(mId, id)
            }
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        CoreUtil.setTypeFaceMedium(listOf(binding.topSuitTv2))

        intent.extras?.containsKey(mId)?.let {
            if (it) {
                intent.extras?.getString(mId)?.apply {
                    viewModel.getTopics(this)
                }
            }
        }

        val adapter = TopicSuitAdapter(listOf("", "", "", "", "", "", "", "", "", "", ""))
        binding.topSuitRV.adapter = adapter

        adapter.onClickListener = object : TopicSuitAdapter.OnClickListener {
            override fun onItemClickListener(position: Int) {
                ToastUtils.show(position.toString())
            }

            override fun onCollectClickListener(position: Int) {
            }
        }
    }
}

class TopicSuitViewHolder : BaseViewModel() {

    private var launch: Job? = null

    /**
     * 根据合集id获取合集信息，如：首页banner跳转到高级西装搭配
     */
    fun getTopics(id: String) {
        launch = viewModelScope.launch {
            repository.getTopics(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}