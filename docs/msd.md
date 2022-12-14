# Metabolomic Semantic Datalake

## Accès aux interfaces

https://metabolomics-datalake.ara.inrae.fr/

## Execution de la commande interactive spark-shell
[index](./index.md)

### Activer l'environnement Java compatible avec Sansa

```
export JAVA_HOME=/usr/local/openjdk/jdk-12.0.2+10/
```


Il faut adapter les options "executor-memory" et "num-executors"

```shell
spark-shell \
--name TP \
--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"  \
--conf "spark.sql.crossJoin.enabled=true"   \
--conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator,net.sansa_stack.query.spark.ontop.OntopKryoRegistrator,net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify"  \
--conf "spark.kryoserializer.buffer.max=2000" \
--conf spark.sql.shuffle.partitions="300" \
--conf spark.yarn.appMasterEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/" \
--conf spark.executorEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/"  \
--executor-memory 12G \
--num-executors 4  \
--jars /usr/share/java/sansa-stack-spark_2.12-0.8.4_ExDistAD.jar
```

## Soumission d'un job spark avec spark-submit

Il faut adapter les options "executor-memory" et "num-executors" et fournir le jar assemblé (ici tp.jar)

```shell
spark-submit  \
  --deploy-mode cluster \
  --driver-memory 8G  \
  --executor-memory 28G \
  --num-executors 4 \
  --conf spark.sql.shuffle.partitions="300" \
  --conf spark.yarn.appMasterEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/" \
  --conf spark.executorEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/" \
  --conf spark.yarn.submit.waitAppCompletion="false" \
  --jars /usr/share/java/sansa-stack-spark_2.12-0.8.4_ExDistAD.jar <path/tp.ja>
```   
