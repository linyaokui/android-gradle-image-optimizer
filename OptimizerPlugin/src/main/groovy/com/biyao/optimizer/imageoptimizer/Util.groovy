package com.biyao.optimizer.imageoptimizer

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.GradleException
import org.gradle.api.Project

class Util{

    def static final PNG = '.png'
    def static final PNG9 = '.9.png'
    def static final JPG = '.jpg'
    def static final JPEG = '.jpeg'

    //判断文件夹是否为资源图片的文件夹
    def static isImageFolder(File file){
        if(!file){
            return false
        }
        return file.name.startsWith("drawable") || file.name.startsWith("mipmap")
    }

    //判断文件是否为png文件
    def static isPngImage(File file){
        if(!file){
            return false
        }
        return (file.name.endsWith(PNG) || file.name.endsWith(PNG.toUpperCase())) &&
                !file.name.endsWith(PNG9) && !file.name.endsWith(PNG9.toUpperCase())
    }

    //判断文件是否问jpg文件
    def static isJpgImage(File file){
        if(!file){
            return false
        }
        return file.name.endsWith(JPG) || file.name.endsWith(JPG.toUpperCase()) ||
                file.name.endsWith(JPEG) || file.name.endsWith(JPEG.toUpperCase())
    }

    //拿到cwebp转换工具
    def static getWebpTool(Project project){
        def name = 'cwebp'
        def toolName
        if(Os.isFamily(Os.FAMILY_MAC)){
            //如果是mac系统 选择cwebp_mac工具
            toolName = "${name}_mac"
        }else if(Os.isFamily(Os.FAMILY_WINDOWS)){
            //如果是window系统 选择cwebp_win.exe工具
            toolName = "${name}_win.exe"
        }else{
            //其他就是linux了
            toolName = "${name}_linux"
        }
        def file = new File("${project.buildDir}/tools/${toolName}")
        if(!file.exists()){
            file.parentFile.mkdirs()
            new FileOutputStream(file).withStream {
                def inputStream = Util.class.getResourceAsStream("/${name}/${toolName}")
                it.write(inputStream.getBytes())
                inputStream.close()
            }
        }
        if(file.exists() && file.setExecutable(true) ){
            return file.absolutePath
        }
        throw new GradleException("${toolName}webp文件转换工具不存在")
    }

}