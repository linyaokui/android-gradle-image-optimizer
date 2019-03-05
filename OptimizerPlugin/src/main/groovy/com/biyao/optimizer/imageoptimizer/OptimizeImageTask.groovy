package com.biyao.optimizer.imageoptimizer

import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * 图片转换任务
 */
class OptimizeImageTask extends DefaultTask{

    @Optional
    @Input
    File manifestDirectory // manifest文件夹
    File manifestFile //manifest文件

    @Input
    def minSdk // 最小sdk

    @Input
    File res //资源文件夹

    def webpTool // webp转换工具
    String icon //app图标
    String iconRound // app圆形图标
    def quality // webp压缩质量 建议为75

    OptimizeImageTask(){
        group = 'Optimizer'
        webpTool = Util.getWebpTool(project)
        quality = project.extensions.getByName('optimizer').quality
    }

    @TaskAction
    def run(){
        println("======开始进行webp图片转换======")
        println("转换为webp的质量为 ${quality}")
        println("webpTool: ${webpTool}")
        println("minSdk: ${minSdk}")
        println("res目录: ${res.absolutePath}")
        println("manifestFile目录: ${manifestDirectory.absolutePath}")
        if(minSdk < 18){
            throw new GradleException("本插件只支持的sdk最低版本为18")
        }
        //找到manifest文件
        manifestDirectory.eachFile {
            if(it.name == 'AndroidManifest.xml'){
                manifestFile = it
            }
        }
        //解析manifest文件 找到launch图标
        def ns = new Namespace("http://schemas.android.com/apk/res/android","android")
        def node = new XmlParser().parse(manifestFile)
        Node nodeApplication = node.application[0]
        icon = nodeApplication.attributes()[ns.icon]
        iconRound = nodeApplication.attributes()[ns.roundIcon]
        icon = icon.substring(icon.lastIndexOf('/')+1,icon.length())
        if(iconRound){
            iconRound = iconRound.substring(iconRound.lastIndexOf('/')+1,iconRound.length())
        }
        println("app的登录图标为名为：${icon} round图标为${iconRound}")
        def images = []
        //遍历资源文件中的所有文件夹
        res.eachDir {
            //如果是包含图片的文件夹
            if(Util.isImageFolder(it)){
                it.eachFile {
                    if(Util.isJpgImage(it) && !isSystemFile(it)){
                        images << it
                    }
                    if(Util.isPngImage(it) && isNoLaunch(it) && !isSystemFile(it)){
                        images << it
                    }
                }
            }
        }
        images.each {
            convertToWebp(it)
        }
    }

    def convertToWebp(File file){
        if(!file){
            return
        }
        String name = file.name
        name = name.substring(0,name.lastIndexOf('.'))
        def result = "${webpTool} -q ${quality} ${file.absolutePath} -o ${file.parent}/${name}.webp".execute()
        result.waitForProcessOutput()
        if(result.exitValue() == 0){
            file.delete()
            println("转换完成 webp ${file.absolutePath} 成功")
        }else{
            println("转换完成 webp ${file.absolutePath} 失败")
        }
    }

    //判断是否为登录图标
    def isNoLaunch(File file){
        if(!file){
            return false
        }
        String name = file.name
        if(iconRound){
            return name != "${icon}.png" && name != "${iconRound}.png"
        }else{
            return name != "${icon}.png"
        }
    }

    //判断是否为系统的文件
    def isSystemFile(File file){
        if(!file){
            return
        }
        String name = file.name
        return name.startsWith("abc_")
    }
}