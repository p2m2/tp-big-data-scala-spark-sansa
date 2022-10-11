# Prérequis

[index](./index.md)

## Environnement d'exécution / editeurs

## Java installation

```shell=
wget https://github.com/AdoptOpenJDK/openjdk12-binaries/releases/download/jdk-12.0.2%2B10/OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
tar xvf OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
```

### bashrc

```shell=
export JAVA_HOME=/usr/local/openjdk/jdk-12.0.2+10/
export PATH=$JAVA_HOME/bin:$PATH
```

## Spark

```shell=
wget https://dlcdn.apache.org/spark/spark-3.3.0/spark-3.3.0-bin-hadoop3.tgz
tar xvf spark-3.3.0-bin-hadoop3.tgz
sudo mv spark-3.3.0-bin-hadoop3 /usr/local/share/spark
```

## SBT

 - [SBT - The interactive build tool](https://www.scala-sbt.org/download.html)


## Sansa installation

Cette étape n'est pas obligatoire pour une installation locale. Vous pouvez télécharger le fichier jar sur le repo Git du [projet](https://github.com/SANSA-Stack/SANSA-Stack/releases/download/v0.8.5_ExPAD/sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar)


Pour des raisons de compatibilité des versions Spark/Hadoop sur les clusters, il vaut mieux générer le jar sur l'infrastructure avec les commandes suivantes (:warning: il faut préalablement initialiser l'environnement java) .

```shell=
git clone https://github.com/SANSA-Stack/SANSA-Stack.git
cd SANSA-Stack
sh ./dev/make_spark_dist.sh 
```

### EDI

- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=linux)
- [Visual Studio Code](https://code.visualstudio.com/)