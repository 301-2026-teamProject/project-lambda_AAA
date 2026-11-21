package com.example.lambda_aaa.model
import com.google.firebase.Timestamp
import java.util.ArrayList

internal object ProfileFirestoreMapper {
    fun encode(p: UserProfile): Map<String, Any?> = mapOf(
        "deviceId" to p.deviceId,
        "name" to p.name,
        "email" to p.email,
        "phone" to p.phone,
        "profilePhotoUrl" to p.profilePhotoUrl,
        "roles" to p.roles,
        "notificationsEnabled" to p.notificationsEnabled,
        "isOwner" to p.isOwner,
        "creationDate" to p.creationDate
    )

    fun decode(id: String, fields: Map<String, Any?>): UserProfile {
        val p = UserProfile(
            uid = id,
            deviceId = fields["deviceId"] as? String ?: "",
            name = fields["name"] as? String ?: "",
            email = fields["email"] as? String ?: "",
            phone = fields["phone"] as? String ?: "",
            profilePhotoUrl = fields["profilePhotoUrl"] as? String ?: "",
            roles = (fields["roles"] as? List<*>)?.map { Role.valueOf(it as String) }?.let { ArrayList(it) }?: arrayListOf(),
            notificationsEnabled = fields["notificationsEnabled"] as? Boolean ?: false,
            isOwner = fields["isOwner"] as? Boolean ?: false,
            creationDate = fields["creationDate"] as? Timestamp,
        )
        return p
    }
}