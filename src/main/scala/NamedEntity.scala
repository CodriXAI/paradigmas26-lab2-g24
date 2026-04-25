// =====================================================================
// Ejercicio 1: Modelar la jerarquía de entidades
// =====================================================================

/**
 * Clase base abstracta para todas las entidades nombradas.
 *
 * Una entidad nombrada es una expresión del texto que refiere a un objeto
 * del mundo real (persona, lugar, organización, tecnología, etc.).
 *
 * @param text el texto tal como aparece en el corpus
 */
abstract class NamedEntity(val text: String) {

  /**
   * Retorna el tipo de la entidad como String.
   * Ejemplo: "Person", "University", "ProgrammingLanguage"
   *
   * TODO (Ejercicio 1): Implementar en cada subclase concreta.
   */
  def entityType: String

  /**
   * Retorna una línea de descripción de la entidad para el informe.
   *
   * Al usar entityType aquí, este método funciona correctamente para cualquier
   * subclase sin necesidad de redefinirlo. Esto es polimorfismo.
   */
  def describe: String = s"[$entityType] $text"
}

// =====================================================================
// TODO (Ejercicio 1): Completar la jerarquía de entidades
//
// Implementar las clases faltantes.
//
// Jerarquía esperada:
//
//   NamedEntity
//   ├── Person
//   ├── Organization
//   │   └── University
//   ├── Place
//   └── Technology
//       └── ProgrammingLanguage
//
// Luego de implementar las clases, este código debe compilar:
//
//   val entities: List[NamedEntity] = List(
//     new Person("Alan Turing"),
//     new University("MIT"),
//     new ProgrammingLanguage("Scala"),
//     new Place("San Francisco")
//   )
//   entities.foreach(e => println(e.describe))
// =====================================================================
