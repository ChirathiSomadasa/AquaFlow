package com.chirathi.aquaflow.NotificationService


import com.google.auth.oauth2.GoogleCredentials
import java.io.FileInputStream

object FirebaseUtils {
    fun getAccessToken(): String {
        val googleCredentials = GoogleCredentials
            .fromStream(FileInputStream("secrets/aquaflow-e93c7-firebase-adminsdk-42ldy-15117638df.json"))
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}
