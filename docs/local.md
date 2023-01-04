# Commandes SPARK pour une installation locale

[index](./index.md)

## Prérequis

- [Java / Spark](./prerequisites.md) 
- Téléchargement du jar de [Sansa](https://github.com/SANSA-Stack/SANSA-Stack/releases/download/v0.8.5_ExPAD/sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar)

### Repertoire de log

```shell
mkdir /tmp/spark-events
```

### Execution du Spark-history

```shell
/usr/local/share/spark/sbin/start-history-server.sh
```

## Execution de la commande interactive spark-shell

```shell
/usr/local/share/spark/bin/spark-shell \
  --conf "spark.eventLog.enabled=true" \
  --conf "spark.eventLog.dir=file:///tmp/spark-events"  \
  --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
  --conf "spark.sql.crossJoin.enabled=true" \
  --conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator,net.sansa_stack.query.spark.ontop.OntopKryoRegistrator,net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify" \
  --conf "spark.kryoserializer.buffer.max=2000" \
  --executor-memory 1G  \
  --num-executors 1 \
  --jars <PATH_SANSA>/sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar
```


## Soumission d'un job spark avec spark-submit

Il faut adapter les options "executor-memory" et "num-executors" et fournir le jar assemblé (ici tp.jar)

```shell
/usr/local/share/spark/bin/spark-submit \
   --conf "spark.eventLog.enabled=true" \
   --conf "spark.eventLog.dir=file:///tmp/spark-events" \
   --executor-memory 1G \
   --num-executors 1 \
   --jars ./sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar \
    assembly/tp.jar
```   