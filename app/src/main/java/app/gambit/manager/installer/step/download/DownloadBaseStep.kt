package app.gambit.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.gambit.manager.R
import app.gambit.manager.installer.step.StepStatus
import app.gambit.manager.installer.step.StepRunner
import app.gambit.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Loads the Chess.com base APK and split APKs from the cache directory
 */
@Stable
class DownloadBaseStep(
    private val dir: File,
    private val workingDir: File,
    private val version: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_base

    override val destination = dir.resolve("base-$version.apk")
    override val workingCopy = workingDir.resolve("base-$version.apk")

    override suspend fun run(runner: StepRunner) {
        runner.logger.i("Checking for cached Chess.com base APK at ${destination.absolutePath}")
        if (destination.exists() && destination.length() > 0) {
            runner.logger.i("Copying base APK to working directory")
            destination.copyTo(workingCopy, true)

            // Copy any accompanying split APKs if they exist in the cache directory
            val splits = dir.listFiles { _, name ->
                name.startsWith("config.") && name.endsWith("-$version.apk")
            }
            if (splits != null) {
                for (split in splits) {
                    runner.logger.i("Copying split APK: ${split.name}")
                    split.copyTo(workingDir.resolve(split.name), true)
                }
            }

            status = StepStatus.SUCCESSFUL
            return
        }

        throw Error("Chess.com base APK not found. Please place base-$version.apk in the cache folder: ${dir.absolutePath}")
    }

}