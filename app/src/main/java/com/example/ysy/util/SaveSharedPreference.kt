/**
 *
 *  REST API HELPER
 *  제작자 : 김승용
 *  마지막 업데이트 : 2021-02-09 18:40
 *
 * */

package com.example.ysy.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SaveSharedPreference {
    val PREF_TOKEN = "token"

    fun getSharedPreferences(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    // 토큰 저장
    fun setToken (ctx: Context, token: String) {
        var editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()

        editor.putString(PREF_TOKEN, token)
        editor.commit()
    }

    // 토큰 가져오기
    fun getToken (ctx: Context): String {
        return getSharedPreferences(ctx).getString(PREF_TOKEN, "").toString()
    }

    // 로그아웃
    fun clearToken (ctx: Context) {
        var editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()

        editor.clear()
        editor.commit()
    }
}