package com.example.client.common.dialog

import android.app.Dialog
import androidx.databinding.ViewDataBinding

interface IDialogService<D : ViewDataBinding> {

    fun getLayoutId(): Int

    fun getDialog(): Dialog

    fun init(title: String?, viewDataBinding: D, dialog: Dialog)

    fun show(dialog: Dialog, viewDataBinding: D)

}