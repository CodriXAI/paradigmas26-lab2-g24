# Guía: Testear con `sbt console`

## ¿Qué es `sbt console`?

`sbt console` abre la **Scala REPL** (Read-Eval-Print Loop) con tu proyecto ya compilado y disponible. Esto te permite probar clases y código de forma interactiva, sin necesidad de definir un `object Main`.

---

## Requisitos previos

Antes de abrir la consola, el proyecto debe **compilar sin errores**. Verificalo con:

```bash
sbt compile
```

Si hay errores, corregílos antes de continuar. Una vez que compila limpio:

```bash
sbt console
```

Deberías ver algo como:

```
[info] Starting scala interpreter...
Welcome to Scala 2.13.x
Type in expressions for evaluation. Or try :help.

scala>
```

---

## Uso básico

### Instanciar clases de tu proyecto

```scala
scala> val p = new Person("Alan Turing")
// p: Person = Person@...

scala> println(p.describe)
// [Person] Alan Turing

scala> val u = new University("MIT")
scala> println(u.describe)
// [University] MIT
```

### Probar listas y foreach

```scala
scala> val entities = List(new Person("Alan Turing"), new University("MIT"))
scala> entities.foreach(e => println(e.describe))
// [Person] Alan Turing
// [University] MIT
```

### Verificar tipos con isInstanceOf

```scala
scala> u.isInstanceOf[Organization]
// res0: Boolean = true

scala> u.isInstanceOf[NamedEntity]
// res1: Boolean = true
```

---

## Pegar bloques de código: `:paste`

Para ingresar **múltiples líneas** (listas, bloques, etc.) sin que la REPL las interprete línea por línea, usá el modo paste:

```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)
```

Pegá tu código:

```scala
val entities: List[NamedEntity] = List(
  new Person("Alan Turing"),
  new University("MIT"),
  new ProgrammingLanguage("Scala"),
  new Place("San Francisco")
)
entities.foreach(e => println(e.describe))
```

Luego presioná **Ctrl+D** para ejecutar. Resultado esperado:

```
[Person] Alan Turing
[University] MIT
[ProgrammingLanguage] Scala
[Place] San Francisco
```

---

## Errores comunes y soluciones

### El proyecto no compiló antes de abrir la consola

```
error: not found: type Person
```

**Solución:** Salir con `:quit`, correr `sbt compile` y volver a entrar con `sbt console`.

### Modifiqué un archivo `.scala` y los cambios no aparecen

La REPL carga las clases **al momento de abrirse**. Si modificás un archivo, tenés que:

```scala
scala> :quit
```

```bash
sbt console   # vuelve a compilar y cargar
```

### Error al pegar código multilínea sin `:paste`

Si pegás varias líneas directamente sin `:paste`, la REPL puede interpretar cada línea por separado y dar errores de sintaxis. **Siempre usá `:paste` para bloques**.

---

## Comandos útiles dentro de la REPL

| Comando | Descripción |
|---|---|
| `:paste` | Modo para pegar bloques multilínea (terminar con Ctrl+D) |
| `:quit` | Salir de la REPL |
| `:help` | Ver todos los comandos disponibles |
| `:type <expr>` | Ver el tipo inferido de una expresión |
| `:reset` | Reiniciar la sesión de la REPL (borra definiciones) |

Ejemplo de `:type`:

```scala
scala> :type new University("MIT")
// University
```

---

## Flujo de trabajo recomendado

```
1. Escribir/modificar código en src/main/scala/
        ↓
2. sbt compile   →   corregir errores si los hay
        ↓
3. sbt console
        ↓
4. Probar con :paste o línea por línea
        ↓
5. Si modificás algo → :quit → volver al paso 2
```

---

## Ejemplo completo del laboratorio

```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)

val entities: List[NamedEntity] = List(
  new Person("Alan Turing"),
  new University("MIT"),
  new ProgrammingLanguage("Scala"),
  new Place("San Francisco")
)

println("=== Entidades reconocidas ===")
entities.foreach(e => println(e.describe))

println("\n=== Verificación de jerarquía ===")
val mit = new University("MIT")
println(s"University es Organization: ${mit.isInstanceOf[Organization]}")
println(s"University es NamedEntity:  ${mit.isInstanceOf[NamedEntity]}")

// Ctrl+D
```

Salida esperada:

```
=== Entidades reconocidas ===
[Person] Alan Turing
[University] MIT
[ProgrammingLanguage] Scala
[Place] San Francisco

=== Verificación de jerarquía ===
University es Organization: true
University es NamedEntity:  true
```