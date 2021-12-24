package m.fasion.ai.toolbar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityFeedBacksBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.CoreUtil

/**
 * 意见反馈页面
 */
class FeedBacksActivity : BaseActivity() {

    private val binding by lazy { ActivityFeedBacksBinding.inflate(layoutInflater) }
    private val viewModel: FeedBackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.feedBacksTitle.inCludeTitleIvBack.setOnClickListener {
            finish()
        }
        binding.feedBacksTitle.inCludeTitleTvCenterTitle.text = "意见反馈"

        CoreUtil.setTypeFaceMedium(listOf(binding.feedBacksTitle.inCludeTitleTvCenterTitle, binding.feedBacksTv1, binding.feedBacksTvContactKey))

        binding.feedBacksEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val trim = s.toString().trim()
                if (trim.isNotEmpty()) {
                    binding.feedBacksTvSubmit.alpha = 1f
                    binding.feedBacksTvSubmit.isClickable = true
                } else {
                    binding.feedBacksTvSubmit.isClickable = false
                    binding.feedBacksTvSubmit.alpha = 0.4f
                }
            }
        })

        binding.feedBacksTvSubmit.isClickable = false    //禁止点击

        //提交
        binding.feedBacksTvSubmit.setOnClickListener {
            if (binding.feedBacksEt.text.toString().trim().isEmpty()) {
                ToastUtils.show("请输入你要描述的问题")
                return@setOnClickListener
            }
            viewModel.saveContent(binding.feedBacksEt.text.toString(),
                if (binding.feedBacksEtPhone.text.toString().trim().isNotEmpty()) binding.feedBacksEtPhone.text.toString() else "")
        }

        //点击外部隐藏软键盘
        binding.feedBacksClAll.setOnClickListener {
            CoreUtil.hideKeyBoard(binding.feedBacksClAll)
        }

        viewModel.saveSuccessLiveData.observe(this, {
            ToastUtils.show(it)
            finish()
        })
    }
}

class FeedBackViewModel : BaseViewModel() {

    private var launch: Job? = null

    val saveSuccessLiveData = MutableLiveData<String>()
    fun saveContent(contents: String, phone: String) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["contents"] = contents
        if (phone.isNotEmpty()) {
            map["contactDetails"] = phone
        }
        try {
            launch = viewModelScope.launch {
                val feedBack = repository.feedBack(map)
                if (feedBack.isSuccessful) {
                    saveSuccessLiveData.value = "感谢您的反馈"
                } else {
                    feedBack.errorBody()?.stringSuspending()?.let {
                        Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                            ToastUtils.show(message)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}