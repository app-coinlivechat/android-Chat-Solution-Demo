package com.coinlive.chat.firebase.service

import com.coinlive.chat.exception.FirebaseIdTokenException
import com.coinlive.chat.exception.UnknwonExecption
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Coinlive Firebase Authentication와 관련된 class입니다.
 */
//TODO ios와 메소드 주석 맞추기
object CoinliveAuthentication {


    /**
     * Coinlive Firebase Authentication에 로그인을 합니다.
     * @param[customToken] [ChattingMemberRepository.getCustomToken] 을 이용해서 받은 값을 말합니다.
     * @return[FirebaseUser] 사용자의 User[FirebaseUser] object를 리턴합니다.
     * @see ChattingMemberRepository.getCustomToken
     */
    suspend fun signIn(customToken: String): FirebaseUser {
        try {
            Firebase.auth.signInWithCustomToken(customToken).await()
            return Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.signIn error")
        } catch (e: Exception) {
            throw UnknwonExecption("CoinliveAuthentication.signIn error")
        }
    }

    /**
     * Coinlive Firebase Authentication에 로그아웃 합니다.
     */
    fun signOut() {
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            throw UnknwonExecption("CoinliveAuthentication.signOut error")
        }
    }

    /**
     * Firebase Authentication 로그인 유저의 IdToken을 전달합니다.
     * 이 function을 사용하기 위해서는 [signIn]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 IdToken을 전달합니다. 로그인이 되어있지 않다면 null이 전달됩니다.
     */
    fun getFirebaseToken(): String {
        val currentUser: FirebaseUser =
            Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.getFirebaseToken error")
        try {
            return currentUser.getIdToken(true).result.token
                ?: throw UnknwonExecption("CoinliveAuthentication.getFirebaseToken error" +
                        "error")
        } catch (e: Exception) {
            throw UnknwonExecption("CoinliveAuthentication.getFirebaseToken error")

        }
    }

    /**
     * Firebase Authentication 로그인 유저의 Firebase UUId을 전달합니다.
     * 이 function을 사용하기 위해서는 [signIn]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 UUId을 전달합니다.
     */
    fun getFirebaseUuid(): String {
        val currentUser: FirebaseUser = Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.getFirebaseUuid error")
        return currentUser.uid
    }
}