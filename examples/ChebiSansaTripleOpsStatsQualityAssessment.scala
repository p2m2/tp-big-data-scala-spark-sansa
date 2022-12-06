
import net.sansa_stack.rdf.spark.io._
import org.apache.jena.riot.Lang

val path : String = "/rdf/ebi/chebi/13-Jun-2022/chebi.owl"

val triplesChebi = spark.rdf(Lang.RDFXML)(path) 

// count -> res0: Long = 5981144

val parallelism : Int = 4

// RDF Triple Ops
// ------------
import org.apache.jena.graph.NodeFactory
import net.sansa_stack.rdf.spark.model._ 

val triplesWithPropertyInchikey = triplesChebi.find(None,Some(NodeFactory.createURI("http://purl.obolibrary.org/obo/chebi/inchikey")), None)
// count -> res11: Long = 138722

println("Number of triples: " + triplesWithPropertyInchikey.distinct.count())
// Number of triples: 138722
println("Number of subjects: " + triplesWithPropertyInchikey.getSubjects.distinct.count())
// Number of subjects: 138722
println("Number of predicates: " + triplesWithPropertyInchikey.getPredicates.distinct.count())
// Number of predicates: 1
println("Number of objects: " + triplesWithPropertyInchikey.getObjects.distinct.count())
// Number of objects: 137814

val subjects = triplesChebi.filterSubjects(_.isURI()).collect.mkString("\n")
val predicates = triplesChebi.filterPredicates(_.isVariable()).collect.mkString("\n")
val objects = triplesChebi.filterObjects(_.isLiteral()).collect.mkString("\n")


//RDF Statistic
// ------------

import net.sansa_stack.rdf.spark.stats._ 

val rdf_stats_prop_dist = triplesChebi.statsPropertyUsage()

rdf_stats_prop_dist.take(5)
//res13: Array[(org.apache.jena.graph.Node, Int)] = Array((http://www.w3.org/2000/01/rdf-schema#subClassOf,322738), (http://www.geneontology.org/formats/oboInOwl#hasAlternativeId,18515), (http://www.geneontology.org/formats/oboInOwl#date,1), (http://www.geneontology.org/formats/oboInOwl#hasOBONamespace,161792), (http://www.w3.org/2000/01/rdf-schema#subPropertyOf,6))

// RDF Quality Assessment 

import net.sansa_stack.rdf.spark.qualityassessment._
// compute  quality assessment
val completeness_schema = Try(triplesChebi.assessSchemaCompleteness()).getOrElse(" -- ")
val completeness_interlinking = Try(triplesChebi.assessInterlinkingCompleteness()).getOrElse(" -- ")
val completeness_property = Try(triplesChebi.assessPropertyCompleteness()).getOrElse(" -- ")

val syntacticvalidity_literalnumeric = Try(triplesChebi.assessLiteralNumericRangeChecker()).getOrElse(" -- ")
val syntacticvalidity_XSDDatatypeCompatibleLiterals = Try(triplesChebi.assessXSDDatatypeCompatibleLiterals()).getOrElse(" -- ")

val availability_DereferenceableUris = Try(triplesChebi.assessDereferenceableUris()).getOrElse(" -- ")

val relevancy_CoverageDetail = Try(triplesChebi.assessCoverageDetail()).getOrElse(" -- ")
val relevancy_CoverageScope = Try(triplesChebi.assessCoverageScope()).getOrElse(" -- ")
val relevancy_AmountOfTriples = Try(triplesChebi.assessAmountOfTriples()).getOrElse(" -- ")

val performance_NoHashURIs = Try(triplesChebi.assessNoHashUris()).getOrElse(" -- ")
val understandability_LabeledResources = Try(triplesChebi.assessLabeledResources()).getOrElse(" -- ")


import spark.implicits._
val columns = Seq("Metric","Value")
val data = Seq(("completeness_schema", completeness_schema),("completeness_interlinking", completeness_interlinking),("completeness_property", completeness_property),("syntacticvalidity_literalnumeric",syntacticvalidity_literalnumeric),("availability_DereferenceableUris",availability_DereferenceableUris),("relevancy_CoverageDetail",relevancy_CoverageDetail),("relevancy_CoverageScope",relevancy_CoverageScope),("relevancy_AmountOfTriples",relevancy_AmountOfTriples),("performance_NoHashURIs",performance_NoHashURIs),("understandability_LabeledResources",understandability_LabeledResources))

val df = data.map(x=>(x._1,x._2.toString)).toDF(columns:_*)
df.show(false)
Unit


// Inference
val reasoner=new ForwardRuleReasonerOWLHorst(spark.sparkContext,parallelism)
val inferredTriples = reasoner.apply(triples)

