package fr.inrae.bigdata.tp

import net.sansa_stack.rdf.spark.io.RDFReader
import net.sansa_stack.rdf.spark.model.TripleOperations
import org.apache.jena.graph.Triple
import org.apache.jena.riot.Lang
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object Main extends App {

  /**
   * Configuration de l'environnement pour la serialisation des objets Sansa
   */
  val spark = SparkSession
    .builder()
    .appName("tp-bigdata-rdf-sansa")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.sql.crossJoin.enabled", "true")
    .config("spark.kryo.registrator","net.sansa_stack.rdf.spark.io.JenaKryoRegistrator")
    .config("spark.kryoserializer.buffer.max.mb","1800")
    .getOrCreate()

  /**
   * Accès au jeux de données test
   */
  def getTestDataset : Dataset[Triple] = {
    val taxonomyPath = "rdf-files-test/pc_taxonomy_test.ttl"
    val meshPath = "rdf-files-test/mesh_test.nt"
    val assoforumChebiMesh = "rdf-files-test/triples_assos_chebi_mesh_test.ttl"

    spark.rdf(Lang.TURTLE)(taxonomyPath).toDS()
        .union(spark.rdf(Lang.NT)(meshPath).toDS())
        .union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh).toDS())
  }

  /**
   * Accès au jeux de données sur le cluster
   */
  def getDataset : Dataset[Triple] = {
    val taxonomyPath="./rdf/pc_taxonomy.ttl"
    val meshPath="/rdf/nlm/mesh/SHA_5a785145/mesh.nt"
    val assoforumChebiMesh1="./rdf/triples_assos_CHEBI_MESH_1.ttl"
    val assoforumChebiMesh2="./rdf/triples_assos_CHEBI_MESH_2.ttl"
    val assoforumChebiMesh3="./rdf/triples_assos_CHEBI_MESH_3.ttl"

    spark.rdf(Lang.TURTLE)(taxonomyPath).toDS()
      .union(spark.rdf(Lang.NT)(meshPath).toDS())
      .union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh1).toDS())
      .union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh2).toDS())
      .union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh3).toDS())
  }

  /** Choisir le bon dataset  */
    val triplesDataset: Dataset[Triple] = getDataset

  /** Requete SPARQL qui associe des composés à un taxon */
    val query =
      """
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

  /**
   * Préparation de l'environnement d'execution Sansa
   */
  val sparqlFrame =
      new SparqlFrame()
        .setSparqlQuery(query)
        .setQueryExcecutionEngine(SPARQLEngine.Sparqlify)

  /**
   * Récupération des résultats
   */
  val resultsDF: DataFrame = sparqlFrame.transform(triplesDataset)

  /**
   * Affichage du premier element
   */

  println(resultsDF.take(1).mkString("Array(", ", ", ")"))

  /**
   * Peristence des résultats au format parquet
   */
  resultsDF.write.parquet("./results/compound_taxon.parquet")


}
