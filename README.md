####Step 1. Add the JitPack repository to your build file
    allprojects {
        repositories {
	        maven { url 'https://jitpack.io' }
        }
    }
	
####Step 2. Add the dependency
    dependencies {
        implementation 'com.github.fangliangv587:lib-common:0.3.2'
    }


