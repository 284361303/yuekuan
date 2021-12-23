package m.fasion.ai.homeDetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityTopicSuitBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.Body
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.RecommendList
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.CoreUtil

/**
 * 高级西装搭配页面
 */
class TopicSuitActivity : BaseActivity() {

    private var mAdapter: TopicSuitAdapter? = null
    private val binding by lazy { ActivityTopicSuitBinding.inflate(layoutInflater) }
    private val viewModel: TopicSuitViewHolder by viewModels()
    private var listData: MutableList<Body> = mutableListOf()

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

    @SuppressLint("NotifyDataSetChanged", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        CoreUtil.setTypeFaceMedium(listOf(binding.topSuitTvTitle))

        intent.extras?.containsKey(mId)?.let {
            if (it) {
                intent.extras?.getString(mId)?.apply {
                    viewModel.getTopics(this)
                }
            }
        }

        initAdapter()

        //数据回调
        viewModel.topicsData.observe(this, {
            it?.let { data ->
                val head = data.head
                val body = data.body
                if (head.isNotEmpty()) {
                    val link = head[0].link
                    Glide.with(this).asBitmap().load(link).into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            binding.topSuitIvBG.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
                    binding.topSuitTvTitle.text = data.title
                    binding.topSuitTv2.text = data.sub_title
                }
                if (body.isNotEmpty()) {
                    listData.addAll(body)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        })

        binding.topicSuitIvBack.setOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        mAdapter = TopicSuitAdapter(listData)
        binding.topSuitRV.adapter = mAdapter

        mAdapter?.onClickListener = object : TopicSuitAdapter.OnClickListener {
            override fun onItemClickListener(model: Body, position: Int) {
                HomeDetailsActivity.startActivity(this@TopicSuitActivity, model.target)
            }

            override fun onCollectClickListener(model: Body, position: Int) {
                checkLogin {
                    val favourite = model.favourite
                    val id = model.target
                    if (favourite) {
                        model.favourite = false
                        viewModel.cancelFavorites(id)
                    } else {
                        model.favourite = true
                        viewModel.addFavorites(id)
                    }
                    mAdapter?.notifyItemChanged(position, -1)
                }
            }
        }
    }
}

class TopicSuitViewHolder : BaseViewModel() {

    private var launch: Job? = null

    val topicsData = MutableLiveData<RecommendList>()

    /**
     * 根据合集id获取合集信息，如：首页banner跳转到高级西装搭配
     */
    fun getTopics(id: String) {
        launch = viewModelScope.launch {
            val topics = repository.getTopics(id)
            if (topics.isSuccessful) {
                topics.body()?.let { data ->
                    topicsData.value = data
                }
            } else {
                topics.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        ToastUtils.show(message)
                    }
                }
            }
        }
    }

    val addFavoritesOk = MutableLiveData<String>()
    fun addFavorites(id: String) {
        launch = viewModelScope.launch {
            val addFavorites = repository.addFavorites(id)
            if (addFavorites.isSuccessful) {
                addFavorites.body()?.let {
                    addFavoritesOk.value = id
                }
            }
        }
    }

    val cancelFavoritesOk = MutableLiveData<String>()
    fun cancelFavorites(id: String) {
        launch = viewModelScope.launch {
            val cancelFavorites = repository.cancelFavorites(id)
            if (cancelFavorites.isSuccessful) {
                cancelFavorites.body()?.let {
                    cancelFavoritesOk.value = id
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}