# tp-big-data-scala-spark-sansa

https://atelier-bigdata.sciencesconf.org/

## prepare test

### Chebi

```sh 
mkdir -p rdf/ebi/chebi/current_release/
pushd rdf/ebi/chebi/current_release/
wget https://ftp.ebi.ac.uk/pub/databases/chebi/ontology/chebi.owl
popd
```
### Mesh

```sh 
mkdir -p rdf/nlm/mesh/current_release/
pushd rdf/nlm/mesh/current_release/
wget https://nlmpubs.nlm.nih.gov/projects/mesh/rdf/mesh.nt
popd
```
## Pubchem 


```sh 
mkdir -p rdf/pubchem/compound-general/current_release/
pushd rdf/pubchem/compound-general/current_release/
wget https://ftp.ncbi.nlm.nih.gov/pubchem/RDF/compound/general/pc_compound_type.ttl.gz
gunzip pc_compound_type.ttl.gz
popd
```


``` 
/usr/local/share/spark/bin/spark-submit \
   --conf "spark.eventLog.enabled=true" \
   --conf "spark.eventLog.dir=file:///tmp/spark-events" \
   --executor-memory 1G \
   --num-executors 1 \
   --jars ./sansa-ml-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar \
    assembly/msd-metdisease-request-compound-mesh.jar -d ./rdf
```

``` 
spark-submit  \
 --deploy-mode cluster \
 --driver-memory 8G \
 --executor-memory 28G \
 --num-executors 4 \
 --conf spark.sql.shuffle.partitions="300" \
 --conf spark.yarn.appMasterEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/" \
 --conf spark.executorEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/"  \
 --conf spark.yarn.submit.waitAppCompletion="false" \
 --jars /usr/share/java/sansa-stack-spark_2.12-0.8.4_ExDistAD.jar \
 msd-metdisease-request-compound-mesh.jar
```


``` 
sbt -J-Xmx2G -J-Xms2G
```

## TODO

- PubChem compounds - MeSH associations
- ChEBI - MeSH associations
- Chemont - MeSH associations
- MeSH - MeSH associations
