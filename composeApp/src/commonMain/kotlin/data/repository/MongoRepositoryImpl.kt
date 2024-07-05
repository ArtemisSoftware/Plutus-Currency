package data.repository

import data.CurrencyEntity
import domain.RequestState
import domain.repository.MongoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoRepositoryImpl : MongoRepository {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(CurrencyEntity::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: CurrencyEntity) {
        realm?.write { copyToRealm(currency) }
    }

    override fun readCurrencyData(): Flow<RequestState<List<CurrencyEntity>>> {
        return realm?.query<CurrencyEntity>()
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(data = result.list)
            }
            ?: flow { RequestState.Error(message = "Realm not configured.") }
    }

    override suspend fun cleanUp() {
        realm?.write {
            val currencyCollection = this.query<CurrencyEntity>()
            delete(currencyCollection)
        }
    }
}