package com.schuanhe.auto_redbook.actions

import androidx.activity.ComponentActivity

/**
 * # Action
 *
 * Created on 2024/5/22
 * @author schuanhe
 */
abstract class Action {
    abstract val name: String
    abstract suspend fun run(act: ComponentActivity)

    override fun toString() = name

}