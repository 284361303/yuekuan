package m.fasion.ai.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import m.fasion.ai.databinding.LoadingViewBinding

class LoadingDialog(var message: String? = null) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = LoadingViewBinding.inflate(inflater, container, false)
        val tvContent = binding.loadingViewTvContent
        if (!message.isNullOrEmpty()) {
            tvContent.text = message
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0f)  //完全透明
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false    //点击外部不可取消
        val dm = DisplayMetrics()
        this.activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        this.dialog?.window?.setLayout(dm.widthPixels, dm.heightPixels)
    }
}