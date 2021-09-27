package m.fasion.core.base

import androidx.lifecycle.ViewModel
import m.fasion.core.service.Repository
import m.fasion.core.service.Service

open class BaseViewModel : ViewModel() {

    protected var repository: Service = HttpUtils.getInstanse().getRetrofit().create(Service::class.java)

}