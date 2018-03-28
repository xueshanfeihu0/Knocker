# Transmitter
Sample lock screen library.

### Import

```groovy
//Add it in your root build.gradle at the end of repositories:
maven { url 'https://jitpack.io' }
```
```groovy
//Add the dependency
compile 'com.github.Aquarids:Knocker:0.0.1'
```
[![import](https://jitpack.io/v/Aquarids/Knocker.svg)](https://jitpack.io/#Aquarids/Knocker)

### Usage

1. make your lock screen fragment extends KnockerFragment
2. KnockerManager.startKnockerService("your fragment package name")

License
--------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
