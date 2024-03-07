
val indexOfItemRemoved = 2 //0th Starting index

val itemFailures = Seq(
  "Banana/foo/BodyEadEsad[1]/Quanitty",
  "Banana/foo/BodyEadEsad[2]/Quanitty",
  "Banana/foo/wegewewegw[6]/Quanitty",
  "Banana/foo/BodyEadEsad[3]/Quanitty",
  "Banana/foo/BodyEadEsad[4]/Quanitty",
  "Banana/foo/gGiogigo[1]/Quanitty",
  "Banana/foo/wfegegwqo[1]/Quanitty"
)

val itemErrors = itemFailures.filter(_.contains("BodyEadEsad"))

val itemIndexes: Seq[Int] = itemErrors.collect { case itemError =>
  val lookup = "BodyEadEsad\\[(\\d+)\\]".r.unanchored
  val lookup(index) = itemError
  index.toInt
}

val indexesToAmend = itemIndexes.filter(_ > (indexOfItemRemoved + 1))

println("Item Errors:")
println(itemIndexes)

println("Item Errors That needs re-indexing:")
println(indexesToAmend)