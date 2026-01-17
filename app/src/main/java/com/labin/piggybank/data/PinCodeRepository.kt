package com.labin.piggybank.data

import com.labin.piggybank.api.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor (
    private val api: AuthApi
)