package com.vshum.turbogum.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.model.SeriesStats
import com.vshum.turbogum.model.UserProfile

/** Wrapper around the Firestore "users" collection. */
class UserRepository {

    private val usersCollection = FirebaseFirestore.getInstance().collection("users")

    fun getUser(uid: String, onResult: (UserProfile?) -> Unit, onError: (Exception) -> Unit) {
        usersCollection.document(uid).get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObject(UserProfile::class.java)?.copy(uid = snapshot.id))
            }
            .addOnFailureListener(onError)
    }

    fun createUserIfMissing(uid: String, nickname: String, onComplete: () -> Unit, onError: (Exception) -> Unit) {
        val docRef = usersCollection.document(uid)
        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    onComplete()
                } else {
                    val profile = UserProfile(uid = uid, nickname = nickname)
                    docRef.set(profile)
                        .addOnSuccessListener { onComplete() }
                        .addOnFailureListener(onError)
                }
            }
            .addOnFailureListener(onError)
    }

    fun updateNickname(uid: String, nickname: String, onComplete: () -> Unit, onError: (Exception) -> Unit) {
        usersCollection.document(uid).set(mapOf("nickname" to nickname), SetOptions.merge())
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener(onError)
    }

    fun updateStats(uid: String, percentMap: Map<String, Any>) {
        usersCollection.document(uid).set(percentMap, SetOptions.merge())
    }

    /** Recomputes series/total percentages from [owned] stickers and pushes them to Firestore. */
    fun syncStats(uid: String, owned: List<LinersFavourite>) {
        updateStats(uid, SeriesStats.percentMap(owned))
    }

    /** Realtime leaderboard ordered by [field] (one of [SeriesStats.TOTAL_PERCENT_KEY] or series keys). */
    fun observeLeaderboard(field: String, onUpdate: (List<UserProfile>) -> Unit): ListenerRegistration {
        return usersCollection.orderBy(field, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserProfile::class.java)?.copy(uid = doc.id)
                } ?: emptyList()
                onUpdate(list)
            }
    }
}
