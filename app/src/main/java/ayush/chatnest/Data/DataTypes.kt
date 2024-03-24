package ayush.chatnest.Data

data class User(
    var userId : String? = "" ,
    val userEmail : String? = "",
    val phoneNumber : String? = "",
    val userPassword : String? = "",
    val name : String? = "",
    var imageUrl : String? = ""
){
    fun toMap() = mapOf(
        "userId" to userId,
        "userEmail" to userEmail,
        "phoneNumber" to phoneNumber ,
        "userPassword" to userPassword,
        "name" to name  ,
        "imageUrl" to imageUrl
    )
}