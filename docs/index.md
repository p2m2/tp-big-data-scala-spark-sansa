# Atelier Big Data - 10 au 12 janvier 2023 - Hadoop/Spark/RDF

## Objectifs

L'objectif de ce TP est d'intégrer puis d'utiliser des graphes de connaissances (fichier RDF) de plusieurs sources (Mesh, NCBI, FORUM) dans un DataLake et un environnement Spark/Hadoop. 

Nous utiliserons des [commandes Hadoop](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/FileSystemShell.html) pour la gestion des fichiers et nous developperons le traitement en language [scala](https://www.scala-lang.org/). 

Pour mettre au point la méthode nous utiliserons un [spark-shell](https://spark.apache.org/docs/latest/quick-start.html) puis nous developperons une application spark pour automatiser le traitement.

## Sujet

Associer un composé du projet [FORUM](https://forum-webapp.semantic-metabolomics.fr/#/about) avec un [PubChem/Taxonomy](https://pubchem.ncbi.nlm.nih.gov/source/22056) .

![fig1](./img/fig1.png)

[Les données RDF](./databases.md)

## Plateforme d'exécution

- [Local](./local.md)
- [Cluster de l'atelier](./clustertp.md)
- [Metabolomics Semantic Datalake](./msd.md)

L'idéal est de réaliser le développement et les tests sur votre machine personnelle puis de lancer le traitement sur le cluster.

Pour l'environnement de travail, il faut se référer aux [prérequis](./prerequisites.md).


## Traitement sur un jeu de données test

Nous allons travailler a partir d'un jeu de données test qui se trouve dans [rdf-files-test](https://github.com/p2m2/tp-big-data-scala-spark-sansa/tree/main/rdf-files-test)

### Exécutez un  sbt-shell pour travailler sur ce jeu de données

### Exécution du traitement

Il s'agit ici de construire avec [Sansa](http://sansa-stack.github.io/SANSA-Stack/)

 - un Dataset par fichier RDF
 - créer un Dataset qui merge les trois Datasets
 - Executer une requete SPARQL
 - créer un Dataset resultat au format parquet
  


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
}
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

## Intégrer ce code dans une application spark

### Connectez vous au cluster Big Data

### Reperez les fichiers RDF sur le cluster (commande hdfs)

Les fichiers sont localisés sur le stockage hdfs dans le répertoire */rdf*.

:information_source: [documentation hadoop](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/FileSystemShell.html)

### Verifiez la structure des fichiers avec la command head

### Base du TP

Récuperez [l'archive template du tp](https://github.com/p2m2/tp-big-data-scala-spark-sansa/archive/refs/heads/main.zip) 

### Intégrez dans la classe Main le traitement

### Assemblage du Jar

```shell
sbt assembly
```

#### Générez l'assemblage du jar pour un test en local 
#### Générez l'assemblage du jar pour un test sur le cluster
