import net.sansa_stack.rdf.spark.io._
import org.apache.jena.riot.Lang
import scala.util.Try

val path : String = "/rdf/metabohub/metabolights/20211210/MetaboLights_Studies.ttl"

val triples = spark.rdf(Lang.TURTLE)(path) 

// RDF Quality Assessment 

import net.sansa_stack.rdf.spark.qualityassessment._
// compute  quality assessment
val completeness_schema = Try(triples.assessSchemaCompleteness()).getOrElse(" -- ")
val completeness_interlinking = Try(triples.assessInterlinkingCompleteness()).getOrElse(" -- ")
val completeness_property = Try(triples.assessPropertyCompleteness()).getOrElse(" -- ")

val syntacticvalidity_literalnumeric = Try(triples.assessLiteralNumericRangeChecker()).getOrElse(" -- ")
val syntacticvalidity_XSDDatatypeCompatibleLiterals = Try(triples.assessXSDDatatypeCompatibleLiterals()).getOrElse(" -- ")

val availability_DereferenceableUris = Try(triples.assessDereferenceableUris()).getOrElse(" -- ")

val relevancy_CoverageDetail = Try(triples.assessCoverageDetail()).getOrElse(" -- ")
val relevancy_CoverageScope = Try(triples.assessCoverageScope()).getOrElse(" -- ")
val relevancy_AmountOfTriples = Try(triples.assessAmountOfTriples()).getOrElse(" -- ")

val performance_NoHashURIs = Try(triples.assessNoHashUris()).getOrElse(" -- ")
val understandability_LabeledResources = Try(triples.assessLabeledResources()).getOrElse(" -- ")


import spark.implicits._
val columns = Seq("Metric","Value")
val data = Seq(("completeness_schema", completeness_schema),("completeness_interlinking", completeness_interlinking),("completeness_property", completeness_property),("syntacticvalidity_literalnumeric",syntacticvalidity_literalnumeric),("availability_DereferenceableUris",availability_DereferenceableUris),("relevancy_CoverageDetail",relevancy_CoverageDetail),("relevancy_CoverageScope",relevancy_CoverageScope),("relevancy_AmountOfTriples",relevancy_AmountOfTriples),("performance_NoHashURIs",performance_NoHashURIs),("understandability_LabeledResources",understandability_LabeledResources))

val df = data.map(x=>(x._1,x._2.toString)).toDF(columns:_*)
df.show(false)
Unit



