package ru.xmn.randompoem.model

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.reactivex.functions.Cancellable
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.application.di.scopes.ActivityScope
import javax.inject.Inject
import javax.inject.Singleton

interface PoemsRepository {
    fun poetWithPoems(poet: Poet): Single<Poet>
    fun poets(): Single<List<Poet>>
}

public class FirebasePoemsRepository constructor(context: Context) : PoemsRepository {

    var db: FirebaseDatabase

    init {
        FirebaseApp.initializeApp(context)
        db = FirebaseDatabase.getInstance()
    }

    override fun poetWithPoems(poet: Poet): Single<Poet> {
        return Single.create<Poet> {
                println("poetWithPoems ${it.isDisposed}")

                db.getReference("poems/${poet.id}").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        val list = (snapshot?.children ?: emptyList()).map { toPoem(it) }.filter { it != null }.map { it!! }
                        it.onSuccess(poet.copy(poems = list))
                        println("poetWithPoems onDataChange")
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
                    return Poet(poetFb.id, poetFb.name, poetFb.century?:0, emptyList())
                }

            })
        })
    }
}

@Module
class PoemsNetworkModule {
    @Provides @Singleton
    fun providePoemsRepository(context: Context): PoemsRepository = FirebasePoemsRepository(context)
}