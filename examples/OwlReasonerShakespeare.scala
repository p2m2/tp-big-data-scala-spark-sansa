import org.apache.jena.riot.lang.CollectorStreamTriples
import org.apache.jena.riot.{Lang, RDFParser}
import java.io.{File, FileReader, StringReader}
import scala.collection.JavaConverters._
import org.apache.jena.riot.Lang
import org.apache.jena.graph.{Triple}
import org.apache.spark.rdd.RDD
import net.sansa_stack.inference.spark.forwardchaining.triples.ForwardRuleReasonerOWLHorst


val prefixDeclStr =
    """@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
      |@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
      |@prefix owl: <http://www.w3.org/2002/07/owl#> .
      |@prefix ab: <http://atelierbigdata-inrae-sete/2023/01#> .
      |
    """.stripMargin

val turtle = 
"""ab:Femme rdfs:subClassOf ab:Humain .
  |ab:Anne rdf:type ab:Femme .
  |ab:Anne ab:married ab:Shakespeare .
  |ab:married rdf:type owl:SymmetricProperty .
   """.stripMargin



def toTriples(turtleString: String): Seq[Triple] = {
    val parser = RDFParser.create().source(new StringReader(prefixDeclStr + turtleString)).lang(Lang.TTL).build()
    val sink = new CollectorStreamTriples
    parser.parse(sink)

    sink.getCollected.asScala.toSeq
  }

val triples: RDD[Triple] = sc.parallelize(toTriples(turtle))
val parallelism : Int = 1

val reasoner=new ForwardRuleReasonerOWLHorst(spark.sparkContext,parallelism)
val inferredTriples = reasoner.apply(triples)

triples.count()
inferredTriples.count()
triples.collect().map(println)
inferredTriples.collect().map(println)

