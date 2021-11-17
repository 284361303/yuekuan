package m.fasion.ai.homeDetails

import android.os.Bundle
import android.view.WindowManager
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityTopicEveryPeriodBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.util.CoreUtil

/**
 * 每期精选
 */
class TopicEveryPeriodActivity : BaseActivity() {

    private val binding by lazy { ActivityTopicEveryPeriodBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        binding.topicEveryPeriodIvBack.setOnClickListener { finish() }
        CoreUtil.setTypeFaceMedium(listOf(binding.topicEveryPeriodTv1, binding.topicEveryPeriodTv2, binding.topicEveryPeriodTv3))
        val adapter = TopicEveryPeriodAdapter(listOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        binding.topicEveryPeriodRV.adapter = adapter

        adapter.onClickListener = object : TopicEveryPeriodAdapter.OnClickListener {
            override fun collectionClick(position: Int) {
                ToastUtils.show("喜欢")
            }

            override fun onItemClick(position: Int) {
                ToastUtils.show("第${position}个")
            }
        }
    }
}