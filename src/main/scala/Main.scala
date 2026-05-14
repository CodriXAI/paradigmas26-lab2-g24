// =====================================================================
// Ejercicio 6: Integración del sistema completo
// =====================================================================

object Main {
  def main(args: Array[String]): Unit = {

    // ------------------------------------------------------------------
    // Paso 1: Cargar diccionarios
    // ------------------------------------------------------------------
    val dictionary: List[NamedEntity] = Dictionary.loadAll()

    println(s"Diccionario cargado: ${dictionary.size} entidades.\n")

    // ------------------------------------------------------------------
    // Paso 2: Descargar posts
    // ------------------------------------------------------------------
    val subscriptions = FileIO.readSubscriptions()

    val allPosts: List[(String, List[String])] = subscriptions.map { url =>
      println(s"Descargando posts de: $url")
      val json   = FileIO.downloadFeed(url)
      val titles = FileIO.extractPostTitles(json)
      (url, titles)
    }

    // ------------------------------------------------------------------
    // Paso 3: Detectar entidades y mostrar resultados por post
    // ------------------------------------------------------------------
    // Recorrer todos los posts y mostrar los resultados
    val allDetectedEntities = allPosts.flatMap { case (url, titles) =>
      println()
      titles.map { title =>
        val detected = Analyzer.detectEntities(title, dictionary)
        println(Formatters.formatNERResult(title, detected))
        println()
        detected
      }
    }

    // ------------------------------------------------------------------
    // Paso 4: Estadísticas globales
    // ------------------------------------------------------------------
    val stats = Analyzer.countByType(allDetectedEntities.flatten)
    println(Formatters.formatEntityStats(stats))

  }

  /*
    ¿Por qué flatMap y flatten?
    Lo que está pasando es que el map interno devuelve List[NamedEntity] 
    por cada título, entonces el map externo devuelve List[List[List[NamedEntity]]]. 
    El flatMap aplana un nivel, dejándote List[List[NamedEntity]], 
    y después flatten aplana el segundo nivel quedando List[NamedEntity].
  */
}
