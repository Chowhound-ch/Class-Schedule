package per.chowhound.plugin.mirai

import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import java.nio.file.Paths

class TestClass {

}

@OptIn(ConsoleExperimentalApi::class)
fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon(
        MiraiConsoleImplementationTerminal(rootPath = Paths.get(System.getProperty("user.dir", ".") + "\\debug-sandbox").toAbsolutePath())
    )
}