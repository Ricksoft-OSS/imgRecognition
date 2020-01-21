# Alfresco Image Recognition with AI Services

[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](LICENSE)

### What is Alfresco Image Recognition?

Alfresco Image Recognition is an add-on that recognize images by AI Services like AWS,IBM etc. and add tags for search.

### Build

Run with `mvn clean install -DskipTests=true alfresco:run` or `./run.sh build_start` and verify that it.


### Installation

1. Download `imgRecognition/target/img-recognition-x.y.z.jar`
2. Put jar file to `{alfresco_install_dir}/modules/platform`
3. Stop Alfresco and install jar files. Please refer https://docs.alfresco.com/6.2/concepts/dev-extensions-packaging-techniques-jar-files.html for jar installation.
4. Please check following Configuration section and start Alfresco.

### Configuration

Before using this add-on, please add parameters in your alfresco-global.properties.
You will need IBM Watson Visual Recognition API Key or AWS Rekognition API Key.

Note: IBM Watson Visual Recognition is used as default. If you want to use AWS service, please change `recognition.service.type` property value to `aws`.


|Setting contents|Property key|Default|
|--------|--------------|------------|
|Default Recognition Service (`ibm` or `aws`) |recognition.service.type|ibm|
|AWS Access Key |aws.accessKey|None|
|AWS Secret Key |aws.secretKey|None|
|Max Number of Label (tags) for AWS Recognition |aws.maxNumberOfLabels|5|
|AWS Confident threshold |aws.confidentLevel|90.0F|
|IBM Watson Api Key |bluemix.apikey|None|
|IBM Watson Version |bluemix.version|2020-01-01|
|IBM Watson Confident threshold |bluemix.confidentLevel|0.55|
|IBM Watson Custom Model ID |bluemix.custom.model.id|None|


### Contribution

If you would like to request bug fixes and additional functions, please create Issue or Pull Request in Github.

### Credit

- Yoshihiko Aochi (aochi.yoshihiko@ricksoft.jp)

### License

Copyright 2018 Ricksoft Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
   
  
 
