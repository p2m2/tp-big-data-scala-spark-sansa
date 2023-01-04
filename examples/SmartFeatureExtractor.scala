/** 

spark-shell  \
 --name SmartFeatureExtractor \
 --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"  \
--conf "spark.sql.crossJoin.enabled=true"   \
--conf "spark.kryo.registrator=net.sansa_stack.rdf.spark.io.JenaKryoRegistrator,net.sansa_stack.query.spark.ontop.OntopKryoRegistrator,net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify"  \
--conf "spark.kryoserializer.buffer.max=1024"   \
 --driver-memory 2G \
 --executor-memory 2G \
 --num-executors 8 \
 --conf spark.yarn.appMasterEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/" \
 --conf spark.executorEnv.JAVA_HOME="/usr/local/openjdk/jdk-12.0.2+10/"  \
 --conf spark.yarn.submit.waitAppCompletion="false" \
 --jars /usr/share/java/sansa-stack-spark_2.12-0.8.0-RC3-SNAPSHOT-jar-with-dependencies.jar

 */


import net.sansa_stack.rdf.spark.io._
import org.apache.jena.riot.Lang

import net.sansa_stack.ml.spark.featureExtraction._

//val path : String = "/rdf/ebi/chebi/13-Jun-2022/chebi.owl"
//val path : String = "/rdf/pubchem/compound-general/2022-06-08/pc_compound_type.ttl"
//val path : String = "/rdf/pubchem/compound-general/2022-06-08/pc_compound2component.ttl"
val path : String = "/rdf/pubchem/compound-general/2022-06-08/pc_compound2descriptor_000156.ttl"

val triples = spark.rdf(Lang.RDFXML)(path) 

val dataset = {
    import net.sansa_stack.rdf.spark.model.TripleOperations
    triples.toDS()
}

val sfeNoFilter = new SmartFeatureExtractor()


/** Feature Extracted DataFrame */
//val feDf = sfeNoFilter.transform(dataset)
//feDf.show(5,false)
//.setObjectFilter("http://purl.obolibrary.org/obo/CHEBI_24279")
val sfeObjectFilter = new SmartFeatureExtractor().setSparqlFilter("SELECT ?s WHERE { ?s ?p <http://purl.obolibrary.org/obo/CHEBI_24279> }")

val feDf1 = sfeObjectFilter.transform(dataset)
feDf1.show(5,false)

/* =============================================================================================================== */
/*****Sparql Transformer **/

val sparqlQueryString =""" 
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
SELECT ?s WHERE {
      <http://rdf.ncbi.nlm.nih.gov/pubchem/descriptor/CID126500775_Canonical_SMILES> ?p ?v .
    }
"""


import net.sansa_stack.ml.spark.utils._
import org.apache.spark.sql.{DataFrame,Encoders,Encoder}
import org.apache.jena.graph.Node

val sparqlQuery: SPARQLQuery = SPARQLQuery(sparqlQueryString)

val res = sparqlQuery.transform(dataset)

implicit val triplesEncoder : Encoder[Node] = Encoders.kryo(classOf[Node])

val resultNodes: Array[Node] = res.as[Node].collect()

/* =============================================================================================================== */
