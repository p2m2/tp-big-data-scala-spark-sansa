# Correction TP

## Environnement

```bash

hdfs dfs -mkdir rdf-files-test
wget -q -O - https://raw.githubusercontent.com/p2m2/tp-big-data-scala-spark-sansa/main/rdf-files-test/mesh_test.nt | hdfs dfs -put - rdf-files-test/mesh_test.nt

wget -q -O - https://raw.githubusercontent.com/p2m2/tp-big-data-scala-spark-sansa/main/rdf-files-test/pc_taxonomy_test.ttl | hdfs dfs -put - rdf-files-test/pc_taxonomy_test.ttl

wget -q -O - https://raw.githubusercontent.com/p2m2/tp-big-data-scala-spark-sansa/main/rdf-files-test/triples_assos_chebi_mesh_test.ttl | hdfs dfs -put - rdf-files-test/triples_assos_chebi_mesh_test.ttl

export JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/

spark-shell  --name TP  --master yarn  --conf "spark.yarn.appMasterEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/"  --conf "spark.executorEnv.JAVA_HOME=/usr/lib/jvm/jdk-12.0.2+10/"  --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"   --conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator,net.sansa_stack.query.spark.ontop.OntopKryoRegistrator,net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify"   --executor-memory 4G  --num-executors 4   --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar
```

## Extraction du nombre de triplets contenant le prédicat "http://id.nlm.nih.gov/mesh/vocab#concept" en utilisant find

```scala
val meshPath : String ="rdf-files-test/mesh_test.nt"

import net.sansa_stack.rdf.spark.io.RDFReader
import org.apache.jena.riot.Lang
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import org.apache.jena.graph.NodeFactory
import net.sansa_stack.rdf.spark.model._

val triples = spark.rdf(Lang.NT)(meshPath)
val triplesFiltered : RDD[Triple]= triples.find(None,Some(NodeFactory.createURI("http://id.nlm.nih.gov/mesh/vocab#concept")),None)
triplesFiltered.distinct.count() //6
```

## Extraction du nombre de triplets contenant le prédicat "http://id.nlm.nih.gov/mesh/vocab#concept" en utilisant statsPropertyUsage

```scala
import net.sansa_stack.rdf.spark.stats._

triples.statsPropertyUsage()
triples.statsPropertyUsage().filter(_._1.getURI() == "http://id.nlm.nih.gov/mesh/vocab#concept").collect()
```

## Quality Assessment

```shell
wget -q -O - https://nlmpubs.nlm.nih.gov/projects/mesh/rdf/mesh.nt.gz | gunzip -c | hdfs dfs -put - ./mesh.nt
wget -q -O - https://nlmpubs.nlm.nih.gov/projects/mesh/rdf/vocabulary_1.0.0.ttl | hdfs dfs -put - ./vocabulary_1.0.0.ttl
```

```scala
import net.sansa_stack.rdf.spark.io.RDFReader
import org.apache.jena.riot.Lang
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import net.sansa_stack.rdf.spark.qualityassessment._
import scala.util.Try

val triples : RDD[Triple] = spark.rdf(Lang.NT)("./mesh.nt").union(spark.rdf(Lang.TURTLE)("./vocabulary_1.0.0.ttl"))

val q = QualityAssessmentOperations(triples)
q.assessNoHashUris()
```

## Traitement Chargement dans un RDD[Triple] et requete SPARQL dans le spark-shell

```scala
val taxonomyPath : String ="rdf-files-test/pc_taxonomy_test.ttl"
val meshPath : String ="rdf-files-test/mesh_test.nt"
val assoforumChebiMesh : String ="rdf-files-test/triples_assos_chebi_mesh_test.ttl"

import net.sansa_stack.rdf.spark.io.RDFReader
import org.apache.jena.riot.Lang
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD

val triplesRdd : RDD[Triple] = spark.rdf(Lang.TURTLE)(taxonomyPath).union(spark.rdf(Lang.NT)(meshPath)).union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh))

val query = """ 
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX meshv: <http://id.nlm.nih.gov/mesh/vocab#>
PREFIX chebi: <http://purl.obolibrary.org/obo/CHEBI_>
PREFIX mesh: <http://id.nlm.nih.gov/mesh/>

SELECT ?compound ?prop ?mesh ?taxon
WHERE {
	?compound skos:related ?descriptor .
    ?descriptor ?prop ?mesh .
    FILTER ( ?prop=meshv:concept || ?prop=meshv:preferredConcept )
    ?taxon skos:closeMatch ?mesh .
}
"""

import net.sansa_stack.query.spark.sparqlify._
val queryEngineFactory = new QueryEngineFactorySparqlify(spark)

import net.sansa_stack.query.spark.api.domain.ResultSetSpark
import org.apache.jena.sparql.engine.binding.Binding
import org.apache.spark.rdd.RDD

val qef1 = queryEngineFactory.create(triplesRdd)
val qe = qef1.createQueryExecution(query)
val result: ResultSetSpark = qe.execSelectSpark()
val resultBindings: RDD[Binding] = result.getBindings

resultBindings.collect().foreach(println(_))
```
## Traitement Chargement dans un Dataset[Triple] et requete SPARQL dans le spark-shell

```Scala
val taxonomyPath : String ="rdf-files-test/pc_taxonomy_test.ttl"
val meshPath : String ="rdf-files-test/mesh_test.nt"
val assoforumChebiMesh : String ="rdf-files-test/triples_assos_chebi_mesh_test.ttl"

import net.sansa_stack.rdf.spark.io.RDFReader
import net.sansa_stack.rdf.spark.model.TripleOperations // to reach `toDS()` functionnality
import org.apache.jena.riot.Lang
import org.apache.jena.graph.Triple
import org.apache.spark.sql.Dataset

val triplesDataset : Dataset[Triple] = spark.rdf(Lang.TURTLE)(taxonomyPath).toDS().union(spark.rdf(Lang.NT)(meshPath).toDS()).union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh).toDS())

val query = """ 
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX meshv: <http://id.nlm.nih.gov/mesh/vocab#>
PREFIX chebi: <http://purl.obolibrary.org/obo/CHEBI_>
PREFIX mesh: <http://id.nlm.nih.gov/mesh/>

SELECT ?compound ?prop ?mesh ?taxon
WHERE {
	?compound skos:related ?descriptor .
    ?descriptor ?prop ?mesh .
    FILTER ( ?prop=meshv:concept || ?prop=meshv:preferredConcept )
    ?taxon skos:closeMatch ?mesh .
}
"""

import net.sansa_stack.ml.spark.featureExtraction.SparqlFrame
import net.sansa_stack.query.spark.SPARQLEngine
import org.apache.spark.sql.DataFrame

val sparqlFrame = new SparqlFrame()
                   .setSparqlQuery(query)
                   .setQueryExcecutionEngine(SPARQLEngine.Sparqlify)

val resultsDF : DataFrame = sparqlFrame.transform(triplesDataset)

resultsDF.select("compound","prop","mesh","taxon").collect()
```

# Creation du fichier au format Apache parquet 

```scala
resultsDF.write.parquet("./results/compound_taxon.parquet")
```

# Inspection du fichier via une commande yarn

via hdfs dfs -ls/cat

# Inspection du fichier vi spark-shell

```scala
spark.read.load("./results/compound_taxon.parquet").count
```