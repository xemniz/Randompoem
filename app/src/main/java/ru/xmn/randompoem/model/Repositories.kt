package ru.xmn.randompoem.model

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.queryAllAsFlowable
import com.vicpin.krealmextensions.save
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import javax.inject.Singleton

interface PoemsRepository {
    fun poetWithPoems(poet: Poet): Single<Poet>
    fun poets(): Single<List<Poet>>
}

class FirebasePoemsRepository constructor(context: Context) : PoemsRepository {

    var db: FirebaseDatabase

    init {
        FirebaseApp.initializeApp(context)
        db = FirebaseDatabase.getInstance()
    }

    override fun poetWithPoems(poet: Poet): Single<Poet> {
        return Single.create<Poet> {
            db.getReference("poems/${poet.id}").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    val list = (snapshot?.children ?: emptyList()).map { toPoem(it) }.filter { it != null }.map { it!! }
                    it.onSuccess(poet.copy(poems = list))
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                }

                private fun toPoem(snapshot: DataSnapshot?): Poem? {
                    val poemFB = snapshot?.getValue(PoemFB::class.java)
                    if (poemFB?.id == null || poemFB.title == null || poemFB.text == null) return null
                    return Poem(poemFB.id, poet.copy(), poemFB.title, poemFB.text, "")
                }
            })
        }

    }

    override fun poets(): Single<List<Poet>> {
        return Single.create<List<Poet>>({
            db.getReference("poets").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    val list = (snapshot?.children ?: emptyList()).map { toPoet(it) }.filter { it != null }.map { it!! }
                    it.onSuccess(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                }

                private fun toPoet(item: DataSnapshot): Poet? {
                    val poetFb = item.getValue(PoetFB::class.java)
                    if (poetFb?.id == null || poetFb.name == null) return null
                    return Poet(poetFb.id, poetFb.name, poetFb.century ?: 0, emptyList())
                }

            })
        })
    }
}

@Module
class PoemsNetworkModule {
    @Provides
    @Singleton
    fun providePoemsRepository(context: Context): PoemsRepository = FirebasePoemsRepository(context)
}

interface CatalogRepository {
    fun getCurrentIgnoredPoetIdsList(): Single<List<String>>
    fun unignorePoet(id: String)
    fun ignorePoet(id: String)
    fun unignoreAllPoets()
}

class RealmCatalogRepository : CatalogRepository {
    override fun unignoreAllPoets() {
        RealmIgnoredPoetId().deleteAll()
    }

    override fun unignorePoet(id: String) {
        RealmIgnoredPoetId().delete { it.equalTo("id", id) }
    }

    override fun ignorePoet(id: String) {
        RealmIgnoredPoetId().apply { this.id = id }.save()
    }

    override fun getCurrentIgnoredPoetIdsList(): Single<List<String>> {
        return RealmIgnoredPoetId()
                .queryAllAsFlowable()
                .first(ArrayList())
                .map { it.map { it.id!! } }
    }
}

open class RealmIgnoredPoetId : RealmObject() {
    @PrimaryKey
    var id: String? = null
}

@Module
class CatalogRepositoryModule {
    @Provides
    @Singleton
    fun provideCatalogRepository(): CatalogRepository = RealmCatalogRepository()
}