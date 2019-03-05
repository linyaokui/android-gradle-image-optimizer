# android-gradle-image-optimizer
Android gradle图片资源优化插件，可以在构建过程中动态将png 和 jpg文件转为webp格式。==此插件仅适合Android4.3及以上系统==
# 使用方法
### 1.在项目的build.gradle文件中添加
    classpath 'com.biyao.optimizer:optimizeImage:1.0'
例如：
```
buildscript {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'com.biyao.optimizer:optimizeImage:1.0'
    }
}
```
### 2.app项目中的build.gradle中下面代码

```
apply plugin:'com.biyao.optimizer.imageoptimizer'
```

### 3.因为这个插件是通过改变build目录下的res文件中的图片达到的目的，所以不支持aapt2编译。使用者在使用的时候需要关闭aapt2</br>
#### 关闭方法1
```
在项目的gradle.properties文件中增加
android.enableAapt2=false
    
```
#### 关闭方法2
```
通过命令行打包 增加关闭参数 如下：
gradle assembleRelease -Pandroid.enableAapt2=false
    
```

### 4.本插件是通过把jpg和png图片转为webp格式的，有一个转换的压缩系数,默认为75，在app的build.gradle根节点，通过如下方法配置

```
optimizer{
    quality 75
}
```

### License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)









