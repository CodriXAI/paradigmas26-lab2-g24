# Análisis de `Analyzer.scala` — Chat completo

---

## Pregunta 1: Explicame el método `detectEntities`

---

### `detectEntities` al detalle

El método recibe un `text` (texto libre) y un `dictionary` (lista de entidades conocidas), y retorna solo las entidades que **aparecen** en el texto.

---

### Funciones auxiliares involucradas

**`hasSpecialChars(s: String): Boolean`**
```scala
s.exists(c => !c.isLetterOrDigit && !c.isWhitespace)
```
Recorre cada carácter del string. Si encuentra alguno que **no** sea letra, dígito ni espacio (ej: `+`, `#`, `.`), retorna `true`. Sirve para detectar casos como `C++`, `C#`, `Node.js`.

---

**`normalize(s: String): String`**
```scala
val sinTildes = Normalizer.normalize(s, Form.NFD)
  .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
sinTildes.toLowerCase.replaceAll("[^a-z0-9]", " ").replaceAll("\\s+", " ").trim
```
Hace tres cosas en cadena:
1. `NFD` descompone los caracteres con tilde en letra base + diacrítico (`á` → `a` + `´`), y luego el regex elimina esos diacríticos sueltos → quita tildes
2. `.toLowerCase` → todo minúscula
3. `[^a-z0-9]` reemplaza todo lo que **no** sea letra o número por espacio, y `\\s+` colapsa espacios múltiples

Resultado: `"Martín Odérsky C++"` → `"martin odersky c"` (el `++` desaparece, por eso los especiales se tratan aparte)

---

**`tokenize(s: String): List[String]`**
```scala
s.split("\\s+").toList.filter(_.nonEmpty)
```
Divide el string por uno o más espacios y descarta tokens vacíos. Simple split en palabras.

---

**`containsTokenSequence(textTokens, entityTokens): Boolean`**
```scala
textTokens.sliding(entityTokens.length).contains(entityTokens)
```
`.sliding(n)` genera todas las **ventanas contiguas de tamaño n** sobre la lista. Por ejemplo:
```
["martin", "odersky", "creo", "scala"].sliding(2) →
  ["martin", "odersky"]
  ["odersky", "creo"]
  ["creo", "scala"]
```
Luego `.contains(entityTokens)` verifica si alguna ventana coincide exactamente con los tokens de la entidad. Esto garantiza que `"martin odersky"` no matchee en `"martin es odersky"`.

---

### El flujo de `detectEntities`

```scala
val (special, normal) = dictionary.partition(e => hasSpecialChars(e.text))
```
**Paso 1 — Separar el diccionario en dos grupos:**
- `special`: entidades con chars especiales (`C++`, `C#`)
- `normal`: el resto (`Scala`, `Martin Odersky`, `MIT`)

La razón: `normalize` destruiría `C++` → `c`, así que estas entidades necesitan un camino diferente.

---

```scala
val textTokensLower = tokenize(text.toLowerCase)

val foundSpecial = special.filter(e =>
  containsTokenSequence(textTokensLower, tokenize(e.text.toLowerCase))
)
```
**Paso 2 — Buscar especiales:**
El texto se tokeniza solo en **minúscula** (sin normalizar, para preservar `++`). Cada entidad especial también se pasa solo a lowercase. Luego se busca la secuencia de tokens en el texto.

`"usa C++"` → lowercase → `["usa", "c++"]` → `c++` encontrado ✓

---

```scala
val textTokensNorm = tokenize(normalize(text))

val foundNormal = normal.filter(e =>
  containsTokenSequence(textTokensNorm, tokenize(normalize(e.text)))
)
```
**Paso 3 — Buscar normales:**
Ahora sí se normaliza completamente tanto el texto como cada entidad. Esto permite que `"Martín"` matchee con `"Martin"`, o `"EPFL"` con `"epfl"`.

---

```scala
foundSpecial ++ foundNormal
```
**Paso 4 — Unir resultados.** Los especiales encontrados primero, los normales después.

---

### Resumen visual del flujo

```
dictionary
    │
    ├── hasSpecialChars → special (C++, C#...)
    │       │
    │       └── text.toLowerCase + tokenize
    │               └── containsTokenSequence → foundSpecial
    │
    └── normal (Scala, MIT, Martin Odersky...)
            │
            └── normalize(text) + tokenize
                    └── containsTokenSequence → foundNormal

resultado = foundSpecial ++ foundNormal
```

La clave del diseño es ese **split en dos caminos**: los chars especiales se preservan con solo lowercase, los normales pasan por normalización completa para tolerar tildes, mayúsculas y variaciones.

---

## Pregunta 2: ¿Es el mejor camino para implementar esto?

---

### El problema real

El doble camino existe porque `normalize` es **demasiado agresiva** — elimina todo lo que no sea `[a-z0-9]`, incluyendo `+`, `#`, `.`. Eso fuerza a bifurcar la lógica como workaround.

Hay además un **bug silencioso**: una entidad con tildes Y caracteres especiales (ej: `"Añadir++"`) cae en `special`, se procesa solo con lowercase, y pierde la normalización de tildes.

---

### La solución más simple

Separar las responsabilidades de `normalize` en dos funciones con propósito claro:

```scala
// Solo elimina tildes y pasa a minúscula. No toca nada más.
def normalizeSoft(s: String): String = {
  Normalizer.normalize(s, Form.NFD)
    .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
    .toLowerCase
    .replaceAll("\\s+", " ")
    .trim
}

// Tokeniza sobre el texto soft-normalizado
def tokenize(s: String): List[String] =
  s.split("\\s+").toList.filter(_.nonEmpty)
```

Con eso, `detectEntities` colapsa a **un solo camino**:

```scala
def detectEntities(text: String, dictionary: List[NamedEntity]): List[NamedEntity] = {
  val textTokens = tokenize(normalizeSoft(text))

  dictionary.filter { e =>
    containsTokenSequence(textTokens, tokenize(normalizeSoft(e.text)))
  }
}
```

---

### Por qué funciona igual (o mejor)

| Entidad | Original | `normalizeSoft` |
|---|---|---|
| `C++` | destruida por normalize | `c++` ✓ |
| `Martin Odersky` | `martin odersky` | `martin odersky` ✓ |
| `Martín` | `martin` | `martin` ✓ |
| `Añadir++` | bug (perdía tildes) | `anadir++` ✓ |

El único caso que se pierde es si el **texto fuente** escribe `C ++` con espacio, pero eso es un problema de datos, no de lógica.

---

### En resumen

El diseño original resuelve bien el problema pero **atacó el síntoma** (bifurcando paths) en lugar de la causa (`normalize` hacía demasiado). Una sola función de normalización más conservadora elimina toda esa complejidad accidental.