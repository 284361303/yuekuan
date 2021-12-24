package m.fasion.ai.util.customize

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import m.fasion.ai.databinding.DialogPrivacyBinding
import m.fasion.ai.webView.WebViewActivity
import m.fasion.core.Config
import m.fasion.core.util.CoreUtil

class PrivacyDialog(private val callback: Callback) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogPrivacyBinding.inflate(inflater, container, false)
        CoreUtil.setTypeFaceMedium(listOf(binding.dialogPrivacyTv1))
        binding.dialogPrivacyTv4.setOnClickListener {
            WebViewActivity.startActivity(requireContext(), Config.PROTOCOL_URL, "")
        }
        binding.dialogPrivacyTv5.setOnClickListener {
            WebViewActivity.startActivity(requireContext(), Config.PRIVACY_URL, "")
        }

        //不同意
        binding.dialogPrivacyTvCancel.setOnClickListener {
            callback.noAgreeCliListener()
        }
        //同意
        binding.dialogPrivacyTvSure.setOnClickListener {
            dismiss()
            callback.agreeClickListener()
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
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
         * 同意按钮
         */
        fun agreeClickListener()

        /**
         * 不同意
         */
        fun noAgreeCliListener()

        /**
         * 隐私政策
         */
//        fun privacyCliListener()

        /**
         * 用户协议
         */
//        fun protocolCliListener()
    }
}