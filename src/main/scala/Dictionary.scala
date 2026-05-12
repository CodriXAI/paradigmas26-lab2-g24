// =====================================================================
// Ejercicio 2: Cargar diccionarios de entidades
// =====================================================================
import FileIO._

/**
 * Responsable de cargar colecciones de entidades nombradas desde archivos.
 *
 * Un diccionario es un archivo de texto plano donde cada línea contiene
 * el nombre de una entidad conocida del mismo tipo.
 *
 * Ejemplo — data/people.txt:
 *   Martin Odersky
 *   Alan Turing
 *   Ada Lovelace
 *
 * Ejemplo — data/languages.txt:
 *   Scala
 *   Python
 *   Haskell
 */
object Dictionary {

  /**
   * Lee un archivo de diccionario y crea una lista de entidades del tipo indicado.
   *
   * @param filePath   ruta al archivo de diccionario (ej: "data/people.txt")
   * @param entityType tipo de entidad: "Person", "University", "ProgrammingLanguage", etc.
   * @return lista de NamedEntity del tipo correspondiente
   *
   * TODO (Ejercicio 2): Implementar este método.
   *
   *   Pasos sugeridos:
   *     1. Leer las líneas del archivo
   *     2. Para cada línea, crear la instancia de la clase correcta
   *     3. Retornar la lista de entidades creadas
   *
   *   Para crear la clase correcta según el tipo se puede usar match:
   *
   */
  def loadFromFile(filePath: String, entityType: String): List[NamedEntity] = {
    readLines(filePath).map{
      s => entityType match {
        case "Person" => new Person(s)
        case "Organization" => new Organization(s)
        case "University" => new University(s)
        case "Place" => new Place(s)
        case "Technology" => new Technology(s)
        case "ProgrammingLanguage" => new ProgrammingLanguage(s)
      }
    }
  }

  /**
   * Carga todos los diccionarios disponibles y combina sus entidades.
   *
   * @return lista con todas las entidades de todos los diccionarios
   *
   * TODO (Ejercicio 2): Implementar este método.
   *
   */
  def loadAll(): List[NamedEntity] = {
    val pathList: Set[(String, String)] = Set (
      ("data/languages.txt", "ProgrammingLanguage"),
      ("data/organizations.txt", "Organization"),
      ("data/people.txt", "Person"),
      ("data/places.txt", "Place"),
      ("data/universities.txt", "University")
    )

    val allEntities = pathList.foldLeft(List[NamedEntity]())(
      (listSum, pathEntity) 
      => 
      listSum ++ loadFromFile(pathEntity._1, pathEntity._2)
    )
    allEntities
  }
}
