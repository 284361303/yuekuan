package m.fasion.ai.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import m.fasion.ai.R

class HomeBannerAdapter(private val context: Context, private val mList: List<HomeBannerModel>) :
    BannerAdapter<HomeBannerModel, HomeBannerAdapter.HomeBannerHolder>(mList) {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): HomeBannerHolder {
        return HomeBannerHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_home_banner, parent, false))
    }

    override fun onBindView(holder: HomeBannerHolder?, data: HomeBannerModel?, position: Int, size: Int) {
        holder?.let {
            Glide.with(context).load(data?.url).into(it.ivBg)
        }
    }

    class HomeBannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: ImageView = itemView.findViewById(R.id.itemHomeBanner_ivBg)
    }
}

data class HomeBannerModel(val id: String, val url: String)