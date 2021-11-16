package m.fasion.ai.util.customize

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import m.fasion.ai.databinding.DialogLogoutBinding
import m.fasion.core.util.CoreUtil

/**
 * 自定义内容弹窗，左边是取消，右边是确定或者其他的，中间是内容
 * 2021年11月11日19:26:07
 * @author shao_g
 * @param content 中间的内容 如：确定要退出吗
 * @param leftStr   左边的取消按钮，默认取消，可不传
 * @param rightStr  右边的确定按钮，内容自定义
 */
class CustomizeDialog(private val content: String,
                      private val leftStr: String? = null, private val rightStr: String,
                      private val callback: Callback) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogLogoutBinding.inflate(inflater, container, false)
        CoreUtil.setTypeFaceMedium(listOf(binding.dialogLogoutTvCancel, binding.dialogLogoutTvQuit))
        if (content.isNotEmpty()) {
            binding.dialogLogoutTv1.text = content
        }
        if (!leftStr.isNullOrEmpty()) {
            binding.dialogLogoutTvCancel.text = leftStr
        }
        if (rightStr.isNotEmpty()) {
            binding.dialogLogoutTvQuit.text = rightStr
        }

        binding.dialogLogoutTvCancel.setOnClickListener {
            dismiss()
        }

        //退出登录
        binding.dialogLogoutTvQuit.setOnClickListener {
            callback.rightClickCallBack()
            dismiss()
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false    //点击外部不可取消
        val dm = DisplayMetrics()
        this.activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        this.dialog?.window?.setLayout(dm.widthPixels, dm.heightPixels)
    }

    interface Callback {
        /**
         * 右侧的确定或者退出按钮回调事件
         */
        fun rightClickCallBack()
    }
}