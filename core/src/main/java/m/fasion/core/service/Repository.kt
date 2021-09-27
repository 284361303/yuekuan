package m.fasion.core.service


class Repository(val api: Service) {

    suspend fun getLogin(maps:MutableMap<String,String>){
        api.getLogin(maps)
    }
}