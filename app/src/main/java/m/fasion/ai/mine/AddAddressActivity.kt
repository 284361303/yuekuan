package m.fasion.ai.mine

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.NonNull
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.google.gson.Gson
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityAddAddressBinding
import m.fasion.ai.databinding.ActivityMainBinding
import m.fasion.ai.util.LogUtils
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.util.CoreUtil

/**
 * 添加收货地址页面
 */
class AddAddressActivity : BaseActivity() {

    private val viewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = ActivityAddAddressBinding.inflate(layoutInflater).apply {
            addressModel = viewModel
        }
        setContentView(inflate.root)

        inflate.include.inCludeTitleTvTitle.text = "添加收货地址"
        CoreUtil.setTypeFaceMedium(arrayOf(inflate.include.inCludeTitleTvTitle, inflate.addAddressTvReceiverKey, inflate.addAddressTvPhoneKey).toList())

        inflate.addAddressTvInfoAddressPosition.setOnClickListener {
            viewModel.startLocation()
        }

        inflate.include.inCludeTitleIvBack.setOnClickListener {
            finish()
        }

        viewModel.initLocation(this)
    }
}

class AddressViewModel : BaseViewModel() {
    private var locationClient: AMapLocationClient? = null
    private var mOption: AMapLocationClientOption? = null

    /**
     * 初始化定位
     */
    fun initLocation(@NonNull context: Context) {
        locationClient = AMapLocationClient(context)
        mOption = AMapLocationClientOption()
        //设置定位模式，高精度模式
        mOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mOption?.isNeedAddress = true    //置是否返回逆地理地址信息。默认是true
        mOption?.isOnceLocation = true   //设置是否单次定位
        locationClient?.setLocationListener { location ->
            location?.apply {
                if (errorCode == 0) {   //errCode等于0代表定位成功，其他的为定位失败
                    if (latitude > 0 && longitude > 0 && !street.isNullOrEmpty()) {
                        val lp = LatLonPoint(latitude, longitude)
                        val query = PoiSearch.Query("", "", street)
                        query.pageSize = 20
                        query.pageNum = 0

                        val poiSearch = PoiSearch(context, query)
                        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                            override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                                if (p1 == AMapException.CODE_AMAP_SUCCESS) {
                                    if (p0 != null && p0.query != null) { // 搜索poi的结果
                                        if (p0.query == query) {
                                            // 取得第一页的poiitem数据，页数从数字0开始
                                            val poiItems = p0.pois
                                            // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                                            val suggestionCitys = p0.searchSuggestionCitys
                                            if (poiItems != null && poiItems.size > 0) {
                                                LogUtils.log("获取的定位信息", "poi列表-- " + Gson().toJson(poiItems))
                                            } else if (suggestionCitys != null && suggestionCitys.size > 0) {
                                                LogUtils.log("获取的定位信息", "poi列表2-- " + Gson().toJson(suggestionCitys))
                                            } else {
                                                ToastUtils.show(context, "对不起，没有搜索到相关数据！")
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                            }
                        })
                        // 设置搜索区域为以lp点为圆心，其周围1000米范围
                        poiSearch.bound = PoiSearch.SearchBound(lp, 1000, true)
                        poiSearch.searchPOIAsyn()
                    }
                }
            }
        }
    }

    /**
     * 启动定位
     */
    fun startLocation() {
        locationClient?.setLocationOption(mOption)
        locationClient?.startLocation()
    }

    override fun onCleared() {
        super.onCleared()
        if (locationClient != null) {
            locationClient?.onDestroy()
            locationClient = null
            mOption = null
        }
    }
}