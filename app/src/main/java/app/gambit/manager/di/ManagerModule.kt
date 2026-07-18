package app.gambit.manager.di

import app.gambit.manager.domain.manager.DownloadManager
import app.gambit.manager.domain.manager.InstallManager
import app.gambit.manager.domain.manager.PreferenceManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::DownloadManager)
    singleOf(::PreferenceManager)
    singleOf(::InstallManager)
}