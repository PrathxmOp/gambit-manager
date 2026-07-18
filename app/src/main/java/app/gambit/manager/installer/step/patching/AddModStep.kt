package app.gambit.manager.installer.step.patching

import app.gambit.manager.BuildConfig
import app.gambit.manager.R
import app.gambit.manager.installer.step.Step
import app.gambit.manager.installer.step.StepGroup
import app.gambit.manager.installer.step.StepRunner
import app.gambit.manager.installer.step.download.DownloadModStep
import java.io.File

/**
 * Uses LSPatch to inject the Gambit XPosed module into Chess.com
 *
 * @param signedDir The signed apks to patch
 * @param lspatchedDir Output directory for LSPatch
 */
class AddModStep(
    private val signedDir: File,
    private val lspatchedDir: File
) : Step() {

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_add_mod

    override suspend fun run(runner: StepRunner) {
        val mod = runner.getCompletedStep<DownloadModStep>().workingCopy

        runner.logger.i("Adding ${BuildConfig.MOD_NAME}Xposed module with LSPatch")
        val files = signedDir.listFiles()
            ?.takeIf { it.isNotEmpty() }
            ?: throw Error("Missing APKs from signing step")

        app.gambit.manager.installer.util.Patcher.patch(
            runner.logger,
            outputDir = lspatchedDir,
            apkPaths = files.map { it.absolutePath },
            embeddedModules = listOf(mod.absolutePath)
        )
    }

}