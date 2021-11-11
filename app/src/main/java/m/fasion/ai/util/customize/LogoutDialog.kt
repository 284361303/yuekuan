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
 * @author shao_g
 * 退出登录弹窗
 * 2021年11月11日19:26:07
 */
class LogoutDialog(private val callback: Callback) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogLogoutBinding.inflate(inflater, container, false)
        CoreUtil.setTypeFaceMedium(listOf(binding.dialogLogoutTvCancel, binding.dialogLogoutTvQuit))
        binding.dialogLogoutTvCancel.setOnClickListener {
            dismiss()
        }

        //退出登录
        binding.dialogLogoutTvQuit.setOnClickListener {
            callback.logoutCallBack()
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
        fun logoutCallBack()
    }
}