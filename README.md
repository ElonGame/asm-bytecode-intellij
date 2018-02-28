This file was created by IntelliJ IDEA 10.0.1 for binding GitHub repository

### 2018.2.11
1. crf decompile支持直接查看External Libraries下的jar中的相关类.

### 2018.2.4
1. 处理action与具体反编译处理分离
2. cfr复用stringWriter
3. 修复`groovyCodeStyle`配置显示错误bug

### 2018.2.2
1. 加入cfr配置参数,参数格式为 `--decodelambdas true --decodestringswitch false`,与cfr官方一致
2. 进一步整理代码,配置与UI分离.
3. 改动太大,应该不会提pull request了


### 2018.1.31
fork后加入了cfr反编译结果,个人认为观感更加方便,题主最近在学kotlin,配合反编译更快的掌握kotlin.

插件本身感觉很完善了,后续会修改下代码结构,加入cfr的参数配置,提高性能.

![cfr decompile](http://oobu4m7ko.bkt.clouddn.com/1517390920.png?imageMogr2/thumbnail/!100p)


### link

[CFR - another java decompiler](http://www.benf.org/other/cfr/)


### 附录

**CFR参数**
>   --aexagg                         (boolean) 
>    --aggressivesizethreshold        (int >= 0)  default: 15000
>    --allowcorrecting                (boolean)  default: true
>    --analyseas                      (One of [JAR, WAR, CLASS]) 
>    --arrayiter                      (boolean)  default: true if class file from version 49.0 (Java 5) or greater
>    --caseinsensitivefs              (boolean)  default: false
>    --clobber                        (boolean) 
>    --collectioniter                 (boolean)  default: true if class file from version 49.0 (Java 5) or greater
>    --commentmonitors                (boolean)  default: false
>    --comments                       (boolean)  default: true
>    --decodeenumswitch               (boolean)  default: true if class file from version 49.0 (Java 5) or greater
>    --decodefinally                  (boolean)  default: true
>    --decodelambdas                  (boolean)  default: true if class file from version 52.0 (Java 8) or greater
>    --decodestringswitch             (boolean)  default: true if class file from version 51.0 (Java 7) or greater
>    --dumpclasspath                  (boolean)  default: false
>    --eclipse                        (boolean)  default: true
>    --elidescala                     (boolean)  default: false
>    --extraclasspath                 (string) 
>    --forcecondpropagate             (boolean) 
>    --forceexceptionprune            (boolean) 
>    --forcereturningifs              (boolean) 
>    --forcetopsort                   (boolean) 
>    --forcetopsortaggress            (boolean) 
>    --forloopaggcapture              (boolean) 
>    --hidebridgemethods              (boolean)  default: true
>    --hidelangimports                (boolean)  default: true
>    --hidelongstrings                (boolean)  default: false
>    --hideutf                        (boolean)  default: true
>    --innerclasses                   (boolean)  default: true
>    --j14classobj                    (boolean)  default: false if class file from version 49.0 (Java 5) or greater
>    --jarfilter                      (string) 
>    --labelledblocks                 (boolean)  default: true
>    --lenient                        (boolean)  default: false
>    --liftconstructorinit            (boolean)  default: true
>    --outputdir                      (string) 
>    --outputpath                     (string) 
>    --override                       (boolean)  default: true if class file from version 50.0 (Java 6) or greater
>    --pullcodecase                   (boolean)  default: false
>    --recover                        (boolean)  default: true
>    --recovertypeclash               (boolean) 
>    --recovertypehints               (boolean) 
>    --removebadgenerics              (boolean)  default: true
>    --removeboilerplate              (boolean)  default: true
>    --removedeadmethods              (boolean)  default: true
>    --removeinnerclasssynthetics     (boolean)  default: true
>    --rename                         (boolean)  default: false
>    --renamedupmembers              
>    --renameenumidents              
>    --renameillegalidents           
>    --renamesmallmembers             (int >= 0)  default: 0
>    --showinferrable                 (boolean)  default: false if class file from version 51.0 (Java 7) or greater
>    --showops                        (int >= 0)  default: 0
>    --showversion                    (boolean)  default: true
>    --silent                         (boolean)  default: false
>    --stringbuffer                   (boolean)  default: false if class file from version 49.0 (Java 5) or greater
>    --stringbuilder                  (boolean)  default: true if class file from version 49.0 (Java 5) or greater
>    --sugarasserts                   (boolean)  default: true
>    --sugarboxing                    (boolean)  default: true
>    --sugarenums                     (boolean)  default: true if class file from version 49.0 (Java 5) or greater
>    --tidymonitors                   (boolean)  default: true
>    --help                           (string) 
 
