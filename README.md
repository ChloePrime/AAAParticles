Effekseer loader on Minecraft 1.18+ <br>
**Windows** and **Linux** are supported <br>
**MacOS** is supported since version **1.4.3**<br>
Current Effekseer version is **1.70e**<br>
<br>
#### Quick Start:
```groovy
repositories {
    maven {
        name = 'Chloe Maven'
        url 'https://maven.chloeprime.cn:4080/releases/'
        content {
            includeGroup("mod.chloeprime")
            includeGroup("cn.chloeprime")
        }
    }
}
dependencies {
    // Choose one of these depending on your platform/toolchain
    implementation fg.deobf('mod.chloeprime:aaa-particles-forge:1.20.1-1.4.11')
    modImplementation 'mod.chloeprime:aaa-particles-fabric:1.20.1-1.4.11'
}
```
Then follow the [tutorial](https://github.com/ChloePrime/AAAParticles/wiki/How-To-Add-Particles)
<br><br>
#### Discussion & Contact:<br>
[![Join Discord](https://dcbadge.limes.pink/api/server/https://discord.gg/pkpNN77yfs)](https://discord.gg/pkpNN77yfs)