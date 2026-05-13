// =====================================================================
// Ejercicios 3 y 5: Detección y conteo de entidades
// =====================================================================
import java.text.Normalizer
import java.text.Normalizer.Form
/**
 * Responsable de detectar entidades nombradas en texto libre y
 * producir estadísticas sobre ellas.
 */
object Analyzer {

  /**
   * Se encarga de filtrar carácteres especiales.
   * @param s cadena a normalizar
   * @return texto libre de carácteres especiales y en minúscula
   */ 
  def normalize(s: String): String = {
    val sinTildes = Normalizer.normalize(s, Form.NFD)
    .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
    sinTildes.toLowerCase.replaceAll("[^a-z0-9]", " ").replaceAll("\\s+", " ").trim
  }

  /**
   * Se encarga de detectar palabras "excepcionales" como c++
   *
   * @param s cadena a detectar carácteres
   * @return True si habia carácteres especiales, False caso contrario
   */ 
  def hasSpecialChars(s: String): Boolean =
    s.exists(c => !c.isLetterOrDigit && !c.isWhitespace)

  /**
   * Tokeniza la frase ingresada, diviendola en palabras
   *
   * @param s cadena a tokenizar
   * @return Lista con los tokens
   */ 
  def tokenize(s: String): List[String] =
    s.split("\\s+").toList.filter(_.nonEmpty)

  /**
   * Verifica si una secuencia de tokens aparece de forma consecutiva
   * dentro de otra secuencia de tokens.
   *
   * Genera todas las subslistas contiguas del tamaño de entityTokens
   * y verifica si alguna coincide exactamente.
   *
   * Ejemplo:
   *   textTokens   = ["martin", "odersky", "creó", "scala"]
   *   entityTokens = ["martin", "odersky"]
   *   resultado    = true  (aparecen consecutivos en posición 0 y 1)
   *
   * @param textTokens   tokens del texto a analizar
   * @param entityTokens tokens de la entidad a buscar
   * @return True si la secuencia aparece consecutiva, False en caso contrario
   */
  def containsTokenSequence(textTokens: List[String], entityTokens: List[String]): Boolean =
    textTokens.sliding(entityTokens.length).contains(entityTokens)


  /**
   * Detecta las entidades del diccionario que aparecen en el texto dado.
   *
   * @param text       texto a analizar (ej: título o cuerpo de un post)
   * @param dictionary lista de entidades conocidas (cargadas desde los diccionarios)
   * @return lista de entidades cuyo texto aparece en el texto analizado
   *
   * TODO (Ejercicio 3): Implementar este método.
   *
   *   Para cada entidad en el diccionario, verificar si su texto aparece en el
   *   texto del post. Retornar únicamente las entidades que aparecen.
   *
   *   Ejemplo:
   *     text       = "Scala fue creado en EPFL por Martin Odersky"
   *     dictionary = List(
   *                    ProgrammingLanguage("Scala"),
   *                    University("EPFL"),
   *                    Person("Martin Odersky"),
   *                    Person("Ada Lovelace")   ← no aparece en el texto
   *                  )
   *     resultado  = List(
   *                    ProgrammingLanguage("Scala"),
   *                    University("EPFL"),
   *                    Person("Martin Odersky")
   *                  )
   */
  def detectEntities(text: String, dictionary: List[NamedEntity]): List[NamedEntity] = {
    // Almacena el 'e' en special si hasSpecialChars retorna True, en normal si retorna False
    val (special, normal) = dictionary.partition(e => hasSpecialChars(e.text))

    // Excepciones con texto solo "lowercaseado" ej: c++
    val textTokensLower = tokenize(text.toLowerCase)

    // Búsqueda de Palabras especiales en special
    val foundSpecial = special.filter(e =>
      containsTokenSequence(textTokensLower, tokenize(e.text.toLowerCase))
    )

    // Normales con texto completamente normalizado ej: mit
    val textTokensNorm = tokenize(normalize(text))

    // Búsqueda de Palabras Normales en normal
    val foundNormal = normal.filter(e =>
      containsTokenSequence(textTokensNorm, tokenize(normalize(e.text)))
    )

    foundSpecial ++ foundNormal
  }

  /** Ejemplo de ejecución detallado:
  *
  *   text       = "Scala fue creado por Martin Odersky y usa C++"
  *   dictionary = List(
  *                  Person("Martin Odersky"),
  *                  ProgrammingLanguage("Scala"),
  *                  ProgrammingLanguage("C++"),
  *                  Person("Ada Lovelace")
  *                )
  *
  *   Paso 1: partition separa el diccionario en dos listas según si tienen caracteres especiales:
  *     special = [ProgrammingLanguage("C++")]
  *     normal  = [Person("Martin Odersky"), ProgrammingLanguage("Scala"), Person("Ada Lovelace")]
  *
  *   Paso 2: buscar especiales con texto solo lowercaseado y tokenizado:
  *     textTokensLower = ["scala", "fue", "creado", "por", "martin", "odersky", "y", "usa", "c++"]
  *     C++ → tokenize("c++") = ["c++"] → sliding(1) encuentra "c++" → ✓
  *     foundSpecial = [ProgrammingLanguage("C++")]
  *
  *   Paso 3: buscar normales con texto completamente normalizado y tokenizado:
  *     textTokensNorm = ["scala", "fue", "creado", "por", "martin", "odersky", "y", "usa", "c"]
  *     (nótese que c++ se convirtió en c, pero ya fue encontrado en el paso anterior)
  *
  *     Para cada entidad en normal se normaliza y tokeniza su texto en el momento de comparar:
  *       Scala         → normalize("Scala")         = "scala"         → tokenize = ["scala"]
  *                     → sliding(1) encuentra "scala" → ✓
  *       Martin Odersky → normalize("Martin Odersky") = "martin odersky" → tokenize = ["martin", "odersky"]
  *                     → sliding(2) encuentra ["martin", "odersky"] → ✓
  *       Ada Lovelace  → normalize("Ada Lovelace")  = "ada lovelace"  → tokenize = ["ada", "lovelace"]
  *                     → sliding(2) no encuentra ["ada", "lovelace"] → ✗
  *     foundNormal = [ProgrammingLanguage("Scala"), Person("Martin Odersky")]
  *
  *   Resultado final: foundSpecial ++ foundNormal =
  *     List(ProgrammingLanguage("C++"), ProgrammingLanguage("Scala"), Person("Martin Odersky"))
  */

  /**
   * Cuenta cuántas entidades de cada tipo fueron detectadas.
   *
   * @param entities lista de entidades detectadas
   * @return mapa de entityType → cantidad de apariciones
   *
   * TODO (Ejercicio 5): Implementar este método.
   *
   *   Ejemplo:
   *     entities = List(
   *                  Person("Alan Turing"),
   *                  ProgrammingLanguage("Scala"),
   *                  Person("Ada Lovelace"),
   *                  University("MIT")
   *                )
   *     resultado = Map(
   *                   "Person"              -> 2,
   *                   "ProgrammingLanguage" -> 1,
   *                   "University"          -> 1
   *                 )
   */
  def countByType(entities: List[NamedEntity]): Map[String, Int] = {
    ???
  }
}
