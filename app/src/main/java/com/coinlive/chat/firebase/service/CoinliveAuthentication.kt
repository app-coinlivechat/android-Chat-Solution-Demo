package com.coinlive.chat.firebase.service

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Coinlive Firebase를 이용하기 위한 클래스입니다.
 */
object CoinliveAuthentication {

    /**
     * Coinlive Firebase Authentication에 로그인을 합니다.
     * @param[customToken] [ChattingMemberRepository.getCustomToken] 을 이용해서 받은 값을 말합니다.
     * @return[FirebaseUser] Firebase Authentication에 정상적으로 로그인이 됐다면 null이 아닌 값이 전달 됩니다.
     * @see com.coinlive.chat.api.repository.ChattingMemberRepository.getCustomToken
     */
    suspend fun signIn(customToken: String): FirebaseUser? {
        return Firebase.auth.signInWithCustomToken(customToken).await().user
    }

    /**
     * Coinlive Firebase Authentication에 로그아웃 합니다.
     */
    fun signOut() {
        Firebase.auth.signOut()
    }

    /**
     * Firebase Authentication 로그인 유저의 IdToken을 전달합니다.
     * 이 function을 사용하기 위해서는 [signIn]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 IdToken을 전달합니다. 로그인이 되어있지 않다면 null이 전달됩니다.
     */
    fun getFirebaseToken(): String? {
        val currentUser: FirebaseUser = Firebase.auth.currentUser ?: return null
        return currentUser.getIdToken(true).result.token
    }

    /**
     * Firebase Authentication 로그인 유저의 Firebase UUId을 전달합니다.
     * 이 function을 사용하기 위해서는 [signIn]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 UUId을 전달합니다. 로그인이 되어있지 않다면 null이 전달됩니다.
     */
    fun getFirebaseUuid(): String? {
        val currentUser: FirebaseUser = Firebase.auth.currentUser ?: return null
        return currentUser.uid
    }
}