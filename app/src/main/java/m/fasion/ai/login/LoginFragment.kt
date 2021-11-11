package m.fasion.ai.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import m.fasion.ai.databinding.FragmentLoginBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.util.CoreUtil.isMobile

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        binding.loginTvLogin.isClickable = false    //禁止点击
    }

    private fun initListener() {
        //输入监听
        binding.loginEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.loginEt.isClickable = s.toString().trim().isNotEmpty()
                val length = s.toString().length
                if (length == 0) return
                //删除数字
                when (count) {
                    0 -> {
                        if (length == 4) {
                            binding.loginEt.setText(s!!.subSequence(0, 3))
                        }
                        if (length == 9) {
                            binding.loginEt.setText(s!!.subSequence(0, 8))
                        }
                    }
                    //添加数字
                    1 -> {
                        if (length == 4) {
                            val part1 = s!!.subSequence(0, 3).toString()
                            val part2 = s.subSequence(3, length).toString()
                            val part3 = "$part1 $part2"
                            binding.loginEt.setText(part3)
                        }
                        if (length == 9) {
                            val part1 = s!!.subSequence(0, 8).toString()
                            val part2 = s.subSequence(8, length).toString()
                            val part3 = "$part1 $part2"
                            binding.loginEt.setText(part3)
                        }
                    }
                }

                if (length == 13) { //按钮可以点击
                    binding.loginTvLogin.alpha = 1f
                    binding.loginTvLogin.isClickable = true
                } else {    //不可点击
                    binding.loginTvLogin.isClickable = false
                    binding.loginTvLogin.alpha = 0.55f
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.loginEt.setSelection(binding.loginEt.text.toString().length)
            }
        })

        //下一步
        binding.loginTvLogin.setOnClickListener {
            if (!binding.loginCheckBox.isChecked) {
                ToastUtils.show("请先勾选同意用户协议与隐私政策")
            } else {
                val phone = binding.loginEt.text.toString()
                if (phone.isEmpty()) {
                    ToastUtils.show("手机号不能为空")
                    return@setOnClickListener
                }
                val newStr = phone.replace("\\s".toRegex(), "")
                if (newStr.length < 11 || !newStr.isMobile()) {
                    ToastUtils.show("请输入正确的手机号")
                    return@setOnClickListener
                }
                viewModel.sendCode(newStr)
            }
        }

        binding.loginLlAgreement.setOnClickListener {
            binding.loginCheckBox.isChecked = !binding.loginCheckBox.isChecked
        }
    }
}