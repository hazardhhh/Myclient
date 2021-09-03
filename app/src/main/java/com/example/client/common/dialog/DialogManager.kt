package com.example.client.common.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

@Suppress("UNCHECKED_CAST")
@SuppressLint("StaticFieldLeak")

/**
 * @author: chenjiayou
 * @createBy: 2019/11/29
 * @descript: 基于DataBinding创建Dialog 新建dialog 需要实现IDialogService接口 用于自定义dialog视图
 */
class DialogManager {

    companion object {

        private var dialog: Dialog? = null

        private lateinit var viewDataBinding: ViewDataBinding
        private lateinit var service: IDialogService<*>

        fun dismiss() {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }

        fun <D : ViewDataBinding> create(
            context: Activity,
            title: String?,
            service: IDialogService<D>
        ): IDialogService<D> {
            val convertView = LayoutInflater.from(context).inflate(service.getLayoutId(), null)
            this.viewDataBinding = DataBindingUtil.bind<D>(convertView)!!
            this.dialog = service.getDialog()
            service.init(title, viewDataBinding as D, dialog!!)
            this.service = service
            return service
        }

        fun <D : ViewDataBinding> showByService(service: IDialogService<D>) {
            dismiss()
            service.show(dialog!!, this.viewDataBinding as D)
        }
    }
}