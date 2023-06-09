package per.chowhound.plugin.mirai

import cn.hutool.core.util.StrUtil
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import per.chowhound.plugin.mirai.academic.Academic
import per.chowhound.plugin.mirai.academic.ClassScheduleConfig
import per.chowhound.plugin.mirai.mirai.ScheduleMessages
import per.chowhound.plugin.mirai.util.HutoolClassUtil
import per.chowhound.plugin.mirai.util.JacksonUtil
import per.chowhound.plugin.mirai.util.Logger.logError
import per.chowhound.plugin.mirai.util.Logger.logInfo
import per.chowhound.plugin.mirai.util.Logger.logWarn
import per.chowhound.plugin.mirai.util.SqlDateUtil

/**
 * @Author: Chowhound
 * @Date: 2023/4/14 - 15:40
 * @Description: 插件主类
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ClassSchedule : KotlinPlugin(JvmPluginDescription.loadFromResource()) {
    const val configFileName = "config.json"
    val academicMap: MutableMap<String, Academic> = mutableMapOf()
    var config: ClassScheduleConfig? = null


    override fun PluginComponentStorage.onLoad() {
        HutoolClassUtil.scanPackageBySuper("per.chowhound.plugin.mirai.academic", Academic::class.java).forEach{
            val instance = it.constructors[0].newInstance() as Academic
            academicMap[instance.prefix.uppercase()] = instance
        }

        // 读取配置文件
        config = resolveConfigFile(configFileName).let {
            if (!it.exists()) {
                logInfo("配置文件${it.name}不存在，正在创建...")
                it.createNewFile()
                ClassScheduleConfig().apply {// 默认配置
                    JacksonUtil.objectMapper.writeValue(it, this)
                    logInfo("配置文件${it.name}创建成功")
                }
            } else if (it.exists() && it.isDirectory) {
                logError("配置文件${it.name}存在，但是不是文件")
                return
            } else{
                logInfo("配置文件${it.name}存在，正在读取...")
                JacksonUtil.objectMapper.readValue(it, ClassScheduleConfig::class.java)
            }
        }

        logInfo("master: ${config!!.master ?: "暂无"}")
    }
    override fun onEnable() {
        CommandManager.registerCommand(MyCommand())

    }

//    override fun onDisable() {
//        logger.info { "ClassSchedule disabled" }
//    }


    /**
     * @Author: Chowhound
     * @Date: 2023/4/14 - 15:40
     * @Description: 命令具体实现
     */
    class MyCommand : RawCommand(
        owner = ClassSchedule,
        primaryName = "class",
        description = "测试命令",
        usage = "/class [agr1](周:w,日:d) [agr2](周:1-20,默认本周,+下周,-上周)|(日:1-7,默认今天,+明天,-昨天)", // /class d 1本周一, /class w +下周, /class d 今天
        prefixOptional = true,
        secondaryNames = arrayOf("cl")
    ) {
        init {
            logInfo(this.permission.id.toString())
        }
        override suspend fun CommandContext.onCommand(args: MessageChain) {
            // 如果是控制台命令则sender.user == null，默认返回第一个
            val user = config!!.users?.find { sender.user == null || it.qq == sender.user!!.id }
            if (user == null) {
                logInfo("未找到对应的用户 user: ${sender.user?.id}")
                sender.sendMessage("未找到对应的用户")
                return
            }

             if(args.size in 1..2) {
                 // 获取对应的academic实例
                 val academic = academicMap[user.school!!.uppercase()] ?: run {
                     logInfo("未找到对应的学校 school: ${user.school?.uppercase()}")
                     sender.sendMessage("未找到对应的学校 school: ${user.school?.uppercase()}")
                     return
                 }
                 val schedules = academic.getSchedules(user.username!!)

                 // 获取符号对应的数字
                 fun getSymbolCode(str: String) = when(str){
                     "+" -> 1
                     "-" -> -1
                     else -> 0
                 }
                 // 解析参数。并获取对应课表
                 val scheduleList = when(args[0].content.uppercase()){
                     "W" -> { // 某一周课表
                         val weekIndex = args.getOrNull(1)?.run {
                             if (StrUtil.isNumeric(this.content)) { // 如果是数字,则直接返回即为周次
                                 return@run this.content.toInt()
                             }else{ // 如果是符号,则返回周次+符号对应的数字 由fun getSymbolCode(str: String)获取
                                 return@run schedules.getWeekIndex() + getSymbolCode(this.content)
                             }
                         }
                         schedules.getByWeek(weekIndex)
                     }
                     "D" -> {
                         val date = args.getOrNull(1)
                             ?.run { SqlDateUtil.getDateReact(getSymbolCode(this.content)) }
                         schedules.getByDate(date)
                     }

                     else -> { emptyList() }
                 }

                 ScheduleMessages.buildMsg(scheduleList).forEach{ sender.sendMessage(it)}
             }else{
                 logWarn("错误的参数长度, 需要 1 - 2 , 实际${args.size}")
             }

        }
    }
}

///**
// * @Author: Chowhound
// * @Date: 2023/4/14 - 15:41
// * @Description: 获取课表对应的消息列表, 用于发送
// */
//fun Academic.getSchedulesMsgList(username: String): List<String>{
//    logInfo("正在获取${username}的课表...")
//    val schedules = getSchedules(username)
//
//    val messagesList = mutableListOf<String>()
//    var now: Date? = null // 当前正在处理的日期
//    var msg = StringBuilder()
//
//    // 先根据date升序排，再根据start升序排
//    schedules.sortedWith { o1, o2 ->
//        if (o1.date == o2.date) {
//            o1.start.compareTo(o2.start)
//        } else {
//            o1.date.compareTo(o2.date)
//        }
//    }.forEach{
//        val th = it.date // 当前schedule消息的日期
//
//        if (now == null || th != now){ // today为空或者th不等于today，说明是新的一天
//            if (now != null){ // today不为空，说明不是第一次进入循环，需要将上一天的消息添加到消息列表中，然后清空msg
//                messagesList.add(msg.toString())
//                msg = StringBuilder()
//            }
//
//            val i: Int = it.date.toLocalDate().get(ChronoField.DAY_OF_WEEK)
//            msg.append(DateUtil.format(th, "MM-dd") + " 周" + WeekUtil.WEEK_DAY[i - 1] + ":")
//            now = th
//        }
//        if (th == now) {
//            msg.append(it.toMessage())
//        }
//    }
//    return messagesList
//}


