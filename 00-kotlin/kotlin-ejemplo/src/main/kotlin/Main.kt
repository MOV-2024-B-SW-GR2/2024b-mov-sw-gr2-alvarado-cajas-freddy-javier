package org.example

import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    // Imutables (no se re asigna "=")
    val inmutable: String = "Freddy"
    //inmutable = "Vicente"
    //Mutables
    var mutable = "Adrin" // ok
    mutable = "Javier"
    // val > var

    // Duck Typing
    val ejemploVariable = "Adrian Eguez"
            ejemploVariable.trim()
    val edadEjemplo: Int = 12
    // ejemploVariable = edadEjemplo // Error!
    // Variables Primitivas
    val Profesor : String = "Adrian Eguez"
    val sueldo: Double = 1.2
    val estadoCivil: Char = 'C'
    val mayorEdad: Boolean = true
    // Clases en Java
    val fechaNacimiento: Date = Date()

    // When (Switch)
    val estadoCivilWhen = "C"
    when (estadoCivilWhen) {
        ("C") ->{
            println("Casado")
        }
        "S" -> {
             println("Soltero")
        }
        else -> {
             println("No sabemos")
        }
    }
    val esSoltero = (estadoCivilWhen == "S")
    val coqueteo = if (esSoltero) "Si" else "No" // if else chiquito

    imprimirNombre("Jose")

    calcularSueldo (10.00) // solo paramtro requerido
    calcularSueldo (10.00, 15.00,20.00) // parametro requerido y sobreescribir
    // Named parameters
    // calcularSueldo (sueldo, tasa, bonoEspecial)
    calcularSueldo (10.00, bonoEspecial = 20.00) // usando el parametro bonoEs
    // gracias a los parametros nombrados
    calcularSueldo (bonoEspecial = 20.00, sueldo= 10.00, tasa = 14.00)
    // usando el parametro bonoEspecial en 1ra posicion
    // usando el parametro sueldo en 2da posicion
    // usando el parametro tasa en 3era posicion
    // gracias a los parametros nombrados

    val SumaA = Suma(1,1)
    val SumaB = Suma(null, 1)
    val SumaC = Suma(1,null)
    val SumaD = Suma(null,null)
    SumaA.Sumar()
    SumaB.Sumar()
    SumaC.Sumar()
    SumaD.Sumar()
    println(Suma.pi)
    println(Suma.elevarAlCuadrado(2))
    println(Suma.historialSuma)


    // Arreglos
    // Estaticos
    val arregloEstatico: Array<Int> = arrayOf<Int>(1,2,3)
    println(arregloEstatico);
    // Dinamicos
    val arregloDinamico: ArrayList<Int> = arrayListOf<Int>(
        1,2,3,4,5,6,7,8,9,10
    )
    println(arregloDinamico)
    arregloDinamico.add(11)
    arregloDinamico.add(12)
    println(arregloDinamico)

    // FOR EACH => Unit
    // Iterar un arreglo
    val respuestaForEach: Unit = arregloDinamico
        .forEach { valorActual: Int ->
            println("Valor actual: ${valorActual}")
        }

    // "it" (en inglés "eso") significa el elemento iterado
    arregloDinamico.forEach { println("Valor Actual (it): ${it}") }

    // MAP -> MUTA (Modifica, cambia) el arreglo
    // 1) Enviamos el nuevo valor de la iteración
    // 2) Nos devuelve un NUEVO ARREGLO con valores de las iteraciones
    val respuestaMap: List<Double> = arregloDinamico
        .map { valorActual: Int ->
            return@map valorActual.toDouble() + 100.00
        }
    println(respuestaMap)

    val respuestaMapDos = arregloDinamico.map { it + 15 }
    println(respuestaMapDos)

    // Filter - Filtrar el ARREGLO
    // 1) Devolver una expresión (TRUE O FALSE)
    // 2) Nuevo arreglo FILTRADO
    val respuestaFilter: List<Int> = arregloDinamico
        .filter { valorActual: Int ->
            // Expresión o CONDICIÓN
            val mayoresACinco: Boolean = valorActual > 5
            return@filter mayoresACinco
        }

    val respuestaFilterDos = arregloDinamico.filter { it <= 5 }

    println(respuestaFilter)
    println(respuestaFilterDos)

    // OR y AND
    // OR - ANY (¿Alguno cumple?)
    val respuestaAny: Boolean = arregloDinamico.any { valorActual: Int ->
        return@any (valorActual > 5)
    }
    println(respuestaAny) // True

    // AND - ALL (¿Todos cumplen?)
    val respuestaAll: Boolean = arregloDinamico.all { valorActual: Int ->
        return@all (valorActual > 5)
    }
    println(respuestaAll) // False

    // REDUCE - Valor acumulado
    // Valor acumulado (Siempre empieza en 0 en Kotlin)
    // [1,2,3,4,5] Acumular "SUMAR" estos valores del arreglo
    // valor Iteracion1 = valorEmpieza + 1 = 0+1=1 Iteracion1
    // valor Iteracion2 = valorAcumulado Iteracion1+2=1+2=3 Iteracion2
    // valorIteracion3 = valorAcumulado Iteracion2+3=3+3=6 Iteracion3
    // valor Iteracion4 = valorAcumulado Iteracion3+4=6+4=10 Iteracion4
    // valorIteracion5 = valorAcumulado Iteracion4+5=10+5=15 Iteracion5
    val respuestaReduce: Int = arregloDinamico
        .reduce { acumulado: Int, valorActual: Int ->
            return@reduce (acumulado + valorActual) // Cambiar o usar la lógica de negocio
        }

    println(respuestaReduce)
    // return@reduce acumulado + (itemCarrito.cantidad * itemCarrito.precio)


}


fun imprimirNombre(nombre:String){
    fun otraFuncionAdentro(){
        println("Otra funcion adentro")
    }
    println("Nombre: $nombre")// Uso sin llaves
    println("Nombre: ${nombre}")// Uso con llaves opcional
    println("Nombre: ${nombre+nombre}")// Uso de llaves (concatenado)
    println("Nombre: ${nombre.toString()}")// Uso con llaves (funcion)
    println("Nombre: $nombre.toString()")// Incorrecto!

    otraFuncionAdentro()
}

fun calcularSueldo (
    sueldo: Double, // Requerido
    tasa: Double = 12.00, // Opcional (defecto)
    bonoEspecial: Double? = null // Opcional (nullable)
    // Variable? "?" Es Nullable (osea que puede en algun momento ser nulo)
): Double {
    // Int Int? (nullable)
    // String String? (nullable)
    // Date Date? (nullable)
    if (bonoEspecial == null) {
        return sueldo*(100 / tasa)
    } else {
        return sueldo*(100 / tasa) * bonoEspecial
    }
}

abstract class NumerosJava{
    protected val numeroUno:Int
    private val numeroDos:Int
    constructor(
        uno: Int,
        dos: Int
    ){
        this.numeroUno = uno
        this.numeroDos = dos
                println("Inicializando")
    }
}

abstract class Numeros( // Constructor Primario
// Caso 1) Parametro normal
// uno: Int, (parametro (sin modificador acceso))
// Caso 2) Parametro y propiedad (atributo) (protected)
// private var uno: Int (propiedad "instancia.uno")
    protected val numeroUno: Int, // instancia.numerouno
    protected val numeroDos: Int, // instancia.numeroDos
    parametroNoUsadoNoPropiedadDeLaClase: Int? = null
) {
    init { // bloque constructor primario OPCIONAL
        this.numeroUno
        this.numeroDos
        println("Inicializando")
    }
}

class Suma( // Constructor primario
        unoParametro: Int, // Parametro
        dosParametro: Int, // Parametros
    ): Numeros(// Close papa, Numeras (extendiendo)
        unoParametro,
        dosParametro
    ){
        public val soyPublicoExplicito: String = "Publicas"
        val souPublicaImplicito: String = "Publico Implicito"
        init { //Bloque constructor Primario
            this.numeroUno
            this.numeroDos
            numeroUno//this. Opcional [propiedades, metodos]
            numeroDos//this. Opcional [propiedades, metodos]
            this.souPublicaImplicito
            soyPublicoExplicito
        }
        constructor(//Constructor secundario
            uno: Int?, //Entero nullable
            dos: Int
        ):this(
            if (uno == null) 0 else uno,
            dos
        ){
            //Bloque de codigo de constructor secundario
        }
        constructor(//Constructor secundario
            uno: Int,
            dos: Int? //Entero nullable
        ):this(
            uno,
            if (dos == null) 0 else dos
        )
        constructor(//Constructor secundario
            uno: Int?, //Entero nullable
            dos: Int?
        ):this(
            if (uno == null) 0 else uno,
            if (dos == null) 0 else dos
        )

        fun Sumar ():Int{
            val total = numeroUno+numeroDos
            agregarHistorial(total)
            return total
        }
        companion object{//Comparte entre todas las instancias, similar a Static
            //funciones, variables
            //NombreClase.metodo, NombreClase.funcion =>
            //Suma.pi
            val pi=3.14
            //Suma.elevarAlCuadrado
            fun elevarAlCuadrado(num:Int):Int{return num*num}
            val historialSuma = arrayListOf<Int>()
            fun agregarHistorial(valorTotalSuma:Int){// Suma.agregarHistorial
                historialSuma.add(valorTotalSuma)
            }
        }

    }

