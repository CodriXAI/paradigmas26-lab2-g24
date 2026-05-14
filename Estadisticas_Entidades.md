# Estadísticas de entidades — Deduplicación y conteo

## Contexto del problema

Cuando se recorren múltiples posts, `detectEntities` puede retornar la misma entidad
varias veces: si "Scala" aparece en 18 títulos distintos, se acumula 18 veces en
`allDetectedEntities`. Esto afecta tanto el conteo por tipo como cualquier estadística
global.

Hay dos métricas útiles y distintas sobre ese dato:

- **Cuántos tipos distintos encontraste** (sin importar repeticiones)
- **Cuántas veces apareció cada entidad** a lo largo de todos los posts

---

## Eliminar duplicados antes de contar por tipo

Si lo que querés es saber cuántas entidades *distintas* aparecieron, deduplicás
antes de pasarle la lista a `countByType`. La herramienta es `distinctBy`:

```scala
val stats = Analyzer.countByType(allDetectedEntities.distinctBy(_.text))
```

`distinctBy(_.text)` conserva solo la primera aparición de cada texto, descartando
las repetidas. Si "Scala" apareció 18 veces, queda una sola.

Si deduplicaras por entityType en cambio, te quedarías con una sola entidad de cada tipo, lo cual perdería información: no sabrías que encontraste tanto "Scala" como "Python" como lenguajes.

```scala
// Sin distinctBy:
// Map("ProgrammingLanguage" -> 18, "Person" -> 2, "University" -> 1)  → 21 entidades

// Con distinctBy:
// Map("ProgrammingLanguage" -> 1, "Person" -> 2, "University" -> 1)   → 4 entidades
```

---

## Contar cuántas veces apareció cada entidad

Si en cambio querés saber cuántas veces se mencionó cada entidad concreta
a lo largo de todos los posts, contás por `text` sin deduplicar:

```scala
def countByDescribe(entities: List[NamedEntity]): Map[String, Int] =
  entities.groupBy(_.text).map { case (text, group) => text -> group.size }
```

`groupBy(_.text)` agrupa todas las instancias con el mismo texto en una sublista,
y luego se toma el tamaño de cada grupo.

```scala
val entities = List(
  new ProgrammingLanguage("Scala"),
  new ProgrammingLanguage("Scala"),
  new ProgrammingLanguage("Python")
)

countByDescribe(entities)
// Map("Scala" -> 2, "Python" -> 1)
```

---

## Formatear el resultado ordenado

Para mostrar el conteo de mayor a menor en `Formatters`:

```scala
def formatDescribeStats(counts: Map[String, Int]): String =
  counts.toList
    .sortBy(-_._2)
    .map { case (text, count) => s"$text: $count" }
    .mkString("\n")
```

Salida esperada:

```
Scala: 2
Python: 1
```

---

## Diferencia entre las dos métricas

| Métrica | Cómo se obtiene | Para qué sirve |
|---|---|---|
| Tipos distintos encontrados | `countByType` con `distinctBy(_.text)` | Saber qué clases de entidades aparecen en el corpus |
| Frecuencia de cada entidad | `countByDescribe` sin deduplicar | Saber cuáles entidades se mencionan más |

Son complementarias: la primera dice *qué* encontraste, la segunda dice *cuánto*
se habla de cada cosa.

---

## Por qué `distinctBy` y no `distinct`

`distinct` compara por igualdad estructural (`equals`). Como las clases del proyecto
no sobreescriben `equals`, dos instancias `new ProgrammingLanguage("Scala")` son
objetos distintos aunque tengan el mismo texto, y `distinct` no las eliminaría.

`distinctBy(_.text)` compara por el campo que te importa, sin necesidad de tocar
las clases.

```scala
val lista = List(
  new ProgrammingLanguage("Scala"),
  new ProgrammingLanguage("Scala")
)

lista.distinct        // List(ProgrammingLanguage, ProgrammingLanguage) — no deduplicó
lista.distinctBy(_.text) // List(ProgrammingLanguage)                  — correcto
```
