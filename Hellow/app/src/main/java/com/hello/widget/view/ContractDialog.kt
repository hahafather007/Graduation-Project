package com.hello.widget.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.hello.R

class ContractDialog(context: Context?) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_contract_us)
        setCanceledOnTouchOutside(true)
    }
}