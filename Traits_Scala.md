# Traits en Scala

## ¿Qué es un trait?

Un trait define comportamiento que puede mezclarse en múltiples clases, sin importar dónde estén en la jerarquía. A diferencia de una clase abstracta, una clase puede mezclar varios traits a la vez pero solo puede extender una clase.

Regla práctica:
- **Clase abstracta** → relación de identidad ("X es un Y")
- **Trait** → capacidad o rol ("X sabe hacer Z")

---

## Definición básica

```scala
trait Summarizable {
  def summary: String                              // abstracto: cada clase lo implementa
  def preview: String = s"Preview: ${summary}"    // concreto: reutilizable por todas
}
```

---

## Mezclar un trait en una clase

```scala
class Person(text: String) extends NamedEntity(text) with Summarizable {
  def entityType: String = "Person"
  def summary: String = s"Persona conocida: $text"
  // preview ya viene resuelto desde el trait
}

class University(text: String) extends Organization(text) with Summarizable {
  override def entityType: String = "University"
  def summary: String = s"Universidad: $text"
}
```

---

## Múltiples traits en una misma clase

Una clase puede mezclar tantos traits como necesite. Con clases abstractas esto es imposible.

```scala
class University(text: String) extends Organization(text)
    with Summarizable
    with Exportable
    with Rankable
```

---

## Polimorfismo con traits

Podés operar sobre una lista heterogénea de objetos que comparten un trait, sin `match` ni `isInstanceOf`.

```scala
val entities: List[NamedEntity] = Dictionary.loadAll()

entities.collect { case s: Summarizable => s }
        .foreach(s => println(s.summary))
// Persona conocida: Martin Odersky
// Universidad: MIT
// Persona conocida: Ada Lovelace
```

`collect` filtra solo las entidades que implementan `Summarizable` y las trata uniformemente.

---

## Cuándo usar trait por encima de clase abstracta

| Situación | Usar |
|---|---|
| "X es un Y" (jerarquía de identidad) | Clase abstracta |
| "X sabe hacer Z" (capacidad o rol) | Trait |
| Compartir comportamiento entre ramas distintas del árbol | Trait |
| Una clase necesita comportamiento de múltiples fuentes | Trait |

En la jerarquía de `NamedEntity`, `University` es una `Organization` → herencia.
Pero `Summarizable` no es algo que una entidad "sea", es algo que "sabe hacer" → trait.

Si quisieras que `Person` y `University` compartan comportamiento usando solo herencia,
tendrías que subirlo a `NamedEntity`, forzándolo sobre `Place` y `ProgrammingLanguage`
aunque no tenga sentido. El trait lo mezclás solo donde corresponde.

---

## Un trait puede extender otro trait

```scala
trait Printable {
  def print: String
}

trait Summarizable extends Printable {
  def summary: String
  def print: String = s"[resumen] ${summary}"  // implementa print usando summary
}

class Person(text: String) extends NamedEntity(text) with Summarizable {
  def entityType: String = "Person"
  def summary: String = s"Persona conocida: $text"
  // print ya viene resuelto desde Summarizable
}

val p = new Person("Alan Turing")
println(p.summary) // Persona conocida: Alan Turing
println(p.print)   // [resumen] Persona conocida: Alan Turing
```

Cualquier clase que mezcle `Summarizable` hereda `print` automáticamente
y queda obligada a implementar `summary`.

---

## Atributos en un trait

Un trait puede declarar atributos, tanto abstractos como concretos.

### Atributo abstracto

La clase que mezcla el trait está obligada a proveer el valor.

```scala
trait Categorizable {
  val category: String          // abstracto: sin valor asignado
  def describe: String = s"Categoría: $category"
}

class Person(text: String) extends NamedEntity(text) with Categorizable {
  def entityType: String = "Person"
  val category: String = "Humano"  // obligatorio implementarlo
}

val p = new Person("Alan Turing")
println(p.describe) // Categoría: Humano
```

### Atributo concreto

El trait provee un valor por defecto que la clase puede usar o pisar con `override`.

```scala
trait Categorizable {
  val category: String = "Sin categoría"   // concreto: valor por defecto
}

class Person(text: String) extends NamedEntity(text) with Categorizable {
  def entityType: String = "Person"
  // usa "Sin categoría" tal cual, o puede pisarlo:
  override val category: String = "Humano"
}
```

### Atributo `var` (mutable)

Un trait también puede declarar atributos mutables, aunque en programación funcional se evita.

```scala
trait Contador {
  var count: Int = 0
  def increment(): Unit = { count += 1 }
}

class Person(text: String) extends NamedEntity(text) with Contador {
  def entityType: String = "Person"
}

val p = new Person("Alan Turing")
p.increment()
println(p.count) // 1
```

### Diferencia clave con los métodos

Un atributo en un trait se inicializa **cuando la clase que lo mezcla es instanciada**,
no cuando el trait es definido. Esto puede generar un problema si un método concreto
del trait intenta usar el atributo abstracto antes de que la clase lo inicialice.

```scala
trait Categorizable {
  val category: String
  val label: String = s"[$category]"  // ⚠️ peligro: category aún no fue inicializado
}

class Person(text: String) extends NamedEntity(text) with Categorizable {
  val category: String = "Humano"
}

val p = new Person("Alan Turing")
println(p.label) // puede imprimir "[null]" en lugar de "[Humano]"
```

La solución es usar `def` en lugar de `val` en el trait cuando el valor depende
de algo abstracto, o declarar el atributo como `lazy val`:

```scala
trait Categorizable {
  val category: String
  lazy val label: String = s"[$category]"  // se evalúa recién cuando se accede
}
// Ahora p.label == "[Humano]" correctamente
```

---

## Linearización (resolución de conflictos)

Cuando dos traits definen el mismo método, Scala usa **linearización** para decidir
cuál tiene prioridad. La regla general: el trait más a la derecha en la declaración
gana, y se sube la cadena hacia la izquierda.

```scala
trait A {
  def mensaje: String = "A"
}

trait B extends A {
  override def mensaje: String = "B"
}

trait C extends A {
  override def mensaje: String = "C"
}

class MiClase extends A with B with C
// C está más a la derecha → gana
// new MiClase().mensaje == "C"
```
