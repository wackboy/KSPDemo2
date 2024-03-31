package com.example.ksp_processor

import com.example.ksp_annotation.VMHolder
import com.example.ksp_annotation.VMTransition
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class VMHolderProcessor (
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbolsOfVMTransition = resolver.getSymbolsWithAnnotation(VMTransition::class.qualifiedName!!)
        val symbolsOfVMHolder = resolver.getSymbolsWithAnnotation(VMHolder::class.qualifiedName!!)

        val vmHolderList = symbolsOfVMHolder
            .map {
                it as KSClassDeclaration
            }.toList()
        if (vmHolderList.isNotEmpty()) {
            val nowType = vmHolderList[0].annotations.firstOrNull()?.arguments?.find { arg -> arg.name?.asString() == "host" }?.value as KSType

            val vmTranslationList = symbolsOfVMTransition
                .filter { it is KSPropertyDeclaration && it.validate() }
                .map {
                    it as KSPropertyDeclaration
                }.toList()
            val vmTranslateMap = vmTranslationList.groupBy {
                val parent = it.parent as KSClassDeclaration
                val key = "${parent.toClassName().simpleName}, ${parent.packageName.asString()}"
                key
            }
            vmTranslateMap.forEach {
                val classItem = it.value[0].parent as KSClassDeclaration
                val fileSpecBuilder = FileSpec.builder(
                    classItem.packageName.asString(),
                    "${classItem.toClassName().simpleName}VMGenerate"
                )
                val functionBuilder = FunSpec.builder("vmHolderInit")

                it.value.forEach { item ->
                    val symbolName = item.simpleName.asString()
                    val sourceVMClass = (item.annotations.firstOrNull()?.arguments?.find { arg -> arg.name?.asString() == "host" }?.value)
                    val sourceProperty = (item.annotations.firstOrNull()?.arguments?.find { arg -> arg.name?.asString() == "target" }?.value as? String)
                    functionBuilder.addParameter("owner", nowType.toClassName())
                    functionBuilder.addCode("""
                    val sourceVM = generate(${sourceVMClass}::class, owner)
                    val targetVM = generate(${classItem}::class, owner)
                    sourceVM.$sourceProperty.observe(owner as LifecycleOwner) { it ->
                        targetVM.$symbolName.value = it
                    }
                """.trimIndent())
                }
                val funCBuilder2 = FunSpec.builder("generate")
                val clazzType = ClassName("kotlin.reflect", "KClass")
                val viewModelType = ClassName("androidx.lifecycle", "ViewModel")
                val parameterType = ParameterSpec.builder("clazz", clazzType.parameterizedBy(TypeVariableName("T"))).build()
                val ownerType = ClassName("android.app", "Activity")
                val ownerParameter = ParameterSpec.builder("owner", ownerType).build()

                funCBuilder2
                    .addTypeVariable(TypeVariableName("T", viewModelType))
                    .addParameter(parameterType)
                    .addParameter(ownerParameter)
                    .addCode("""
                   return ViewModelProvider(owner as ViewModelStoreOwner).get(clazz.java)
            """.trimIndent())
                fileSpecBuilder
                    .addImport("androidx.lifecycle", "LifecycleOwner")
                    .addImport("androidx.lifecycle", "ViewModelProvider")
                    .addImport("androidx.lifecycle", "ViewModelStoreOwner")
                    .addFunction(functionBuilder.build())
                    .addFunction(funCBuilder2.build())
                    .build()
                    .writeTo(codeGenerator, false)
            }
        }
        return emptyList()

    }

}