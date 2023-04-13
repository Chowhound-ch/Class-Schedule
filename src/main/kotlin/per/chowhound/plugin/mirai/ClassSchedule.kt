package per.chowhound.plugin.mirai

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import per.chowhound.plugin.mirai.academic.HFUTAcademic
import per.chowhound.plugin.mirai.util.logError
import per.chowhound.plugin.mirai.util.logInfo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ClassSchedule : KotlinPlugin(JvmPluginDescription.loadFromResource()) {
    val academicFolder = "${dataFolder.absolutePath}/academic"

    init {
        Path(academicFolder).apply {
            if (!this.exists()) {
                logInfo("academic文件夹不存在, 创建文件夹")
                Files.createDirectory(this)
            }else if(!this.isDirectory()) {
                logError("academic文件夹不是文件夹, 请手动删除或者更改文件夹名称")
            }
        }
    }

    override fun onEnable() {
        HFUTAcademic().refresh("2021218222", "Hh636910944..")

        logger.info { "" }
    }
}
