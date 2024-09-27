package org.example

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max


fun main() {
    val rutaFichero = Path.of("src/main/resources/calificaciones.csv")
    val alumnos = readFile(rutaFichero)
    println(alumnos)

    val alumnosFinal = insertarNota(alumnos)
    println(alumnosFinal)

    val (aprobados,suspensos) = aprobadosYsuspensos(alumnosFinal)
    println("Aprobados: ${aprobados}\n Suspensos: ${suspensos} ")

}

fun readFile(fichero : Path) : List<MutableMap<String,List<Double>>>{

    val diccionaries : MutableList<MutableMap<String,List<Double>>> = mutableListOf()
    val br : BufferedReader = Files.newBufferedReader(fichero)
    //para saltar la primera línea
    var firstLineRead = false

    br.use{

        for (line in br.lines()){
            //Para saltar la primera línea
            if (!firstLineRead){
                firstLineRead = true
                continue
            }
            //Declaro la variable para cada mapa
            val currentStudent : MutableMap<String,List<Double>> = mutableMapOf()
            //Esta contiene los datos separados
            val studentInfo = line.split(";")
            //Esta solamente contiene nombre y apellido, que sería la clave del mapa
            val studentName = studentInfo[0] + "," + studentInfo[1]
            //Slice corta la lista para que no incluya nombre y apellido y llega hasta el final -1
            //.map mapea el elemento, en este caso a double
            //Uso operador elvis para los casos en los que hay datos vacios, devuelve -1 q significa que no ha hecho ese exam
            currentStudent[studentName] = studentInfo
                .slice(2..<studentInfo.size)
                .map {
                    x -> x.replace("%", "")
                    .replace(",",".")
                    .toDoubleOrNull()?:-1.0
                }
            //se añade a la lista los diccionarios
            diccionaries.add(currentStudent)
        }
    }
    return diccionaries.sortedBy { it.keys.first() }
}

fun insertarNota(lista: List<MutableMap<String, List<Double>>>): List<MutableMap<String, List<Double>>> {

    for(map in lista){
        //Variable que toma el valor de la primera lista de notas del alumno actual
        val notasAlumnoActual = map.values.first()
        //Aqui se queda con la nota mayor, ya sea exam normal o recuperación
        val parcial1 = max(notasAlumnoActual[1], notasAlumnoActual[3])
        val parcial2 = max(notasAlumnoActual[2],notasAlumnoActual[4])
        val practicas = max(notasAlumnoActual[5],notasAlumnoActual[6])
        val notaFinal = (parcial1 * 0.3) + (parcial2 * 0.3) + (practicas * 0.4)

        map["NotaFinal"] = listOf(notaFinal)
    }
    return lista
}

fun aprobadosYsuspensos (lista : List<MutableMap<String, List<Double>>>): Pair<List<String>,List<String>>{
    //variables de listas para aprobados o suspensos
    var aprobados : MutableList<String> = mutableListOf()
    var suspensos : MutableList<String> = mutableListOf()
    //itero en la lista de mapas
    for(map in lista){
        val notas = map.values.first()
        val asistencia = notas[0]
        val parcial1 = max(notas[1],notas[3])
        val parcial2 = max(notas[2],notas[3])
        val practica = max(notas[5],notas[6])
        val notaFinal = map.values.elementAt(1)[0]

        if(asistencia >= 75 && parcial1 >=4 && parcial2 >=4 && practica >=4 && notaFinal >= 5.0){
            aprobados.add(map.keys.first())
        }else{
            suspensos.add(map.keys.first())
        }
    }
    return Pair(aprobados,suspensos)
}