package m.fasion.ai.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import m.fasion.ai.R
import m.fasion.ai.databinding.FragmentMineBinding

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
            Log.e(TAG, "onCreate: " + param1)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _inflate = null
    }
}