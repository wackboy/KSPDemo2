package com.example.ksp_annotation

import kotlin.reflect.KClass
import kotlin.reflect.KType

annotation class VMHolder(val agrs: Array<KClass<*>>, val host: KClass<*>)
