package com.aytas.notesapp.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.hardware.input.InputManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS

fun View.hideKeyboard()=(context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
    .hideSoftInputFromWindow(windowToken,HIDE_NOT_ALWAYS)