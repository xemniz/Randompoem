package ru.xmn.randompoem.common.realmext

import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery

/**
 * Created by victor on 2/1/17.
 * Extensions for Realm. All methods here return observables.
 */

/**
 * Queries for entities in database asynchronously, and observe changes returning an observable.
 */
fun <T : RealmObject> T.queryOneResultAsObservable(query: (RealmQuery<T>) -> Unit): Observable<List<T>> {

    return prepareObservableQuery { realm, subscriber ->
        val realmQuery = RealmQuery.createQuery(realm, this.javaClass)
        query(realmQuery)

        Observable.fromCallable {
            realmQuery.findAll()
                    .filter { it.isLoaded }
                    .map { realm.copyFromRealm(it) }
        }
                .subscribe({
                    subscriber.onNext(it)
                    subscriber.onComplete()
                }, { subscriber.onError(it) })
    }

}

class BackgroundThread : HandlerThread("Scheduler-Realm-BackgroundThread",
        Process.THREAD_PRIORITY_BACKGROUND)

fun <T : Any> prepareObservableQuery(closure: (Realm, ObservableEmitter<in T>) -> Disposable): Observable<T> {
    var realm: Realm? = null
    var mySubscription: Disposable? = null

    var backgroundThread: HandlerThread? = null
    var looper: Looper = if (Looper.myLooper() != Looper.getMainLooper()) {
        backgroundThread = BackgroundThread()
        backgroundThread.start()
        backgroundThread.looper
    } else {
        Looper.getMainLooper()
    }

    return Observable.defer {
        Observable.create(ObservableOnSubscribe<T> {

            realm = Realm.getDefaultInstance()
            mySubscription = closure(realm!!, it)
        }).doOnSubscribe({
            realm?.close()
            mySubscription?.dispose()
            backgroundThread?.interrupt()

        }).unsubscribeOn(AndroidSchedulers.from(looper))
                .subscribeOn(AndroidSchedulers.from(looper))
    }
}
