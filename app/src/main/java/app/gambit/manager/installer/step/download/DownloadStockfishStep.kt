package app.gambit.manager.installer.step.download

import android.os.Build
import androidx.compose.runtime.Stable
import app.gambit.manager.R
import app.gambit.manager.installer.step.download.base.DownloadStep
import java.io.File

@Stable
class DownloadStockfishStep(
    workingDir: File
): DownloadStep() {

    override val nameRes = R.string.step_dl_stockfish

    // Detect architecture preferred ABI
    private val abi = if (Build.SUPPORTED_ABIS.contains("arm64-v8a")) "arm64-v8a" else "armeabi-v7a"

    override val downloadFullUrl: String = "https://github.com/PrathxmOp/Prathxm-Patches/releases/download/database/libstockfish_$abi.so"
    override val destination = preferenceManager.moduleLocation.parentFile?.resolve("libstockfish_$abi.so")
        ?: File(preferenceManager.moduleLocation.parent, "libstockfish_$abi.so")
    override val workingCopy = workingDir.resolve("libstockfish.so")
}
