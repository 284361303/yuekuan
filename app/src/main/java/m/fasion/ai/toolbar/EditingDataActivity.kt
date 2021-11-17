package m.fasion.ai.toolbar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityEditingDataBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.util.CoreUtil

/**
 * 资料编辑
 */
class EditingDataActivity : BaseActivity() {

    private var mSelectPosition: Int? = null
    private val binding by lazy { ActivityEditingDataBinding.inflate(layoutInflater) }
    private var lists = mutableListOf(R.mipmap.avatar_2, R.mipmap.avatar_3, R.mipmap.avatar_4,
        R.mipmap.avatar_5, R.mipmap.avatar_6, R.mipmap.avatar_7, R.mipmap.avatar_8,
        R.mipmap.avatar_9, R.mipmap.avatar_10, R.mipmap.avatar_11, R.mipmap.avatar_12,
        R.mipmap.avatar_13, R.mipmap.avatar_14, R.mipmap.avatar_15, R.mipmap.avatar_16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editingDataTitle.inCludeTitleIvBack.setOnClickListener {
            finish()
        }
        binding.editingDataTitle.inCludeTitleTvCenterTitle.text = getString(R.string.edit_information)

        CoreUtil.setTypeFaceMedium(listOf(binding.editingDataTv1, binding.editingDataTv2))

        binding.editingDataIvDelete.setOnClickListener {
            binding.editingDataEt.setText("")
        }
        val adapter = AvatarListAdapter(lists)
        binding.editingDataRV.adapter = adapter
        binding.editingDataRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.editingDataRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                mSelectPosition = position
                adapter.setSelectItem(position)
            }
        }))

        binding.editingDataEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString().trim()
                if (content.isNotEmpty()) {
                    binding.editingDataIvDelete.visibility = View.VISIBLE
                    binding.editingDataTvSubmit.isClickable = true
                    binding.editingDataTvSubmit.alpha = 1f
                } else {
                    binding.editingDataIvDelete.visibility = View.GONE
                    binding.editingDataTvSubmit.isClickable = false
                    binding.editingDataTvSubmit.alpha = 0.4f
                }
            }
        })

        //提交
        binding.editingDataTvSubmit.setOnClickListener {
            val content = binding.editingDataEt.text.toString().trim()
            if (content.isEmpty() && mSelectPosition == null) {
                ToastUtils.show("请输入昵称或选择喜欢的头像")
                return@setOnClickListener
            }
            ToastUtils.show("调用修改接口")
        }
        binding.editingDataTvSubmit.isClickable = false
    }
}