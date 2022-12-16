package com.coinlive.chat.firebase.service

import androidx.annotation.Keep
import com.coinlive.chat.exception.FirebaseIdTokenException
import com.coinlive.chat.exception.UnknwonExecption
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Coinlive Firebase Authentication와 관련된 class입니다.
 */
@Keep
object CoinliveAuthentication {


    /**
     * Coinlive Firebase Authentication에 Custom Token을 가지고 Signing을 합니다.
     * @param[customToken] [ChattingMemberRepository.getCustomToken] 을 이용해서 받은 값을 말합니다.
     * @return[String] 사용자의 User[FirebaseUser]의 uid를 반환합니다.
     * @throws [FirebaseIdTokenException] Firebase Authentication에 로그인한 유저가 없을 경우 발생합니다.
     * @throws [UnknwonExecption] Firebase Authentication에 로그인 중 Exception이 발생할 경우 발생됩니다.
     * @see ChattingMemberRepository.getCustomToken
     */
    suspend fun signInWithCustomToken(customToken: String): String{
        try {
            signOutFirebase()
            Firebase.auth.signInWithCustomToken(customToken).await()
            return Firebase.auth.currentUser?.uid ?: throw FirebaseIdTokenException("CoinliveAuthentication.signInWithCustomToken error")
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknwonExecption("CoinliveAuthentication.signIn error")
        }
    }

    /**
     * Coinlive Firebase Authentication에 익명 로그인을 합니다.
     * 대게 사용자가 비회원일 경우 사용됩니다.
     * @return[String] 사용자의 User[FirebaseUser] uid를 반환합니다.
     * @throws [FirebaseIdTokenException] Firebase Authentication에 로그인한 유저가 없을 경우 발생합니다.
     * @throws [UnknwonExecption] Firebase Authentication에 로그인 중 Exception이 발생할 경우 발생됩니다.
     */
    suspend fun signInWithUnknownUser() : String{
        try {
            signOutFirebase()
            Firebase.auth.signInAnonymously().await()
            return Firebase.auth.currentUser?.uid ?: throw FirebaseIdTokenException("CoinliveAuthentication.signInWithUnknownUser error")
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknwonExecption("CoinliveAuthentication.signInWithUnknownUser error")
        }
    }

    /**
     * Firebase Authentication 로그인 유저가 익명 로그인의 유무를 전달합니다.
     * 이 function을 사용하기 위해서는 [signInWithCustomToken] 또는 [signInWithUnknownUser]가 선행되어야 합니다.
     * @return[Boolean] 현재 Firebase Authentication에 로그인된 유저가 익명 로그인의 유무를 전달합니다.
     * @throws [FirebaseIdTokenException] Firebase Authentication에 로그인한 유저가 없을 경우 발생합니다.
     */
    fun isAnonymously() : Boolean {
        val currentUser: FirebaseUser = Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.isAnonymously error")
        return currentUser.isAnonymous
    }

    /**
     * Coinlive Firebase Authentication에 로그아웃 합니다.
     * @throws [UnknwonExecption] Firebase Authentication에 로그아웃 Exception이 발생할 경우 발생됩니다.
     */
    fun signOutFirebase(){
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknwonExecption("CoinliveAuthentication.signOutFirebase error")
        }
    }

    /**
     * Firebase Authentication 로그인 유저의 IdToken을 전달합니다.
     * 이 function을 사용하기 위해서는 [signInWithCustomToken]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 IdToken을 전달합니다. 로그인이 되어있지 않다면 null이 전달됩니다.
     * @throws [FirebaseIdTokenException] Firebase Authentication에 로그인한 유저가 없을 경우 발생합니다.
     * @throws [UnknwonExecption] Firebase Authentication에 로인한 유저의 idToken값이 없거나 idToken값을 가져오는 중 Exception이 발생할 경우
     * 발생합니다.
     */
    suspend fun getFirebaseToken(): String {
        val currentUser: FirebaseUser =
            Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.getFirebaseToken error")
        try {
            val result = currentUser.getIdToken(true).await()
            return result.token
                ?: throw UnknwonExecption("CoinliveAuthentication.getFirebaseToken error")
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknwonExecption("CoinliveAuthentication.getFirebaseToken error")
        }
    }

    /**
     * Firebase Authentication 로그인 유저의 Firebase UUId을 전달합니다.
     * 이 function을 사용하기 위해서는 [signInWithCustomToken]이 선행되어야 합니다.
     * @return[String] 현재 Firebase Authentication에 로그인된 유저의 UUId을 전달합니다.
     * @throws [FirebaseIdTokenException] Firebase Authentication에 로그인한 유저가 없을 경우 발생합니다.
     */
    fun getFirebaseUuid(): String {
        val currentUser: FirebaseUser = Firebase.auth.currentUser ?: throw FirebaseIdTokenException("CoinliveAuthentication.getFirebaseUuid error")
        return currentUser.uid
    }


}