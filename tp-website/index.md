# TP

## 1) Tester en local le traitement sur un jeu de donnée test

```scala
val taxonomyPath="rdf-files-test/pc_taxonomy_test.ttl"
val meshPath="rdf-files-test/mesh_test.nt"
val assoforumChebiMesh="rdf-files-test/triples_assos_chebi_mesh_test.ttl"

import net.sansa_stack.rdf.spark.io.RDFReader
import net.sansa_stack.rdf.spark.model.TripleOperations
import org.apache.jena.graph.Triple
import org.apache.jena.riot.Lang
import org.apache.spark.sql.{Dataset, DataFrame, Encoder, Encoders}

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
} limit 10

"""

import net.sansa_stack.ml.spark.featureExtraction.SparqlFrame
import net.sansa_stack.query.spark.SPARQLEngine

val sparqlFrame =
new SparqlFrame()
  .setSparqlQuery(query)
  .setQueryExcecutionEngine(SPARQLEngine.Sparqlify)

val resultsDF : DataFrame = sparqlFrame.transform(triplesDataset)

//Affichage
resultsDF.map( row => (row.get(0).toString,row.get(2).toString,row.get(0).toString,row.get(3).toString) ).take(1)

resultsDF.write.parquet("./results/compound_taxon.parquet")
```

## 2) Intégrer ce code dans une application spark

```
git clone git@github.com:p2m2/tp-big-data-scala-spark-sansa.git
```

## 3) Générer l'assemblage du jar pour un test en local

## 4) Générer l'assemblage du jar pour un test sur le cluster

