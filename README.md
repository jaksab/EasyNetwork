[ ![Download](https://api.bintray.com/packages/jaksab/EasyNetwork/easynet/images/download.svg) ](https://bintray.com/jaksab/EasyNetwork/easynet/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EasyNetwork-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5578)

# EasyNetwork

EasyNetwork - is powerful and easy-to-use http library for Android. <br/> 
[Wiki - Development guidelines](https://github.com/jaksab/EasyNetwork/wiki)

# Download
  
```groovy
dependencies {
    compile 'pro.oncreate.easynet:easynet:1.3.0'
}
```

# Usage

Make simple request by means of `Request` and start execution: 


```java

   EasyNet.get("users", id)
                .addHeader("Accept", "application/json")
                .start(new NCallback() {
                    @Override
                    public void onSuccess(NResponseModel responseModel) {
                        
                    }
                });
                
```

Primary configuration example (with `EasyNet` instance):

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyNet.getInstance()
        .setWriteLogs(true) // Default
        .setDefaultNBuilderListener(new EasyNet.NBuilderDefaultListener() {
            @Override
            public Request defaultConfig(Request request) {
                return request
                        .setHost("https://example.com/api")
                        .addHeader("Accept-Language", Locale.getDefault().toString());
            }
        })
       .addOnErrorDefaultListener(new EasyNet.OnErrorDefaultListenerWithCode(404) {
            @Override
            public void onError(NResponseModel responseModel) {
                 // For example, intercepted error 404
            }
        });
    }
}
```

See more examples: [Wiki](https://github.com/jaksab/EasyNetwork/wiki)

# Features

- Functional "from the box": without mandatory primary configuration, anywhere in the code, functionally and simply.
- Integration with Gson library.
- There is a flexible functionality for hiding\showing\disabled the views, progress dialogs, swipeRefresh layout when the query is executed. [More](https://github.com/jaksab/EasyNetwork/wiki/5.-Progress-control)
- Visual logs.
- Controll the tasks exucution: cancel all tasks, cancel task by tag and other. [More](https://github.com/jaksab/EasyNetwork/wiki/7.-Execution-control)
- The ability to intercept the results of a request with certain parameters.
- Separation of errors into: error (server) and failed (connection). [More](https://github.com/jaksab/EasyNetwork/wiki/4.-Error-processing)
- Handling the redirects.
- Cache responses.

# License

```
MIT License

Copyright (c) 2016-2018 Andrii Konovalenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
