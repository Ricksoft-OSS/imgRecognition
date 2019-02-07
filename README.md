# Alfresco Image Recognition with AI Services

[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](LICENSE)

### What is Alfresco Image Recognition?

Alfresco Image Recognition is an add-on that recognize images by AI Services like AWS,IBM etc. and add tags for search.

### Build

Run with `mvn clean install -DskipTests=true alfresco:run` or `./run.sh` and verify that it.


### Installation

1. Download imgRecognition-platform-jar/target/imgRecognition-platform-jar-*.amp
2. Put amp file to {alfresco_install_dir}/amps
3. Stop Alfresco and install amp files. Please refer https://docs.alfresco.com/5.0/tasks/dev-extensions-tutorials-simple-module-install-amp.html for amp installation.
4. Please check following Configuration section and start Alfresco.

### Configuration

Before using this add-on, please add parameters in your alfresco-global.properties.
You will need IBM Watson Visual Recognition API Key or AWS Rekognition API Key.

Note: IBM Watson Visual Recognition is used as default. If you want to use AWS service, please change imgRecognitionAction bean
in imgRecognition-platform-jar/src/main/resources/alfresco/module/imgRecognition-platform-jar/context/service-context.xml.


|Setting contents|Property key|Default|
|--------|--------------|------------|
|AWS Access Key |aws.accessKey|None|
|AWS Secret Key |aws.secretKey|None|
|Max Number of Label (tags) for AWS Recognition |aws.maxNumberOfLabels|5|
|AWS Confident threshold |aws.confidentLevel|90.0F|
|IBM Watson Version |bluemix.version|None|
|IBM Watson Api Key |bluemix.apikey|None|
|IBM Watson Confident threshold |bluemix.confidentLevel|0.55|


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
   
  
 
