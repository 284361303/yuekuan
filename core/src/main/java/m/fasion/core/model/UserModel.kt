package m.fasion.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val avatar: String,
    val nickname: String,
    val status: String,
    val token: String,
    val uid: String,
    val phone: String
) : Parcelable


class BannerModel : ArrayList<BannerModelItem>()

@Parcelize
data class BannerModelItem(
    val body: List<Body>,
    val head: List<Head>,
    val id: String,
    val title: String,
    val type: String
) : Parcelable

@Parcelize
data class Body(
    val favourite: Boolean,
    val head_img: String,
    val target: String,
    val type: String,
    val title: String
) : Parcelable

@Parcelize
data class Head(
    val favourite: Boolean,
    val link: String,
    val path: String,
    val type: String
) : Parcelable

/**
 * 首页的款式列表Start
 */
@Parcelize
data class ClothesList(
    val clothes_list: List<Clothes>,
    val current_page: Int,
    val total_count: String,
    val total_page: Int
) : Parcelable

@Parcelize
data class Clothes(
    var favourite: Boolean,
    val head: String,
    val head_img: String,
    val id: String,
    val num: String
) : Parcelable
/**
 * 首页的款式列表End
 */

/**衣服详情*/
data class ClothesInfo(
    val body: String,
    val body_img_list: List<BodyImg>,
    val created_at: Long,
    var favourite: Boolean,
    val head: String,
    val head_img_list: List<HeadImg>,
    val id: String,
    val num: Int,
    val shop_url: String,
    val status: String,
    val tags: List<Any>,
    val title: String
)

data class BodyImg(
    val favourite: Boolean,
    val link: String,
    val path: String,
    val type: String
)

data class HeadImg(
    val favourite: Boolean,
    val link: String,
    val path: String,
    val type: String
)

/*收藏列表数据*/
data class FavoritesListData(
    val `data`: List<Data>,
    val links: Links,
    val total: Int
)

data class Data(
    val body: List<FavoritesBody>,
    val head: List<FavoritesHead>,
    val id: String,
    val title: String
)

data class Links(
    val last: String
)

data class FavoritesBody(
    val id: String,
    val target: Target,
    val type: String
)

data class FavoritesHead(
    val id: String,
    val target: TargetX,
    val type: String
)

data class Target(
    val path: String
)

data class TargetX(
    val path: String
)

//获取用户信息
@Parcelize
data class UserInfo(
    val avatar: String,
    val id: String,
    val nickname: String,
    val phone: String,
    val status: String
) : Parcelable

/**今日推荐页面数据*/
data class RecommendList(
    val body: List<Body>,
    val head: List<Head>,
    val id: String,
    val title: String,
    val type: String
)
/*筛选条件列表*/
class Categories : ArrayList<CategoriesItem>()

data class CategoriesItem(
    val child_list: List<Child>,
    val id: String,
    val name: String
)

data class Child(
    val child_list: List<ChildX>,
    val id: String,
    val name: String
)

data class ChildX(
    val id: String,
    val name: String
)