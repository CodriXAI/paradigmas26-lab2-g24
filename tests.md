# Códigos de Testing

## Environment y Ejecución:

Dichos códigos nos sirven para testear los ejercicios de laboratorio en consola utilizando:
```bash
sbt console
```

Y posteriormente:
```bash
:paste
```

Allí pegaremos nuestro código de testing y para ejecutarlo saldremos con **CTRL + D**

## Tests para Ejercicios:
---

## Ejercicio 1:
```scala
val entities: List[NamedEntity] = List(
    new Person("Alan Turing"),
    new University("MIT"),
    new ProgrammingLanguage("Scala"),
    new Place("San Francisco")
)
entities.foreach(e => println(e.describe))
```

## Salida Esperada:
```none
[Person] Alan Turing
[University] MIT
[ProgrammingLanguage] Scala
[Place] San Francisco
```

---

## Ejercicio 2:
```scala
val dict = Dictionary.loadAll()
println(s"Total de entidades: ${dict.size}")
dict.filter(_.entityType == "Person").foreach(p =>
println(p.describe))
```

## Salida Esperada:
```none
Total de entidades: 65
[Person] Martin Odersky
[Person] Alan Turing
[Person] Ada Lovelace
[Person] Linus Torvalds
[Person] Grace Hopper
[Person] Tim Berners-Lee
[Person] Guido van Rossum
[Person] Bjarne Stroustrup
[Person] James Gosling
[Person] Dennis Ritchie
[Person] John McCarthy
[Person] Donald Knuth
[Person] Charles Petzolds
```

---

## Ejercicio 3:
```scala
val text = "Scala fue creado en EPFL por Martin Odersky"
val dict = Dictionary.loadAll()
val found = Analyzer.detectEntities(text, dict)
found.foreach(e => println(e.describe))
```

## Salida Esperada:
```none
[ProgrammingLanguage] Scala
[University] EPFL
[Person] Martin Odersky
```

---

## Ejercicio 4:
```scala
val entities = List(
  new ProgrammingLanguage("Scala"),
  new University("EPFL"),
  new Person("Martin Odersky")
)

println(Formatters.formatNERResult("\"Scala 3 released at EPFL by Martin Odersky\"", entities))
```

## Salida Esperada:
```none
Post: "Scala 3 released at EPFL by Martin Odersky"

Entidades detectadas : 

  [ProgrammingLanguage] Scala

  [University] EPFL

  [Person] Martin Odersky
```

---

## Ejercicio 5:
```scala
val entities = List(
    new Person("Alan Turing"),
    new ProgrammingLanguage("Scala"),   
    new Person("Ada Lovelace"),
    new University("MIT")
)
val counts = Analyzer.countByType(entities)
Formatters.formatEntityStats(counts)

val entities = List()
val counts = Analyzer.countByType(entities)
Formatters.formatEntityStats(counts)
```

## Salida Esperada:
```none
=== Estadísticas de entidades ===
Person: 2
ProgrammingLanguage: 1
University: 1
```
---



