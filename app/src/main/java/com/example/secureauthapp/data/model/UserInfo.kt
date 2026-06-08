package com.example.secureauthapp.data.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("sub")
    val sub: String? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("given_name")
    val givenName: String? = null,
    
    @SerializedName("family_name")
    val familyName: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("email_verified")
    val emailVerified: Boolean? = null,
    
    @SerializedName("preferred_username")
    val preferredUsername: String? = null,
    
    @SerializedName("picture")
    val picture: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: Long? = null
)
