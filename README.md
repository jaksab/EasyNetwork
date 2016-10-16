# EasyNetwork

EasyNetwork - is powerful and easy-to-use http library for Android.

# Download
  
```groovy
dependencies {
    compile 'com.github.jaksab:easynet:1.0.4'
}
```

# Usage

Make request by means of `NBuilder` and start execution: 


```java
NBuilder.create()
                .setUrl("http://example.com/api/path")
                .addParam("id", "10")
                .addHeader(NConst.ACCEPT_TYPE, NConst.MIME_TYPE_JSON)
                .setMethod(NBuilder.GET) // default
                .enableDefaultListeners(true) // default
                .setReadTimeout(NTask.DEFAULT_TIMEOUT_READ) // default
                .setConnectTimeout(NTask.DEFAULT_TIMEOUT_CONNECT) // default
                .setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED) // default
                .startWithParse(new NCallbackParse<CountryModel>(CountryModel.class) {
                    @Override
                    public void onStart(NRequestModel requestModel) {
                      // Called before the start of the request
                    }

                    @Override
                    public void onSuccess(CountryModel model, NResponseModel responseModel) {
                      // Called when request is executed successfully
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                      // Server error handling
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, NErrors error) {
                      // Processing a fatal error
                    }
                });
    }
```

You can define the default listeners and set up basic `NBuilder` instance with `NConfig`. We recommended do this in Application class:

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
                nBuilder.setHost("https://example.com/api");
                nBuilder.addHeader("Accept-Language", Locale.getDefault().toString());
                return nBuilder;
            }
        });
        netConfig.setDefaultOnSuccessListener(new NConfig.OnSuccessDefaultListener() {
            @Override
            public boolean onSuccess(NResponseModel responseModel) {
                // Processing all successful request
                return true; // Return true, if you want to call the final handler
            }
        });
        netConfig.setDefaultOnFailedListener(new NConfig.OnFailedDefaultListener() {
            @Override
            public boolean onFailed(NRequestModel nRequestModel, NErrors error) {
                // 
                return true; // Return true, if you want to call the final handler
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
You don't have to override all the methods of callback lifecycle.

# Linecse

```
MIT License

Copyright (c) 2016 Andrew Konovalenko

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
