package com.biyao.optimizer.imageoptimizer

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * png jpg图片转为webp插件
 * 此插件只支持4.3版本以上的系统（因为webp格式在4.3以上才有很好的支持）
 */
class ImageOptimizePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new GradleException('当前插件只能够在android主项目中使用，依赖库不可以')
        }
        project.extensions.add('optimizer', OptimizerConfig)
        project.afterEvaluate {
            project.android.applicationVariants.all {
                    //遍历所有变体
                BaseVariant variant ->
                    //每一个变体都创建一个任务，进行图片转化
                    def task = project.tasks.create("optimize${variant.name.capitalize()}", OptimizeImageTask) {
                        //拿到manifest文件夹 用于找launcher图标
                        manifestDirectory = variant.outputs.first().processManifest.manifestOutputDirectory
                        //找到最低版本 本插件只支持18以上的版本（Android4.3版本及以上）
                        minSdk = variant.mergeResources.minSdk
                        //找到资源目录 用于找到所有的图片 进行webp转换
                        res = variant.mergeResources.outputDir
                    }
                    //在打包资源文件前 执行我们的图片转换任务
                    variant.outputs.first().processResources.dependsOn(task)
                    //我们的图片转换任务以来于manifest所以先要进行manifest任务
                    task.dependsOn(variant.outputs.first().processManifest)
            }
        }
    }
}