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
  def getDatasetTest : Dataset[Triple] = {
    val taxonomyPath = "rdf-files-test/pc_taxonomy_test.ttl"
    val meshPath = "rdf-files-test/mesh_test.nt"
    val assoforumChebiMesh = "rdf-files-test/triples_assos_chebi_mesh_test.ttl"

    spark.rdf(Lang.TURTLE)(taxonomyPath).toDS()
        .union(spark.rdf(Lang.NT)(meshPath).toDS())
        .union(spark.rdf(Lang.TURTLE)(assoforumChebiMesh).toDS())
  }

  /**
   * Accès au jeux de données sur le cluster MSD
   */
  def getDatasetMsd : Dataset[Triple] = {
    val taxonomyPath="/user/ofilangi/rdf/pc_taxonomy.ttl"
    val meshPath="/rdf/nlm/mesh/SHA_5a785145/mesh.nt"
    val assoforum="/user/ofilangi/rdf/forum.nt"

    spark.rdf(Lang.TURTLE)(taxonomyPath).toDS()
      .union(spark.rdf(Lang.NT)(meshPath).toDS())
      .union(spark.rdf(Lang.NT)(assoforum).toDS())
  }

  /**
    * Accès au cluster de l'atelier
    */

  def getDatasetClusterAtelier : Dataset[Triple] = {
    val taxonomyPath="/rdf/pc_taxonomy.ttl"
    val meshPath="/rdf/mesh.nt"
    val assoforum="/rdf/forum.nt"

    spark.rdf(Lang.TURTLE)(taxonomyPath).toDS()
      .union(spark.rdf(Lang.NT)(meshPath).toDS())
      .union(spark.rdf(Lang.NT)(assoforum).toDS())
  }

  // ...
}
