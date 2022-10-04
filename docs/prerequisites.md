# Prerequisites

[index](./index.md)

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

Cette étape n'est pas obligatoire pour une installation locale. 
Vous pouvez télécharger le fichier jar sur le repo Git du [projet](https://github.com/SANSA-Stack/SANSA-Stack/releases/download/v0.8.5_ExPAD/sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar)

Pour des raisons de compatibilités de versions Spark/Hadoop sur les clusters, il vaut mieux creer le jar sur l'infrastructure avec les commandes suivantes.
attention, il faut préalablement initialiser l'environnement java.

```shell=
git clone https://github.com/SANSA-Stack/SANSA-Stack.git
cd SANSA-Stack
sh ./dev/make_spark_dist.sh 
```