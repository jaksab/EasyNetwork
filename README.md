[ ![Download](https://api.bintray.com/packages/jaksab/EasyNetwork/easynet/images/download.svg) ](https://bintray.com/jaksab/EasyNetwork/easynet/_latestVersion)

# EasyNetwork

EasyNetwork - is powerful and easy-to-use http library for Android.

# Download
  
```groovy
dependencies {
    compile 'pro.oncreate.easynet:easynet:1.1.8'
}
```

# Usage

Make simple request by means of `NBuilder` and start execution: 


```java

   EasyNet.get("users", id)
                .addHeader("Accept", "application/json")
                .start(new NCallback() {
                    @Override
                    public void onSuccess(NResponseModel responseModel) {
                        
                    }
                });
                
```

Primary configuration example (with `NConfig` instance):

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NConfig netConfig = NConfig.getInstance();
        netConfig.setWriteLogs(true); // Default
        netConfig.setDefaultNBuilderListener(new NConfig.NBuilderDefaultListener() {
            @Override
            public NBuilder defaultConfig(NBuilder nBuilder) {
                return nBuilder
                        .setHost("https://example.com/api")
                        .addHeader("Accept-Language", Locale.getDefault().toString());
            }
        });
        netConfig.addOnErrorDefaultListener(new NConfig.OnErrorDefaultListenerWithCode(404) {
            @Override
            public void onError(NResponseModel responseModel) {
                // Intercepted error 404
            }
        });
    }
}
```

See more: [Wiki](https://github.com/jaksab/EasyNetwork/wiki)

# Features

- Functional "from the box": without mandatory primary configuration, anywhere in the code, functionally and simply.
- Integration with GSON.
- There is a flexible functionality for hiding\showing\disabled the views, progress dialogs, swipeRefresh layout when the query is executed.
- Visual logs.
- Controll the tasks exucution: cancel all tasks, cancel task by tag and other. Example: `NConfig.getInstance().cancelAllTasks();`.
- The ability to intercept the results of a query with certain parameters.
- Separation of errors into: error (server) and failed (connection).
- Handling the redirects

# License

```
MIT License

Copyright (c) 2016-2017 Andrii Konovalenko

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
