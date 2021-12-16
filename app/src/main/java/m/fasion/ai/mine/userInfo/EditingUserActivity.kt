package m.fasion.ai.toolbar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityEditingUserBinding
import m.fasion.ai.mine.userInfo.EditNickNameActivity
import m.fasion.ai.mine.userInfo.EditingUserPicActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.FavoritesListData
import m.fasion.core.model.UserInfo
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.SPUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 编辑用户资料
 */
class EditingUserActivity : BaseActivity() {

    private val binding by lazy { ActivityEditingUserBinding.inflate(layoutInflater) }
    private val viewModel: EditingUserViewModel by viewModels()

    private var mNickName: String = ""
    private var mVatar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.editingUserTitle.inCludeTitleIvBack.setOnClickListener {
            finish()
        }

        binding.editingUserTitle.inCludeTitleTvCenterTitle.text = getString(R.string.edit_information)

        viewModel.getUserInfo()

        viewModel.userInfoData.observe(this, {
            it?.let { model ->
                val phone = model.phone
                mNickName = model.nickname
                mVatar = model.avatar
                binding.editUserTvPhone.text = phone
                binding.editUserTvNickName.text = mNickName
                Glide.with(this).load(mVatar).into(binding.editUserIvUserBg)

                //头像更换
                binding.editUserLlUserBg.setOnClickListener {
                    EditingUserPicActivity.startActivity(this, model.avatar)
                }

                //改昵称
                binding.editUserLlNickName.setOnClickListener {
                    val intent = Intent(this, EditNickNameActivity::class.java)
                    startActivityForResult(intent, 102)
                }
            }
        })

        LiveEventBus.get<String>(ConstantsKey.EDIT_USER_PIC).observe(this,{
            if (!it.isNullOrEmpty()) {
                mVatar = it
                Glide.with(this).load(mVatar).into(binding.editUserIvUserBg)
                editUserInfo()
            }
        })
    }

    private fun editUserInfo() {
        viewModel.editUserInfo(mNickName, mVatar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                102 -> {  //昵称
                    data?.let {
                        val nickName = it.getStringExtra(ConstantsKey.EDIT_NICK)
                        nickName?.let { name ->
                            editUserInfo()
                            mNickName = name
                            binding.editUserTvNickName.text = name
                        }
                    }
                }
            }
        }
    }
}

class EditingUserViewModel : BaseViewModel() {

    private var launch: Job? = null
    val errorCallback = MutableLiveData<String>()

    val userInfoData = MutableLiveData<UserInfo>()

    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        launch = viewModelScope.launch {
            val me = repository.getMe()
            if (me.isSuccessful) {
                me.body()?.let {
                    userInfoData.value = it
                    SPUtil.put(ConstantsKey.USER_INFO_KEY, it)
                }
            } else {
                me.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        if (message.isNotEmpty()) {
                            ToastUtils.show(message)
                        }
                    }
                }
            }
        }
    }

    val uploadImageOk = MutableLiveData<String>()

    /**
     * 上传图片
     */
    fun uploadImage(path: File) {
        if (!path.exists()) return
        val asRequestBody = path.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("file", path.name, asRequestBody)
        launch = viewModelScope.launch {
            val uploadImage = repository.uploadImage(photoPart)
            if (uploadImage.isSuccessful) {
                uploadImage.body()?.let {
                    uploadImageOk.value = it
                }
            } else {
                uploadImage.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        if (message.isNotEmpty()) {
                            ToastUtils.show(message)
                            errorCallback.value = message
                        }
                    }
                }
            }
        }
    }

    /**
     * 修改用户信息
     */
    fun editUserInfo(nickName: String, picPath: String) {
        launch = viewModelScope.launch {
            val maps: MutableMap<String, Any> = mutableMapOf()
            maps.put("nickname", nickName)
            maps.put("avatar", picPath)
            val editMe = repository.editMe(maps)
            if (editMe.isSuccessful) {
                editMe.body()?.let {
                    if (it.id.isNotEmpty() && it.phone.isNotEmpty()) {
                        ToastUtils.show("修改成功")
                        LiveEventBus.get(ConstantsKey.EDIT_SUCCESS, String::class.java).post("1")
                    }
                }
            } else {
                editMe.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        if (message.isNotEmpty()) {
                            ToastUtils.show(message)
                        }
                    }
                }
            }
        }
    }

    val logoutLiveData = MutableLiveData<Int>()
    fun logout() {
        launch = viewModelScope.launch {
            val logout = repository.logout()
            if (logout.isSuccessful) {
                logoutLiveData.value = logout.code()
            }
        }
    }

    val favoritesListData = MutableLiveData<FavoritesListData>()

    /**
     * 收藏列表
     * @param id 上一页最后条数据的id(第一次 请求传空)
     */
    fun getClothesList(id: String) {
        launch = viewModelScope.launch {
            val favoritesList = repository.getFavoritesList(id)
            if (favoritesList.isSuccessful) {
                favoritesList.body()?.let { models ->
                    favoritesListData.value = models
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}