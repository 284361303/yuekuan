package m.fasion.ai.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.ai.databinding.FragmentMineBinding
import m.fasion.ai.mine.AddAddressActivity

/**
 * 我的页面
 * 2021年9月23日
 */
class MineFragment : Fragment() {

    private var param1: String? = ""
    private var _inflate: FragmentMineBinding? = null

    companion object {
        const val TAG = "MineFragmentTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _inflate = FragmentMineBinding.inflate(inflater, container, false)
        return _inflate?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _inflate?.let {
            it.mineTvName.text = param1
            val mUrl = "https://lh3.googleusercontent.com/NSVbWbdKFGRzju5r5XsXKMJ9A41PVdWNhGSxDwxk9aO6o_7SeVMU8z27-GhdNw3uS0PZtLPts5tvaxdsHr--NRXZWfyi=s300"
            Glide.with(requireContext()).load(mUrl).into(it.mineIvBg)
            Unit
        }
        _inflate?.mineBtnAddress?.setOnClickListener {
            startActivity(Intent(requireContext(), AddAddressActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _inflate = null
    }
}