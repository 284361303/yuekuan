package m.fasion.ai.homeDetails

import android.os.Bundle
import android.view.WindowManager
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityTopicSuitBinding
import m.fasion.core.util.CoreUtil

/**
 * 高级西装搭配页面
 */
class TopicSuitActivity : BaseActivity() {

    private val binding by lazy { ActivityTopicSuitBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        CoreUtil.setTypeFaceMedium(listOf(binding.topSuitTv2))

        val adapter = TopicSuitAdapter(listOf("", "", "", "", "", "", "", "", "", "", ""))
        binding.topSuitRV.adapter = adapter
    }
}