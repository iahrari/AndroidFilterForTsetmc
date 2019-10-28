package com.example.tsetmc.ui

import android.widget.EditText

fun EditText.isThereAnyKindOfError(): Boolean =
    if (this.text.toString() == ""){
        this.error = "این فیلد نمی تواند خالی باشد!"
        true
    } else false
