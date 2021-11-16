package m.fasion.ai.toolbar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityFeedBacksBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.util.CoreUtil

/**
 * 意见反馈页面
 */
class FeedBacksActivity : BaseActivity() {

    private val binding by lazy {
        ActivityFeedBacksBinding.inflate(layoutInflater)
    }

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
                if (trim.isNotEmpty() && trim.length > 5) {
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
            if(binding.feedBacksEtPhone.text.toString().trim().isNotEmpty()){

            }
        }
    }
}