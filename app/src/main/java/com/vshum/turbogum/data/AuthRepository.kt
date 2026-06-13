package com.vshum.turbogum.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/** Thin wrapper around [FirebaseAuth] for email/password and Google sign-in. */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun signInWithEmail(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let(onSuccess) ?: onError(IllegalStateException("Не удалось войти"))
            }
            .addOnFailureListener(onError)
    }

    fun registerWithEmail(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let(onSuccess) ?: onError(IllegalStateException("Не удалось зарегистрироваться"))
            }
            .addOnFailureListener(onError)
    }

    fun signInWithGoogleAccount(account: GoogleSignInAccount, onSuccess: (FirebaseUser) -> Unit, onError: (Exception) -> Unit) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                result.user?.let(onSuccess) ?: onError(IllegalStateException("Не удалось войти через Google"))
            }
            .addOnFailureListener(onError)
    }

    fun signOut() {
        auth.signOut()
    }
}
