package app.gambit.manager.installer.step.patching

import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import app.gambit.manager.R
import app.gambit.manager.domain.manager.PreferenceManager
import app.gambit.manager.installer.step.Step
import app.gambit.manager.installer.step.StepGroup
import app.gambit.manager.installer.step.StepRunner
import app.gambit.manager.installer.step.download.DownloadBaseStep
import app.gambit.manager.installer.util.ManifestPatcher
import org.koin.core.component.inject
import java.io.File

/**
 * Modifies each APKs manifest in order to change the package and app name as well as whether or not its debuggable
 */
class PatchManifestsStep : Step() {

    private val preferences: PreferenceManager by inject()

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_patch_manifests

    override suspend fun run(runner: StepRunner) {
        val baseApk = runner.getCompletedStep<DownloadBaseStep>().workingCopy
        val workingDir = baseApk.parentFile ?: throw IllegalStateException("Working directory parent is null")
        val apks = workingDir.listFiles { _, name -> name.endsWith(".apk") && name != "xposed.apk" }
            ?: emptyArray()

        apks.forEach { apk ->
            runner.logger.i("Reading AndroidManifest.xml from ${apk.name}")
            val manifest = ZipReader(apk)
                .use { zip -> zip.openEntry("AndroidManifest.xml")?.read() }
                ?: throw IllegalStateException("No manifest in ${apk.name}")

            ZipWriter(apk, true).use { zip ->
                runner.logger.i("Changing package and app name in ${apk.name}")
                val patchedManifestBytes = if (apk.name == baseApk.name) {
                    ManifestPatcher.patchManifest(
                        manifestBytes = manifest,
                        packageName = preferences.packageName,
                        appName = preferences.appName,
                        debuggable = preferences.debuggable,
                    )
                } else {
                    runner.logger.i("Changing package name in ${apk.name}")
                    ManifestPatcher.renamePackage(manifest, preferences.packageName)
                }

                runner.logger.i("Deleting old AndroidManifest.xml in ${apk.name}")
                zip.deleteEntry(
                    "AndroidManifest.xml",
                    /* fillVoid = */ apk.name.contains("config.arm") || apk.name == baseApk.name
                ) // Preserve alignment in base and library/architecture splits

                runner.logger.i("Adding patched AndroidManifest.xml in ${apk.name}")
                zip.writeEntry("AndroidManifest.xml", patchedManifestBytes)
            }
        }
    }

}