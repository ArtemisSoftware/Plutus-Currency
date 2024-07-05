package di

import com.russhwolf.settings.Settings
import data.CurrencyApiServiceImpl
import data.repository.MongoRepositoryImpl
import data.repository.PreferencesRepositoryImpl
import domain.CurrencyApiService
import domain.repository.MongoRepository
import domain.repository.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.home.HomeViewModel

val appModule = module {
    single { Settings() }
    single<MongoRepository> { MongoRepositoryImpl() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
    factory {
        HomeViewModel(
            preferencesRepository = get(),
            mongoRepository = get(),
            api = get()
        )
    }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}