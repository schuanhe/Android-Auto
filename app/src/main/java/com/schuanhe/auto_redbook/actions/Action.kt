package com.schuanhe.auto_redbook.actions

import androidx.activity.ComponentActivity

/**
 * # Action
 *
 * Created on 2020/6/10
 * @author Vove
 */
abstract class Action {
    abstract val name: String
    abstract suspend fun run(act: ComponentActivity)

    override fun toString() = name

}