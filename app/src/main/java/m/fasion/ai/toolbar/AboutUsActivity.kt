package m.fasion.ai.toolbar

import android.os.Bundle
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityAboutUsBinding
import m.fasion.core.util.CoreUtil

/**
 * 关于我们
 */
class AboutUsActivity : BaseActivity() {

    private val binding by lazy { ActivityAboutUsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.aboutUsTitle.inCludeTitleIvBack.setOnClickListener {
            finish()
        }

        CoreUtil.setTypeFaceMedium(listOf(binding.aboutUsTv1))
    }
}