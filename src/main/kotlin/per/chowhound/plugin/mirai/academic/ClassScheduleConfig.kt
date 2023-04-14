package per.chowhound.plugin.mirai.academic

/**
 * @Author: Chowhound
 * @Date: 2023/4/14 - 12:18
 * @Description:
 */
data class ClassScheduleConfig(
    var users: MutableList<User>? = mutableListOf(),
    var master: Long? = null,
)

/**
 * @Author: Chowhound
 * @Date: 2023/4/14 - 12:26
 * @Description:
 */
data class User(
    var username: String? = "username",
    var password: String? = "password",
    var school: String? = "hfut"
)
