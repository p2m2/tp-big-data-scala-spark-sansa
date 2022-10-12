# Installation

## Environnement d'exécution / editeurs

## Java installation

```shell
wget https://github.com/AdoptOpenJDK/openjdk12-binaries/releases/download/jdk-12.0.2%2B10/OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
tar xvf OpenJDK12U-jdk_x64_linux_hotspot_12.0.2_10.tar.gz
sudo mkdir /usr/local/openjdk
sudo mv jdk-12.0.2+10 /usr/local/openjdk/
```

### bashrc

mettre dans le fichier ~/.bash_profile

```shell
export JAVA_HOME=/usr/local/openjdk/jdk-12.0.2+10/
export PATH=$JAVA_HOME/bin:$PATH
```

## Spark

```shell
wget https://downloads.apache.org/spark/spark-3.3.0/spark-3.3.0-bin-hadoop3.tgz
tar xvf spark-3.3.0-bin-hadoop3.tgz
sudo mv spark-3.3.0-bin-hadoop3 /usr/local/share/spark
```

## SBT

 - [SBT - The interactive build tool](https://www.scala-sbt.org/download.html)

```shell
wget https://github.com/sbt/sbt/releases/download/v1.7.2/sbt-1.7.2.tgz
tar xvf sbt-1.7.2.tgz
sudo mv sbt /usr/local/share/
echo "export PATH=/usr/local/share/sbt/bin:$PATH" >> ~/.bash_profile
```

## Sansa installation

Cette étape n'est pas obligatoire pour une installation locale. Vous pouvez télécharger le fichier jar sur le repo Git du [projet](https://github.com/SANSA-Stack/SANSA-Stack/)

```shell=
wget https://github.com/SANSA-Stack/SANSA-Stack/releases/download/v0.8.5_ExPAD/sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar
```


Pour des raisons de compatibilité des versions Spark/Hadoop sur les clusters, il vaut mieux générer le jar sur l'infrastructure avec les commandes suivantes (:warning: il faut préalablement initialiser l'environnement java) .

```shell=
git clone https://github.com/SANSA-Stack/SANSA-Stack.git
cd SANSA-Stack
sh ./dev/make_spark_dist.sh 
```

### EDI

- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=linux)
- [Visual Studio Code](https://code.visualstudio.com/)