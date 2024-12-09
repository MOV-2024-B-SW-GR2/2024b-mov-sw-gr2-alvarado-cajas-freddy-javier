package org.example

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Artista(
    val nombre: String,
    val fechaNacimiento: LocalDate,
    val nacionalidad: String,
    val biografia: String,
    val premios: Int
)

data class Obra(
    val titulo: String,
    val artistaId: String,
    val fechaCreacion: LocalDate,
    val tecnica: String,
    val descripcion: String,
    val valorEstimado: Double
)

private const val ARCHIVO_ARTISTAS = "src/main/kotlin/archivos/artistas.txt"
private const val ARCHIVO_OBRAS = "src/main/kotlin/archivos/obras.txt"
private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun main() {
    crearArchivosNecesarios()
    mostrarMenu()
}

fun crearArchivosNecesarios() {
    File(ARCHIVO_ARTISTAS).createNewFile()
    File(ARCHIVO_OBRAS).createNewFile()
}

fun mostrarMenu() {
    while (true) {
        println("\n=== MENÚ PRINCIPAL ===")
        println("1. Gestionar Artistas")
        println("2. Gestionar Obras")
        println("3. Salir")
        println("Seleccione una opción: ")

        when (readLine()?.toIntOrNull() ?: 0) {
            1 -> gestionarArtistas()
            2 -> gestionarObras()
            3 -> return
            else -> println("Opción inválida")
        }
    }
}

fun gestionarArtistas() {
    while (true) {
        println("\n=== GESTIÓN DE ARTISTAS ===")
        println("1. Crear Artista")
        println("2. Ver Artistas")
        println("3. Actualizar Artista")
        println("4. Eliminar Artista")
        println("5. Ver Obras por Artista")
        println("6. Volver")
        println("Seleccione una opción: ")

        when (readLine()?.toIntOrNull() ?: 0) {
            1 -> crearArtista()
            2 -> mostrarArtistas()
            3 -> actualizarArtista()
            4 -> eliminarArtista()
            5 -> verObrasPorArtista()
            6 -> return
            else -> println("Opción inválida")
        }
    }
}

fun gestionarObras() {
    while (true) {
        println("\n=== GESTIÓN DE OBRAS ===")
        println("1. Crear Obra")
        println("2. Ver Obras")
        println("3. Actualizar Obra")
        println("4. Eliminar Obra")
        println("5. Volver")
        println("Seleccione una opción: ")

        when (readLine()?.toIntOrNull() ?: 0) {
            1 -> crearObra()
            2 -> mostrarObras()
            3 -> actualizarObra()
            4 -> eliminarObra()
            5 -> return
            else -> println("Opción inválida")
        }
    }
}

// Funciones para Artistas
fun crearArtista() {
    println("\nIngrese los datos del artista:")
    print("Nombre: ")
    val nombre = readLine() ?: return
    print("Fecha de nacimiento (dd/MM/yyyy): ")
    val fechaNacimiento = try {
        LocalDate.parse(readLine(), dateFormatter)
    } catch (e: Exception) {
        println("Fecha inválida")
        return
    }
    print("Nacionalidad: ")
    val nacionalidad = readLine() ?: return
    print("Biografía: ")
    val biografia = readLine() ?: return
    print("Número de premios: ")
    val premios = readLine()?.toIntOrNull() ?: return

    val artista = Artista(nombre, fechaNacimiento, nacionalidad, biografia, premios)
    guardarArtista(artista)
    println("Artista creado exitosamente")
}

fun mostrarArtistas() {
    val artistas = obtenerArtistas()
    if (artistas.isEmpty()) {
        println("No hay artistas registrados")
        return
    }

    println("\nLista de Artistas:")
    artistas.forEachIndexed { index, artista ->
        println("${index + 1}. ${artista.nombre} (${artista.nacionalidad})")
    }
}

fun actualizarArtista() {
    val artistas = obtenerArtistas()
    if (artistas.isEmpty()) {
        println("No hay artistas para actualizar")
        return
    }

    mostrarArtistas()
    print("\nSeleccione el número del artista a actualizar: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in artistas.indices) {
        println("Selección inválida")
        return
    }

    val artista = artistas[index]
    println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):")

    print("Nombre (${artista.nombre}): ")
    val nombre = readLine()?.takeIf { it.isNotBlank() } ?: artista.nombre

    print("Fecha de nacimiento (${artista.fechaNacimiento.format(dateFormatter)}): ")
    val fechaNacimiento = try {
        readLine()?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, dateFormatter) } ?: artista.fechaNacimiento
    } catch (e: Exception) {
        println("Fecha inválida, se mantiene la anterior")
        artista.fechaNacimiento
    }

    print("Nacionalidad (${artista.nacionalidad}): ")
    val nacionalidad = readLine()?.takeIf { it.isNotBlank() } ?: artista.nacionalidad

    print("Biografía (${artista.biografia}): ")
    val biografia = readLine()?.takeIf { it.isNotBlank() } ?: artista.biografia

    print("Premios (${artista.premios}): ")
    val premios = readLine()?.toIntOrNull() ?: artista.premios

    val artistaActualizado = Artista(nombre, fechaNacimiento, nacionalidad, biografia, premios)
    actualizarArtistaEnArchivo(artista, artistaActualizado)
    println("Artista actualizado exitosamente")
}

fun eliminarArtista() {
    val artistas = obtenerArtistas()
    if (artistas.isEmpty()) {
        println("No hay artistas para eliminar")
        return
    }

    mostrarArtistas()
    print("\nSeleccione el número del artista a eliminar: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in artistas.indices) {
        println("Selección inválida")
        return
    }

    val artista = artistas[index]
    val obras = obtenerObrasPorArtista(artista.nombre)
    if (obras.isNotEmpty()) {
        println("No se puede eliminar el artista porque tiene obras asociadas")
        return
    }

    eliminarArtistaDeArchivo(artista)
    println("Artista eliminado exitosamente")
}

fun verObrasPorArtista() {
    val artistas = obtenerArtistas()
    if (artistas.isEmpty()) {
        println("No hay artistas registrados")
        return
    }

    mostrarArtistas()
    print("\nSeleccione el número del artista: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in artistas.indices) {
        println("Selección inválida")
        return
    }

    val artista = artistas[index]
    val obras = obtenerObrasPorArtista(artista.nombre)
    if (obras.isEmpty()) {
        println("El artista no tiene obras registradas")
        return
    }

    println("\nObras de ${artista.nombre}:")
    obras.forEachIndexed { i, obra ->
        println("${i + 1}. ${obra.titulo} (${obra.fechaCreacion.format(dateFormatter)})")
    }
}

fun guardarArtista(artista: Artista) {
    File(ARCHIVO_ARTISTAS).appendText(
        "${artista.nombre},${artista.fechaNacimiento.format(dateFormatter)},${artista.nacionalidad},${artista.biografia},${artista.premios}\n"
    )
}

fun obtenerArtistas(): List<Artista> {
    return try {
        File(ARCHIVO_ARTISTAS).readLines().filter { it.isNotBlank() }.map { linea ->
            val partes = linea.split(",")
            Artista(
                nombre = partes[0],
                fechaNacimiento = LocalDate.parse(partes[1], dateFormatter),
                nacionalidad = partes[2],
                biografia = partes[3],
                premios = partes[4].toInt()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun actualizarArtistaEnArchivo(artistaAntiguo: Artista, artistaNuevo: Artista) {
    val artistas = obtenerArtistas().toMutableList()
    val index = artistas.indexOfFirst { it.nombre == artistaAntiguo.nombre }
    if (index != -1) {
        artistas[index] = artistaNuevo
        File(ARCHIVO_ARTISTAS).writeText("")
        artistas.forEach { guardarArtista(it) }
    }
}

fun eliminarArtistaDeArchivo(artista: Artista) {
    val artistas = obtenerArtistas().filter { it.nombre != artista.nombre }
    File(ARCHIVO_ARTISTAS).writeText("")
    artistas.forEach { guardarArtista(it) }
}

// Funciones para Obras
fun crearObra() {
    val artistas = obtenerArtistas()
    if (artistas.isEmpty()) {
        println("Debe existir al menos un artista para crear una obra")
        return
    }

    mostrarArtistas()
    print("\nSeleccione el número del artista para la obra: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in artistas.indices) {
        println("Selección inválida")
        return
    }

    val artista = artistas[index]
    println("\nIngrese los datos de la obra:")
    print("Título: ")
    val titulo = readLine() ?: return
    print("Fecha de creación (dd/MM/yyyy): ")
    val fechaCreacion = try {
        LocalDate.parse(readLine(), dateFormatter)
    } catch (e: Exception) {
        println("Fecha inválida")
        return
    }
    print("Técnica: ")
    val tecnica = readLine() ?: return
    print("Descripción: ")
    val descripcion = readLine() ?: return
    print("Valor estimado: ")
    val valorEstimado = readLine()?.toDoubleOrNull() ?: return

    val obra = Obra(titulo, artista.nombre, fechaCreacion, tecnica, descripcion, valorEstimado)
    guardarObra(obra)
    println("Obra creada exitosamente")
}

fun mostrarObras() {
    val obras = obtenerObras()
    if (obras.isEmpty()) {
        println("No hay obras registradas")
        return
    }

    println("\nLista de Obras:")
    obras.forEachIndexed { index, obra ->
        println("${index + 1}. ${obra.titulo} (Artista: ${obra.artistaId})")
    }
}

fun actualizarObra() {
    val obras = obtenerObras()
    if (obras.isEmpty()) {
        println("No hay obras para actualizar")
        return
    }

    mostrarObras()
    print("\nSeleccione el número de la obra a actualizar: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in obras.indices) {
        println("Selección inválida")
        return
    }

    val obra = obras[index]
    println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):")

    print("Título (${obra.titulo}): ")
    val titulo = readLine()?.takeIf { it.isNotBlank() } ?: obra.titulo

    print("Fecha de creación (${obra.fechaCreacion.format(dateFormatter)}): ")
    val fechaCreacion = try {
        readLine()?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, dateFormatter) } ?: obra.fechaCreacion
    } catch (e: Exception) {
        println("Fecha inválida, se mantiene la anterior")
        obra.fechaCreacion
    }

    print("Técnica (${obra.tecnica}): ")
    val tecnica = readLine()?.takeIf { it.isNotBlank() } ?: obra.tecnica

    print("Descripción (${obra.descripcion}): ")
    val descripcion = readLine()?.takeIf { it.isNotBlank() } ?: obra.descripcion

    print("Valor estimado (${obra.valorEstimado}): ")
    val valorEstimado = readLine()?.toDoubleOrNull() ?: obra.valorEstimado

    val obraActualizada = Obra(titulo, obra.artistaId, fechaCreacion, tecnica, descripcion, valorEstimado)
    actualizarObraEnArchivo(obra, obraActualizada)
    println("Obra actualizada exitosamente")
}

fun eliminarObra() {
    val obras = obtenerObras()
    if (obras.isEmpty()) {
        println("No hay obras para eliminar")
        return
    }

    mostrarObras()
    print("\nSeleccione el número de la obra a eliminar: ")
    val index = (readLine()?.toIntOrNull() ?: 0) - 1

    if (index !in obras.indices) {
        println("Selección inválida")
        return
    }

    val obra = obras[index]
    eliminarObraDeArchivo(obra)
    println("Obra eliminada exitosamente")
}

fun guardarObra(obra: Obra) {
    File(ARCHIVO_OBRAS).appendText(
        "${obra.titulo},${obra.artistaId},${obra.fechaCreacion.format(dateFormatter)},${obra.tecnica},${obra.descripcion},${obra.valorEstimado}\n"
    )
}

fun obtenerObras(): List<Obra> {
    return try {
        File(ARCHIVO_OBRAS).readLines().filter { it.isNotBlank() }.map { linea ->
            val partes = linea.split(",")
            Obra(
                titulo = partes[0],
                artistaId = partes[1],
                fechaCreacion = LocalDate.parse(partes[2], dateFormatter),
                tecnica = partes[3],
                descripcion = partes[4],
                valorEstimado = partes[5].toDouble()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun obtenerObrasPorArtista(artistaId: String): List<Obra> {
    return obtenerObras().filter { it.artistaId == artistaId }
}

fun actualizarObraEnArchivo(obraAntigua: Obra, obraNueva: Obra) {
    val obras = obtenerObras().toMutableList()
    val index = obras.indexOfFirst { it.titulo == obraAntigua.titulo && it.artistaId == obraAntigua.artistaId }
    if (index != -1) {
        obras[index] = obraNueva
        File(ARCHIVO_OBRAS).writeText("")
        obras.forEach { guardarObra(it) }
    }
}

fun eliminarObraDeArchivo(obra: Obra) {
    val obras = obtenerObras().filter { it.titulo != obra.titulo || it.artistaId != obra.artistaId }
    File(ARCHIVO_OBRAS).writeText("")
    obras.forEach { guardarObra(it) }
}