package di

import com.russhwolf.settings.Settings
import data.CurrencyApiServiceImpl
import data.repository.PreferencesRepositoryImpl
import domain.CurrencyApiService
import domain.repository.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
//    single<MongoRepository> { MongoImpl() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
//    factory {
//        HomeViewModel(
//            preferences = get(),
//            mongoDb = get(),
//            api = get()
//        )
//    }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}