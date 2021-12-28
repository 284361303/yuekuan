package m.fasion.ai.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import m.fasion.ai.databinding.ItemHomeBannerBinding
import m.fasion.core.model.Body
import m.fasion.core.util.CoreUtil

class HomeBannerAdapter(mList: List<Body>) :
    BannerAdapter<Body, HomeBannerAdapter.HomeBannerHolder>(mList) {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): HomeBannerHolder {
        return HomeBannerHolder(ItemHomeBannerBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
    }

    override fun onBindView(holder: HomeBannerHolder, data: Body, position: Int, size: Int) {
        CoreUtil.setTypeFaceMedium(listOf(holder.binding.itemHomeBannerTv1))
        Glide.with(holder.itemView.context).load(data.head_img).into(holder.binding.itemHomeBannerIv)

        holder.binding.itemHomeBannerTv1.text = data.title
        holder.binding.itemHomeBannerTv2.text = data.sub_title
    }

    class HomeBannerHolder(val binding: ItemHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)
}