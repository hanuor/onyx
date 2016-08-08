[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Onyx-red.svg?style=plastic)](http://android-arsenal.com/details/1/4089)

# onyx
----      
Onyx is a library for android that can be used by developers to understand what type of content they are enabling inside their apps.
An example can be to limit adult content in apps specifically made for children. Through Onyx you can get the characteristics of an image and then determine if you want to block it or allow it.
The possibilities are endless, there can be a zillion use case scenarios. Onyx is proud to be powered by world's best visual recognition technology - Clarifai.      
Note - The gathering of information about the images is done through the implementation of technologies like Artificial intelligence, machine learning, and deep learning.


![](https://s8.postimg.org/pla6wqs5h/onyx.png)
------    
#Download    
###Using Gradle:
    repositories {
    mavenCentral() // jcenter() works as well because it pulls from Maven Central
    }
    dependencies {
    compile 'com.hanuor.onyx:onyx:0.1.2'    
    }
**or** simply under dependencies section:   
  
    compile 'com.hanuor.onyx:onyx:0.1.2'  

### or Using Maven:
    <dependency>
    <groupId>com.hanuor.onyx</groupId>
    <artifactId>onyx</artifactId>
    <version>0.1.2</version>
    <type>pom</type>
    </dependency>

------
#Documentation

###Getting tags for an Image
Pretty simple -  


     Onyx.with(Context context).fromURL(String url).getTagsfromApi(new OnTaskCompletion() {
                    @Override
                    public void onComplete(ArrayList<String> response) {
                        //get an arraylist of tags here
                        //do whatever you want here
                            
                      }
                });
            }
        });

------
###Compatibility

**Minimum Android SDK**: Onyx requires a minimum API level of **7**.    

---------
###Special thanks to:       

Clarifai         
----------      
 
 **Please do notify us if you're using our library in your app. We'd be more than happy to list your app here!**    
-----------     
###How it looks like?     
[![Screenshot](anim2.gif)](https://cl.ly/1z1j0847331d)
    




---------

###License
Copyright 2016 Hanuor, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
