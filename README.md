# Named Entity Recognition — Lab 2

Sistema de reconocimiento de entidades nombradas escrito en Scala, desarrollado como parte de la materia Paradigmas de la Programación.

## Requisitos previos

### Instalar Scala

#### macOS (con Homebrew)
```bash
brew install coursier/formulas/coursier
cs setup
```

#### Linux
```bash
curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs
chmod +x cs
./cs setup
```

#### Windows
Descargar e instalar desde: https://www.scala-lang.org/download/

El comando `cs setup` instala el compilador de Scala, sbt y Java JDK (si no está instalado).

## Compilar y ejecutar

```bash
# Compilar
sbt compile

# Ejecutar
sbt run

# Modo interactivo
sbt
> run
```

## Estructura del proyecto

```
lab2/
├── build.sbt
├── consigna.md
├── data/
│   ├── people.txt           # personas conocidas
│   ├── universities.txt     # universidades
│   ├── languages.txt        # lenguajes de programación
│   ├── organizations.txt    # empresas y organizaciones
│   └── places.txt           # lugares
└── src/main/scala/
    ├── NamedEntity.scala    # jerarquía de entidades (completar)
    ├── Dictionary.scala     # carga de diccionarios (implementar)
    ├── Analyzer.scala       # detección y conteo (implementar)
    ├── Formatters.scala     # formateo de resultados (implementar)
    ├── FileIO.scala         # E/S de archivos y feeds (provisto)
    └── Main.scala           # punto de entrada (integrar)
```
