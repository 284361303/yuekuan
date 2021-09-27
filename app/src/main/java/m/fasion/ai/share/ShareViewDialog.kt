package m.fasion.ai.share

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import m.fasion.ai.databinding.DialogShareViewBinding

/**
 * 分享布局页面
 */
class ShareViewDialog(val callback: CallBack) : DialogFragment() {

    private lateinit var binding: DialogShareViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogShareViewBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.diaLogShareViewTop.setOnClickListener {
            dismiss()
        }
        //微信好友
        binding.diaLogShareTvWeiChat.setOnClickListener {
            callback.shareWeChat()
            dismiss()
        }
        //微信朋友圈
        binding.diaLogShareTvWeiChatFriends.setOnClickListener {
            callback.shareFriends()
            dismiss()
        }
        //微博
        binding.diaLogShareTvWeiBo.setOnClickListener {
            callback.shareWeiBo()
            dismiss()
        }
        //抖音
        binding.diaLogShareTvDouYin.setOnClickListener {
            callback.shareDouYin()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false //点击外部不可取消
        this.dialog?.window?.setLayout(
            getScreenWidth(requireActivity()),
            getScreenHeight(requireActivity())
        )
    }

    private fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    interface CallBack {
        fun shareWeChat()
        fun shareFriends()
        fun shareWeiBo()
        fun shareDouYin()
    }
}