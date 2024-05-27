package com.schuanhe.auto_redbook.api


import com.schuanhe.auto.core.api.printLayoutInfo
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.text

//@RequiresApi(Build.VERSION_CODES.N)
suspend fun redBookGo(includeInvisible: Boolean = true) {
    SF.text("全部bitmap").require(2000).apply {
        tryClick()
        SF.text("最新").require(2000).apply {
            tryClick()
            printLayoutInfo()
        }
    }

}