package per.chowhound.plugin.mirai

import cn.hutool.core.util.ClassUtil
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import per.chowhound.plugin.mirai.academic.Academic
import per.chowhound.plugin.mirai.academic.ClassScheduleConfig
import per.chowhound.plugin.mirai.util.JacksonUtil
import per.chowhound.plugin.mirai.util.logError
import per.chowhound.plugin.mirai.util.logInfo

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ClassSchedule : KotlinPlugin(JvmPluginDescription.loadFromResource()) {
    const val configFileName = "config.json"
    val academicMap: MutableMap<String, Academic> = mutableMapOf()
    var config: ClassScheduleConfig? = null


    override fun PluginComponentStorage.onLoad() {
        ClassUtil.scanPackageBySuper("per.chowhound.plugin.mirai", Academic::class.java).forEach{
            val instance = it.constructors[0].newInstance() as Academic
            academicMap[instance.prefix] = instance
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

    }

    override fun onDisable() {
        logger.info { "ClassSchedule disabled" }
    }
}
