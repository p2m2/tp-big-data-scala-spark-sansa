# Cluster de l'atelier
## Accès aux interfaces

### Cluster occitanie

```
ssh <login>@147.100.202.42    
```

## Execution de la commande interactive spark-shell

### Activer l'environnement Java compatible avec Sansa

```
export JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/
```

*Il faut adapter les options "executor-memory" et "num-executors"*

```sh
spark-shell \
 --name TP \
 --master yarn \
 --conf "spark.yarn.appMasterEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/" \
 --conf "spark.executorEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/" \
 --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"  \
 --conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator,net.sansa_stack.query.spark.ontop.OntopKryoRegistrator,net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify"  \
 --executor-memory 4G \
 --num-executors 4  \
 --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar
```

## Soumission d'un job spark avec spark-submit

Il faut adapter les options "executor-memory" et "num-executors" et fournir le jar assemblé (ici tp.jar)

```sh
spark-submit  \
   --name TP  \
   --deploy-mode cluster \
   --master yarn \
   --conf "spark.yarn.appMasterEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/"  \
   --conf "spark.executorEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/" \
   --executor-memory 2G \
   --num-executors 4 \
   --conf spark.yarn.submit.waitAppCompletion="false" \
   --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar <path/tp.jar>
```


## Redirection du Spark history sur la machine locale

- 80xx est le port qui vous est attribué.

```sh
ssh -f <login>@147.100.202.42 -L 80xx:localhost:18080 -N
```

[Spark History](http://localhost:18081/){:target="_blank"}