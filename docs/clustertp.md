# Cluster de l'atelier
## Accès aux interfaces

https://metabolomics-datalake.ara.inrae.fr/

## Execution de la commande interactive spark-shell
[index](./index.md)

Il faut adapter les options "executor-memory" et "num-executors"

```shell
spark-shell \
 --name TP \
 --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"  \
 --conf "spark.sql.crossJoin.enabled=true"   \
 --conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator"  \
 --conf "spark.kryoserializer.buffer.max=2000" \
 --conf spark.sql.shuffle.partitions="300" \
 --executor-memory 4G \
 --num-executors 4  \
 --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar
```

## Soumission d'un job spark avec spark-submit

Il faut adapter les options "executor-memory" et "num-executors" et fournir le jar assemblé (ici tp.jar)

```shell
spark-submit \
 --name TP \
 --master yarn \
 --executor-memory 4G \
 --num-executors 4  \
 --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar

```   
