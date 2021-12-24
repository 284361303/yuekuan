package m.fasion.ai.mine.userInfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityEditNickNameBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.ConstantsKey
import m.fasion.core.util.CoreUtil
import java.nio.charset.Charset
import java.util.*


/**
 * 修改昵称
 */
class EditNickNameActivity : BaseActivity() {

    private val binding by lazy { ActivityEditNickNameBinding.inflate(layoutInflater) }

    private val blockCharacterSet = "[`~!@#\$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ ]"

    /**
     * 过滤特殊字符
     */
    private val filter = InputFilter { source, _, _, _, _, _ ->
        if (source != null && blockCharacterSet.contains("" + source)) {
            ""
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.activity_in_from_right, 0)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CoreUtil.setTypeFaceMedium(listOf(binding.editNickNameTv1))

        binding.editNickNameSpace.setOnClickListener {
            finish()
        }
        binding.editNickNameEtContent.filters = arrayOf(filter)

        binding.editNickNameEtContent.isFocusable = true
        binding.editNickNameEtContent.isFocusableInTouchMode = true
        binding.editNickNameEtContent.requestFocus()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val methodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                methodManager.showSoftInput(binding.editNickNameEtContent, 0)
                binding.editNickNameEtContent.setSelection(binding.editNickNameEtContent.text.toString().length)
            }
        }, 180)

        //确定按钮
        binding.editNickNameTvSave.setOnClickListener {
            val trim = binding.editNickNameEtContent.text.toString().trim()
            if (trim.isEmpty()) {
                ToastUtils.showCenter("昵称不能为空")
                return@setOnClickListener
            }

            val byteLength = trim.toByteArray(Charset.forName("GBK")).size
            if (byteLength > 12) {
                ToastUtils.showCenter("昵称最多设置6个字符！")
                return@setOnClickListener
            }
            val intent = Intent()
            intent.putExtra(ConstantsKey.EDIT_NICK, trim)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}