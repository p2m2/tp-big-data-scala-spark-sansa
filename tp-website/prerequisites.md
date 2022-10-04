# Prerequisites

## Java installation

```shell=
wget https://github.com/AdoptOpenJDK/openjdk12-binaries/releases/download/jdk-12.0.2%2B10/OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
tar xvf OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
```

## bashrc
```shell=
export JAVA_HOME=/usr/local/openjdk/jdk-12.0.2+10/
export PATH=$JAVA_HOME/bin:$PATH
```

## Sansa installation

```shell=
git clone https://github.com/SANSA-Stack/SANSA-Stack.git
cd SANSA-Stack
sh ./dev/make_spark_dist.sh 
```