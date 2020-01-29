import net.styx.generator.CodeGen
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

class CodeGenTask extends DefaultTask {

    @Option(option = "packageName", description = "name space generated classes will have")
    @Input
    def packageName = "net.styx.model.v1"


    @Option(option = "targetDir", description = "file system directory where the soures will be generated to")
    @Input
    def targetDir = "./target/generated-sources/java"

    @TaskAction
    def generate() {
        CodeGen gen = new CodeGen()
        gen.packageName = this.packageName
        gen.generateTarget = new File(this.targetDir)
        gen.start()
    }
}