package app.gambit.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.gambit.manager.R
import app.gambit.manager.installer.step.download.base.DownloadStep
import java.io.File

@Stable
class DownloadLichessDbStep(
    workingDir: File
): DownloadStep() {

    override val nameRes = R.string.step_dl_lichess_db

    override val downloadFullUrl: String = "https://github.com/PrathxmOp/Prathxm-Patches/releases/download/database/lichess_offline_puzzles_50k.csv.gz"
    override val destination = preferenceManager.moduleLocation.parentFile?.resolve("lichess_offline_puzzles_50k.csv.gz")
        ?: File(preferenceManager.moduleLocation.parent, "lichess_offline_puzzles_50k.csv.gz")
    override val workingCopy = workingDir.resolve("temp_lichess_puzzles.gz")
}
