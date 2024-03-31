package com.example.ksp_annotation

import kotlin.reflect.KClass

annotation class VMTransition(val host: KClass<*>, val target: String)
