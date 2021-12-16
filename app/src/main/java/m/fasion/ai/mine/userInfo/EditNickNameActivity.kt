package m.fasion.ai.mine.userInfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import m.fasion.ai.databinding.ActivityEditNickNameBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.Config
import m.fasion.core.base.ConstantsKey
import m.fasion.core.util.CoreUtil

/**
 * 修改昵称
 */
class EditNickNameActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditNickNameBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CoreUtil.setTypeFaceMedium(listOf(binding.editNickNameTv1))

        binding.editNickNameSpace.setOnClickListener {
            finish()
        }

        //确定按钮
        binding.editNickNameTvSave.setOnClickListener {
            val trim = binding.editNickNameEtContent.text.toString().trim()
            if (trim.isEmpty()) {
                ToastUtils.show("昵称不能为空")
                return@setOnClickListener
            }
            val intent = Intent()
            intent.putExtra(ConstantsKey.EDIT_NICK, trim)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}